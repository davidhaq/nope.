package com.jphsoftware.nope.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BlockItemDatabaseHelper extends SQLiteOpenHelper {

	private static final String TAG = BlockItemDatabaseHelper.class
			.getSimpleName();

	// Name and version of database
	private static final String DATABASE_NAME = "blockitems.db";
	private static final int DATABASE_VERSION = 1;

	BlockItemDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d(TAG, "Database handler initiated");
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		BlockItemTable.onCreate(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {

		BlockItemTable.onUpgrade(database, oldVersion, newVersion);
	}

	/**
	 * Helper method to create a drop table query string.
	 * 
	 * @param tableName
	 *            The name of the table you wish to drop
	 * @return Returns an SQL query string using the name of the table you want
	 *         to drop.
	 */
	private String getTableDrop(String tableName) {
		return "DROP TABLE IF EXISTS " + tableName;
	}

	/**
	 * Method that drops the current tables, and calls the onCreate method to
	 * create them.
	 */
	public void redoTbl() {
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL(getTableDrop(CALLBLOCK_TABLE_NAME));
		db.execSQL(getTableDrop(SMSBLOCK_TABLE_NAME));
		Log.d(TAG, "Tables dropped by method \"redoTbl()\"");
		onCreate(db);
	}

	/**
	 * Use this method to insert a new block item row into the database.
	 * 
	 * @param tableName
	 *            The name of the table in which you wish to insert your new
	 *            item
	 * @param serializedItem
	 *            The bytecode of the serialized item you're storing
	 * @param lastContact
	 *            The last time this item tried to contact you
	 * 
	 */
	public void insert(String tableName, byte[] serializedItem,
			String lastContact) {
		long rowId = -1;
		try {
			SQLiteDatabase db = getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put(_BYTECODE, serializedItem);
			values.put(_LASTCONTACT, lastContact);

			rowId = db.insert(tableName, null, values);
			Log.i(TAG, "inserted: " + serializedItem); // If we see this Log, it
														// works!
		} catch (SQLiteException e) {
			Log.i(TAG, "insert()", e);
		} finally {
			Log.i(TAG, "inserted(): rowId=" + rowId);
		}
	}

	/**
	 * Use this method to remove a row from a table based on it's row ID.
	 * 
	 * @param tableName
	 *            The table from which to delete from
	 * @param rowId
	 *            The ID of the row you want to delete.
	 */
	public void remove(String tableName, long rowId) {

		try {
			SQLiteDatabase db = getWritableDatabase();

			db.delete(tableName, _ID + "=?",
					new String[] { String.valueOf(rowId) });
			Log.d(TAG, "removed: " + rowId); // If we see this Log, it works!
		} catch (SQLiteException e) {
			Log.i(TAG, "delete()", e);
		}
	}

	/**
	 * Returns a Cursor object based on what table you give it. Returns entire
	 * table.
	 * 
	 * @param tableName
	 *            Input the table name in String form
	 * @return Returns a Cursor object that points to the first value in the
	 *         table specified with @param tableName
	 */
	public Cursor query(String tableName) {
		SQLiteDatabase db = getWritableDatabase();
		return db.query(tableName, null, null, null, null, null, null + " "
				+ _LASTCONTACT + " DESC");
	}

}
