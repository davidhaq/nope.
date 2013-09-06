package com.jphsoftware.nope.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BlockItemTable {

	private static final String TAG = BlockItemTable.class.getSimpleName();

	// Names and atrributes of tables that will hold block items
	public static final String CALLBLOCK_TABLE_NAME = " call block database ";
	public static final String SMSBLOCK_TABLE_NAME = " call block database ";
	public static final String _ID = "_id";
	public static final String _BYTECODE = "_bytecode";
	public static final String _LASTCONTACT = "_lastcontact";

	private static final String CALLTABLE_CREATE = " CREATE TABLE IF NOT EXISTS "
			+ CALLBLOCK_TABLE_NAME
			+ " ( "
			+ _ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ _BYTECODE
			+ " BLOB, "
			+ _LASTCONTACT + " STRING ); ";
	private static final String SMSTABLE_CREATE = " CREATE TABLE IF NOT EXISTS "
			+ SMSBLOCK_TABLE_NAME
			+ " ( "
			+ _ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ _BYTECODE
			+ " BLOB, "
			+ _LASTCONTACT + " STRING ); ";

	public static void onCreate(SQLiteDatabase db) {

		db.execSQL(CALLTABLE_CREATE);
		db.execSQL(SMSTABLE_CREATE);
		Log.d(TAG, "Creating database from defined schema:" + db);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.d(TAG, "Upgrading database from version " + oldVersion
				+ " to version " + newVersion + ". All data will be deleted ");
		db.execSQL(getTableDrop(CALLBLOCK_TABLE_NAME));
		db.execSQL(getTableDrop(SMSBLOCK_TABLE_NAME));
		onCreate(db);
	}

	/**
	 * Helper method to create a drop table query string.
	 * 
	 * @param tableName
	 *            The name of the table you wish to drop
	 * @return Returns an SQL query string using the name of the table you want
	 *         to drop.
	 */
	private static String getTableDrop(String tableName) {
		return "DROP TABLE IF EXISTS " + tableName;
	}

}
