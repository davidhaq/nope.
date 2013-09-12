package com.jphsoftware.nope.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BlockItemTable {

	private static final String TAG = BlockItemTable.class.getSimpleName();

	// Names and atrributes of tables that will hold block items
	public static final String CALLBLOCK_TABLE_NAME = "callblock";
	public static final String MSGBLOCK_TABLE_NAME = " msgblock";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NUMBER = "_number";
	public static final String COLUMN_LAST_CONTACT = "_lcontact";

	// Table creationg query strings
	private static final String CALLTABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ CALLBLOCK_TABLE_NAME
			+ "("
			+ COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_NUMBER
			+ " TEXT, " + COLUMN_LAST_CONTACT + " INTEGER);";
	private static final String SMSTABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ MSGBLOCK_TABLE_NAME + "(" + COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NUMBER
			+ " TEXT, " + COLUMN_LAST_CONTACT + " INTEGER);";

	public static void onCreate(SQLiteDatabase db) {

		db.execSQL(CALLTABLE_CREATE);
		db.execSQL(SMSTABLE_CREATE);
		Log.d(TAG, "Creating database from defined schema:" + db);
	}

	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.d(TAG, "Upgrading database from version " + oldVersion
				+ " to version " + newVersion + ". All data will be deleted ");
		db.execSQL("DROP TABLE IF EXISTS " + CALLBLOCK_TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + MSGBLOCK_TABLE_NAME);
		onCreate(db);
	}

}
