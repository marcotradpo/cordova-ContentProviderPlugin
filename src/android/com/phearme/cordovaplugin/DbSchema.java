
package com.phearme.cordovaplugin;

import android.provider.BaseColumns;

/**
 * A helper interface which defines constants for work with the DB.
 *
 */
/* package private */ interface DbSchema {

	String DB_USERNAME = "users.db";

	String TBL_USERS = "users";

	String COL_ID = BaseColumns._ID;
	String COL_USERNAME = "username";
	String COL_PASSWORD = "password";
	String COL_DATA = "_data";
	String COL_USERS_ID = "users_id";

	String DDL_CREATE_TBL_USERS =
			"CREATE TABLE users (" +
			"_id           INTEGER  PRIMARY KEY AUTOINCREMENT, \n" +
			"username     TEXT,\n" +
			"password      TEXT \n" +
			")";

	String DML_WHERE_ID_CLAUSE = "_id = ?";

	String DEFAULT_TBL_USERS_SORT_ORDER = "username ASC";

}
