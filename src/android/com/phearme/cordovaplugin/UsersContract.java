/*
 * 
 */
package com.phearme.cordovaplugin;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * The contract between clients and the users content provider.
 *
 */
public final class UsersContract {

	/**
	 * The authority of the users provider.
	 */
	public static final String AUTHORITY = "it.iooota.jarvis.users";
	/**
	 * The content URI for the top-level users authority.
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
	/**
	 * A selection clause for ID based queries.
	 */
	public static final String SELECTION_ID_BASED = BaseColumns._ID + " = ? ";

	/**
	 * Constants for the Users table of the users provider.
	 */
	public static final class Users implements CommonColumns {
		/**
		 * The content URI for this table.
		 */
		public static final Uri CONTENT_URI =  Uri.withAppendedPath(UsersContract.CONTENT_URI, "users");
		/**
		 * The mime type of a directory of users.
		 */
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.it.iooota.users_users";
		/**
		 * The mime type of a single item.
		 */
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.it.iooota.users_users";
		/**
		 * A projection of all columns in the users table.
		 */
		public static final String[] PROJECTION_ALL = {_ID, USERNAME, PASSWORD};
		/**
		 * The default sort order for queries containing USERNAME fields.
		 */
		public static final String SORT_ORDER_DEFAULT = USERNAME + " ASC";
	}



   /**
    * This interface defines common columns found in multiple tables.
    */
	public static interface CommonColumns extends BaseColumns {
      /**
       * The username of the item.
       */
      public static final String USERNAME = "username";
      /**
       * The password of the item.
       */
      public static final String PASSWORD = "password";
	}
}
