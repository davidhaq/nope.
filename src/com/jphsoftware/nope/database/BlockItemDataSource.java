package com.jphsoftware.nope.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BlockItemDataSource {

	// Debugging variables
	private static final String TAG = BlockItemDataSource.class.getSimpleName();
	private static final boolean DEBUG = true;

	// database fields
	private DatabaseHelper helper;
	private SQLiteDatabase database;

	public BlockItemDataSource(Context context) {
		helper = new DatabaseHelper(context);
		if (DEBUG) {
			Log.d(TAG, "Database helper initiated");
		}

	}
	
	public void open() {
		database = helper.getWritableDatabase();
	}
	
	public void close(){
		helper.close();
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
	 * @param values
	 *            A ContentValue object holding two values: First, it contains
	 *            the bytecode array of a serialized BlockItem object. It's
	 *            second value is the latest contact time from that object to
	 *            make it easier to sort the DB tables.
	 */
	public void insert(String tableName, ContentValues values) {
		long rowId = -1;
		try {
			SQLiteDatabase db = getWritableDatabase();

			rowId = db.insert(tableName, null, values);
			Log.d(TAG, "inserted: " + values.getAsByteArray(_BYTECODE));
			// If we see the above log, it worked!
		} catch (SQLiteException e) {
			Log.d(TAG, "insert()", e);
		} finally {
			Log.d(TAG, "inserted(): rowId=" + rowId);
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
	 * Returns a Cursor object based on what table you give it. Cursor return
	 * points at the first row of the entire table specified
	 * 
	 * @param tableName
	 *            Input the table name in String form
	 * @return Returns a Cursor object that points to the first value in the
	 *         table specified with @param tableName
	 */
	public Cursor query(String tableName) {
		SQLiteDatabase db = getWritableDatabase();
		return db.query(tableName, null, null, null, null, null, _ID + " DESC");
	}

}
