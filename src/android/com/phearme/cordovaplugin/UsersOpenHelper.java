
package com.phearme.cordovaplugin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The SQLiteOpenhelper implementation for lent users.
 *
 * @author Wolfram Rittmeyer
 *
 */
/* package */ class UsersOpenHelper extends SQLiteOpenHelper {

	private static final String USERNAME = DbSchema.DB_USERNAME;
	private static final int VERSION = 1;

	public UsersOpenHelper(Context context) {
		super(context, USERNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DbSchema.DDL_CREATE_TBL_USERS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// this is no sample for how to handle SQLite databases
		// thus I simply drop and recreate the database here.
		//
		// NEVER do this in real apps. Your users wouldn't like
		// loosing data just because you decided to change the schema
		db.execSQL(DbSchema.DDL_DROP_TBL_USERS);
		onCreate(db);
	}

}
