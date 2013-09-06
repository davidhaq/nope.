package com.jphsoftware.nope.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.util.Log;

public class BlockItemContentProvider extends ContentProvider {

	// Class tag
	private static final String TAG = BlockItemContentProvider.class
			.getSimpleName();

	// database
	private BlockItemDatabaseHelper database;

	// The authority string
	private static final String AUTHORITY = "com.jphsoftware.nope.database";

	// Define the table URIs
	private static final String PATH_CALLBLOCKS = "callblockitems";
	private static final String PATH_SMSBLOCKS = "smsblockitems";
	public static final Uri CONTENT_URI_CALLBLOCKS = Uri.parse("content://"
			+ AUTHORITY + "/" + PATH_CALLBLOCKS);
	public static final Uri CONTENT_URI_SMSBLOCKS = Uri.parse("content://"
			+ AUTHORITY + "/" + PATH_SMSBLOCKS);

	// Setup the UriMacher
	private static final int CALLBLOCKITEMS = 10;
	private static final int CALLBLOCKITEM_ID = 20;
	private static final int SMSBLOCKITEMS = 30;
	private static final int SMSBLOCKITEM_ID = 40;
	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, PATH_CALLBLOCKS, CALLBLOCKITEMS);
		sURIMatcher.addURI(AUTHORITY, PATH_CALLBLOCKS + "/#", CALLBLOCKITEM_ID);
		sURIMatcher.addURI(AUTHORITY, PATH_SMSBLOCKS, SMSBLOCKITEMS);
		sURIMatcher.addURI(AUTHORITY, PATH_SMSBLOCKS + "/#", SMSBLOCKITEM_ID);
	}

	@Override
	public boolean onCreate() {
		database = new BlockItemDatabaseHelper(getContext());
		return false;
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
		SQLiteDatabase db = database.getWritableDatabase();
		return db.query(tableName, null, null, null, null, null, null + " "
				+ BlockItemTable._LASTCONTACT + " DESC");
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	/**
	 * Use this method to insert a new block item row into a predefined
	 * database.
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
	public void insert(String tablePath, byte[] serializedItem,
			String lastContact) {
		long rowId = -1;
		try {
			SQLiteDatabase db = database.getWritableDatabase();
			ContentValues values = new ContentValues();

			values.put(BlockItemTable._BYTECODE, serializedItem);
			values.put(BlockItemTable._LASTCONTACT, lastContact);

			rowId = db.insert(tableName, null, values);
			Log.i(TAG, "inserted: " + serializedItem); // If we see this Log, it
														// works!
		} catch (SQLiteException e) {
			Log.i(TAG, "insert()", e);
		} finally {
			Log.i(TAG, "inserted(): rowId=" + rowId);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase db = database.getWritableDatabase();
		long id = 0;
		switch (uriType) {
		case CALLBLOCKITEMS:
			id = db
					.insert(BlockItemTable.CALLBLOCK_TABLE_NAME, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(PATH_CALLBLOCKS + "/" + id);
		case SMSBLOCKITEMS:
			id = db.insert(BlockItemTable.SMSBLOCK_TABLE_NAME, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.parse(PATH_SMSBLOCKS + "/" + id);
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		
	}

}
