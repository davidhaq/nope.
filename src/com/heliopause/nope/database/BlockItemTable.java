package com.heliopause.nope.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BlockItemTable {

    private static final String TAG = BlockItemTable.class.getSimpleName();

    // Names and attributes of tables that will hold block items
    public static final String CALLBLOCK_TABLE_NAME = "callblock";
    public static final String MSGBLOCK_TABLE_NAME = "msgblock";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NUMBER = "_number";
    public static final String COLUMN_LAST_CONTACT = "_lcontact";

    // Table creating query strings
    private static final String CALLTABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + CALLBLOCK_TABLE_NAME
            + "(\n"
            + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, \n"
            + COLUMN_NUMBER
            + " TEXT, \n" + COLUMN_LAST_CONTACT + " INTEGER DEFAULT -1337);";

    private static final String SMSTABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + MSGBLOCK_TABLE_NAME + "(\n" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, \n" + COLUMN_NUMBER
            + " TEXT, \n" + COLUMN_LAST_CONTACT + " INTEGER DEFAULT -1337);";

    public static void onCreate(SQLiteDatabase db) {

        db.execSQL(CALLTABLE_CREATE);
        db.execSQL(SMSTABLE_CREATE);
        Log.d(TAG, "Creating database from defined schema:" + db);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion,
                                 int newVersion) {

        // Where we will change the databases on upgrading to future versions if
        // the databases changes to preserve block data
        onCreate(db);
    }

}
