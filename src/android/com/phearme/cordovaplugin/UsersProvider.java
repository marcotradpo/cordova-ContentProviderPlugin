/*
 * Copyright (C) 2013 Wolfram Rittmeyer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.phearme.cordovaplugin;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import com.phearme.cordovaplugin.UsersContract.Users;

/**
 * The actual provider class for the users provider. Clients do not use it directly. Nor
 * do they see it.
 *
 * @author Wolfram Rittmeyer
 */
public class UsersProvider extends ContentProvider {

	// helper constants for use with the UriMatcher
	private static final int ITEM_LIST = 1;
	private static final int ITEM_ID = 2;
   private static final UriMatcher URI_MATCHER;

	private UsersOpenHelper mHelper = null;
   private final ThreadLocal<Boolean> mIsInBatchMode = new ThreadLocal<Boolean>();

	// prepare the UriMatcher
	static {
		URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
		URI_MATCHER.addURI(UsersContract.AUTHORITY, "users", ITEM_LIST);
		URI_MATCHER.addURI(UsersContract.AUTHORITY, "users/#", ITEM_ID);
	}

	@Override
	public boolean onCreate() {
		mHelper = new UsersOpenHelper(getContext());
		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
	   doAnalytics(uri, "delete");

		SQLiteDatabase db = mHelper.getWritableDatabase();
		int delCount = 0;
		switch (URI_MATCHER.match(uri)) {
		case ITEM_LIST:
			delCount = db.delete(DbSchema.TBL_USERS, selection, selectionArgs);
			break;
		case ITEM_ID:
			String idStr = uri.getLastPathSegment();
			String where = Users._ID + " = " + idStr;
			if (!TextUtils.isEmpty(selection)) {
				where += " AND " + selection;
			}
			delCount = db.delete(DbSchema.TBL_USERS, where, selectionArgs);
			break;
		default:
			// no support for deleting photos or entities -
			// photos are deleted by a trigger when the item is deleted
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		// notify all listeners of changes:
		if (delCount > 0 && !isInBatchMode()) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return delCount;
	}

	@Override
	public String getType(Uri uri) {
		switch (URI_MATCHER.match(uri)) {
		case ITEM_LIST:
			return Users.CONTENT_TYPE;
		case ITEM_ID:
			return Users.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
      doAnalytics(uri, "insert");
		if (URI_MATCHER.match(uri) != ITEM_LIST) {
			throw new IllegalArgumentException(
					"Unsupported URI for insertion: " + uri);
		}
		SQLiteDatabase db = mHelper.getWritableDatabase();

    long id = db.insert(DbSchema.TBL_USERS, null, values);
    return getUriForId(id, uri);

	}

	private Uri getUriForId(long id, Uri uri) {
      if (id > 0) {
         Uri itemUri = ContentUris.withAppendedId(uri, id);
         if (!isInBatchMode()) {
            // notify all listeners of changes and return itemUri:
            getContext().
                  getContentResolver().
                        notifyChange(itemUri, null);
         }
         return itemUri;
      }
      // s.th. went wrong:
      throw new SQLException("Problem while inserting into uri: " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
	   doAnalytics(uri, "query");

		SQLiteDatabase db = mHelper.getReadableDatabase();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
	   boolean useAuthorityUri = false;
		switch (URI_MATCHER.match(uri)) {
		case ITEM_LIST:
	      builder.setTables(DbSchema.TBL_USERS);
	      if (TextUtils.isEmpty(sortOrder)) {
	         sortOrder = Users.SORT_ORDER_DEFAULT;
	      }
			break;
		case ITEM_ID:
	      builder.setTables(DbSchema.TBL_USERS);
			// limit query to one row at most:
			builder.appendWhere(Users._ID + " = "
					+ uri.getLastPathSegment());
			break;



		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		// if you like you can log the query
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		   logQuery(builder,  projection, selection, sortOrder);
		}
		else {
		   logQueryDeprecated(builder, projection, selection, sortOrder);
		}
		Cursor cursor = builder.query(db, projection, selection, selectionArgs,
				null, null, sortOrder);
		// if we want to be notified of any changes:
	   if (useAuthorityUri) {
         cursor.setNotificationUri(getContext().getContentResolver(), UsersContract.CONTENT_URI);
	   }
	   else {
	      cursor.setNotificationUri(getContext().getContentResolver(), uri);
	   }
		return cursor;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
   private void logQuery(SQLiteQueryBuilder builder, String[] projection, String selection, String sortOrder) {

	      Log.v("cpsample", "query: " + builder.buildQuery(projection, selection, null, null, sortOrder, null));
	}

	@SuppressWarnings("deprecation")
   private void logQueryDeprecated(SQLiteQueryBuilder builder, String[] projection, String selection, String sortOrder) {
         Log.v("cpsample", "query: " + builder.buildQuery(projection, selection, null, null, null, sortOrder, null));
   }

   @Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
      doAnalytics(uri, "update");
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int updateCount = 0;
		switch (URI_MATCHER.match(uri)) {
		case ITEM_LIST:
			updateCount = db.update(DbSchema.TBL_USERS, values, selection,
					selectionArgs);
			break;
		case ITEM_ID:
			String idStr = uri.getLastPathSegment();
			String where = Users._ID + " = " + idStr;
			if (!TextUtils.isEmpty(selection)) {
				where += " AND " + selection;
			}
			updateCount = db.update(DbSchema.TBL_USERS, values, where,
					selectionArgs);
			break;
		default:
			// no support for updating photos!
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		// notify all listeners of changes:
		if (updateCount > 0 && !isInBatchMode()) {
			getContext().getContentResolver().notifyChange(uri, null);
		}
		return updateCount;
	}

   @Override
   public ContentProviderResult[] applyBatch(
         ArrayList<ContentProviderOperation> operations)
         throws OperationApplicationException {
      SQLiteDatabase db = mHelper.getWritableDatabase();
      mIsInBatchMode.set(true);
      // the next line works because SQLiteDatabase
      // uses a thread local SQLiteSession object for
      // all manipulations
      db.beginTransaction();
      try {
         final ContentProviderResult[] retResult = super.applyBatch(operations);
         db.setTransactionSuccessful();
         getContext().getContentResolver().notifyChange(UsersContract.CONTENT_URI, null);
         return retResult;
      }
      finally {
         mIsInBatchMode.remove();
         db.endTransaction();
      }
   }

   private boolean isInBatchMode() {
      return mIsInBatchMode.get() != null && mIsInBatchMode.get();
   }



	/**
	 * I do not really use analytics, but if you export
	 * your content provider it makes sense to do so, to get
	 * a feeling for client usage. Especially if you want to
	 * _change_ something which might break existing clients,
	 * please check first if you can safely do so.
	 */
	private void doAnalytics(Uri uri, String event) {

         Log.v("cpsample", event + " -> " + uri);
         Log.v("cpsample", "caller: " + detectCaller());
	}

   /**
    * You can use this for Analytics.
    *
    * Be aware though: This might be costly if many apps
    * are running.
    */
   private String detectCaller() {
      // found here:
      // https://groups.google.com/forum/#!topic/android-developers/0HsvyTYZldA
      int pid = Binder.getCallingPid();
      return getProcessNameFromPid(pid);
   }

   /**
    * Returns the username of the process the pid belongs to. Can be null if neither
    * an Activity nor a Service could be found.
    * @param givenPid
    * @return
    */
   private String getProcessNameFromPid(int givenPid) {
      ActivityManager am = (ActivityManager) getContext().getSystemService(
            Activity.ACTIVITY_SERVICE);
      List<ActivityManager.RunningAppProcessInfo> lstAppInfo = am
            .getRunningAppProcesses();
      for (ActivityManager.RunningAppProcessInfo ai : lstAppInfo) {
         if (ai.pid == givenPid) {
            return ai.processName;
         }
      }
      // added to take care of calling services as well:
      List<ActivityManager.RunningServiceInfo> srvInfo = am
            .getRunningServices(Integer.MAX_VALUE);
      for (ActivityManager.RunningServiceInfo si : srvInfo) {
         if (si.pid == givenPid) {
            return si.process;
         }
      }
      return null;
   }

}
