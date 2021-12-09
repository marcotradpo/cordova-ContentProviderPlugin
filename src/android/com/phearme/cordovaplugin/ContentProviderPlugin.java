package com.phearme.cordovaplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.net.Uri;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;

import com.phearme.cordovaplugin.UsersContract.Users;

public class ContentProviderPlugin extends CordovaPlugin {
	private String WRONG_PARAMS = "Wrong parameters.";
	private String UNKNOWN_ERROR = "Unknown error.";

    private UsersProvider usersProvider = new UsersProvider();

	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		final JSONArray methodArgs = args;
		final CallbackContext callback = callbackContext;
		final JSONObject queryArgs = methodArgs.getJSONObject(0);

		if (queryArgs == null) {
			callback.error(WRONG_PARAMS);
			return false;
		}

		if (action.equals("queryUser") || action.equals("insertUser")  || action.equals("deleteUser")  || action.equals("updateUser")) {
			cordova.getThreadPool().execute(new Runnable() {
			public void run() {
				if (action.equals("queryUser")) {
					runQuery(queryArgs, callback);
					}

				if (action.equals("insertUser")) {
					runInsert(queryArgs, callback);
					}

				if (action.equals("deleteUser")) {
					runDelete(queryArgs, callback);
					}

				if (action.equals("updateUser")) {
					runUpdate(queryArgs, callback);
					}
				};
			});
			return true;
		}

		return false;
	}

	private void runInsert(JSONObject queryArgs, CallbackContext callback) {

		String username = null;
		String password = null;
		JSONArray resultJSONArray;

		try {
			if (!queryArgs.isNull("username") || !queryArgs.isNull("password")) {
				username = queryArgs.getString("username");
				password = queryArgs.getString("password");
			} else {
				callback.error(WRONG_PARAMS);
				return;
			}
		} catch (JSONException e) {
			callback.error(WRONG_PARAMS);
			return;
		}

		ContentValues values = new ContentValues();
		values.put(Users.USERNAME, username);
		values.put(Users.PASSWORD, password);

		Uri userResult = cordova.getActivity().getContentResolver().insert(Users.CONTENT_URI, values);
		if (userResult == null) {
			callback.error(UNKNOWN_ERROR);
			return;
		}

		JSONObject resultUri = new JSONObject();

		try {
			resultUri.put("userResultUri", userResult);
		} catch (JSONException e) {
			e.printStackTrace();
			callback.error(UNKNOWN_ERROR);
			return;
		}

		callback.success(resultUri);
	}


	private void runUpdate(JSONObject queryArgs, CallbackContext callback) {

		String username = null;
		String password = null;
		String id = null;
		JSONArray resultJSONArray;

		try {
			if (!queryArgs.isNull("username") || !queryArgs.isNull("password") || !queryArgs.isNull("id")) {
				id = queryArgs.getString("id");
				username = queryArgs.getString("username");
				password = queryArgs.getString("password");
			} else {
				callback.error(WRONG_PARAMS);
				return;
			}
		} catch (JSONException e) {
			callback.error(WRONG_PARAMS);
			return;
		}

		ContentValues values = new ContentValues();
		values.put(Users.USERNAME, username);
		values.put(Users.PASSWORD, password);

		Uri updateUri = ContentUris.withAppendedId(Users.CONTENT_URI, Long.parseLong(id));
		long resultCount = cordova.getActivity().getContentResolver().update(updateUri, values, null, null);

		if (resultCount == 0) {
      		callback.error(UNKNOWN_ERROR);
      		return;
    	}

		JSONObject result = new JSONObject();
		try {
			result.put("resultCount", resultCount);
		} catch (JSONException e) {
			callback.error(UNKNOWN_ERROR);
			e.printStackTrace();
			return;
		}
		callback.success(result);
	}


	private void runDelete(JSONObject queryArgs, CallbackContext callback) {

		String id = null;
		JSONArray resultJSONArray;

		try {
			if ( !queryArgs.isNull("id")) {
				id = queryArgs.getString("id");
			} else {
				callback.error(WRONG_PARAMS);
				return;
			}
		} catch (JSONException e) {
			callback.error(WRONG_PARAMS);
			return;
		}


        Uri delUri = ContentUris.withAppendedId(Users.CONTENT_URI, Long.parseLong(id));
        long resultCount = cordova.getActivity().getContentResolver().delete(delUri, null, null);

		if (resultCount == 0) {
      		callback.error(UNKNOWN_ERROR);
      		return;
    	}

		JSONObject result = new JSONObject();
		try {
			result.put("resultCount", resultCount);
		} catch (JSONException e) {
			callback.error(UNKNOWN_ERROR);
			e.printStackTrace();
			return;
		}
		callback.success(result);
	}


	private void runQuery(JSONObject queryArgs, CallbackContext callback) {
		Uri contentUri = null;
		String[] projection = null;
		String selection = null;
		String[] selectionArgs = null;
		String sortOrder = null;
		JSONArray resultJSONArray;

		try {
			if (!queryArgs.isNull("contentUri")) {
				contentUri = Uri.parse(queryArgs.getString("contentUri"));
			} else {
				callback.error(WRONG_PARAMS);
				return;
			}
		} catch (JSONException e) {
			callback.error(WRONG_PARAMS);
			return;
		}
		if (contentUri == null) {
			callback.error(WRONG_PARAMS);
			return;
		}

		try {
			if (!queryArgs.isNull("projection")) {
				JSONArray projectionJsonArray = queryArgs.getJSONArray("projection");
				projection = new String[projectionJsonArray.length()];
				for (int i = 0; i < projectionJsonArray.length(); i++) {
					projection[i] = projectionJsonArray.getString(i);
				}
			}
		} catch (JSONException e1) {
			projection = null;
		}

		try {
			if (!queryArgs.isNull("selection")) {
				selection = queryArgs.getString("selection");
			}
		} catch (JSONException e1) {
			selection = null;
		}

		try {
			if (!queryArgs.isNull("selectionArgs")) {
				JSONArray selectionArgsJsonArray = queryArgs.getJSONArray("selectionArgs");
				selectionArgs = new String[selectionArgsJsonArray.length()];
				for (int i = 0; i < selectionArgsJsonArray.length(); i++) {
					selectionArgs[i] = selectionArgsJsonArray.getString(i);
				}
			}
		} catch (JSONException e1) {
			selectionArgs = null;
		}

		try {
			if (!queryArgs.isNull("sortOrder")) {
				sortOrder = queryArgs.getString("sortOrder");
			}
		} catch (JSONException e1) {
			sortOrder = null;
		}

		// run query
		Cursor result = cordova.getActivity().getContentResolver().query(contentUri, projection, selection, selectionArgs, sortOrder);
		resultJSONArray = new JSONArray();

		// Some providers return null if an error occurs, others throw an exception
		if(result == null) {
			callback.error(UNKNOWN_ERROR);
		} else {

			try {

				while (result != null && result.moveToNext()) {
					JSONObject resultRow = new JSONObject();
					int colCount = result.getColumnCount();
					for (int i = 0; i < colCount; i++) {
						try {
							resultRow.put(result.getColumnName(i), result.getString(i));
						} catch (JSONException e) {
							resultRow = null;
						}
					}
					resultJSONArray.put(resultRow);
				}
			} finally {
				if(result != null) result.close();
	        	}

			callback.success(resultJSONArray);

		}
	}
}
