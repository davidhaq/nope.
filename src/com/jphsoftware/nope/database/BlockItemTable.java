package com.jphsoftware.nope.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BlockItemTable {

	private static final String TAG = BlockItemTable.class.getSimpleName();

	// Names and atrributes of tables that will hold block items
	public static final String CALLBLOCK_TABLE_NAME = "callblock";
	public static final String SMSBLOCK_TABLE_NAME = " smsblock";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NUMBER = "_number";
	// public static final String COLUMN_LOG = "_log";

	// private static final String CALLTABLE_CREATE =
	// " CREATE TABLE IF NOT EXISTS "
	// + CALLBLOCK_TABLE_NAME
	// + " ( "
	// + COLUMN_ID
	// + " INTEGER PRIMARY KEY AUTOINCREMENT, "
	// + COLUMN_NUMBER
	// + " LONG, " + COLUMN_LOG + " STRING ); ";
	// private static final String SMSTABLE_CREATE =
	// " CREATE TABLE IF NOT EXISTS "
	// + SMSBLOCK_TABLE_NAME
	// + " ( "
	// + COLUMN_ID
	// + " INTEGER PRIMARY KEY AUTOINCREMENT, "
	// + COLUMN_NUMBER
	// + " LONG, " + COLUMN_LOG + " STRING ); ";
	private static final String CALLTABLE_CREATE = " CREATE TABLE IF NOT EXISTS "
			+ CALLBLOCK_TABLE_NAME
			+ " ( "
			+ COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_NUMBER
			+ " LONG ); ";
	private static final String SMSTABLE_CREATE = " CREATE TABLE IF NOT EXISTS "
			+ SMSBLOCK_TABLE_NAME
			+ " ( "
			+ COLUMN_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COLUMN_NUMBER
			+ " LONG ); ";

	
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
		db.execSQL("DROP TABLE IF EXISTS " + SMSBLOCK_TABLE_NAME);
		onCreate(db);
	}

}
