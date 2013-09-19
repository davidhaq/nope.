package com.heliopause.nope.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.heliopause.nope.Constants;
import com.heliopause.nope.database.BlockItemTable;
import com.heliopause.nope.database.DatabaseHelper;

public class CallReceiver extends BroadcastReceiver {

	// Debug constants
	private static final boolean DEBUG = true;
	private static final String TAG = CallReceiver.class.getSimpleName();

	private SQLiteDatabase db;
	private DatabaseHelper helper;
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {

		// Set the context
		this.context = context;

		// Check is the listener is disabled
		SharedPreferences prefs = context.getSharedPreferences(
				Constants.SETTINGS_PREFS, Context.MODE_PRIVATE);
		boolean turnedOn = prefs.getBoolean(
				Constants.CALL_BLOCK_SERVICE_STATUS, true);
		if (!turnedOn) {
			return;
		}

		getHelper();
		db = helper.getReadableDatabase();

		// Get the action if the intent isn't null.
		String action = (intent == null) ? null : intent.getAction();

		if (action.equalsIgnoreCase("android.intent.action.PHONE_STATE")) {
			if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
					TelephonyManager.EXTRA_STATE_RINGING)) {
				if (isOnBlockList(intent
						.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER))) {
					if (DEBUG)
						Log.d(TAG, "Phone number is on block list!");
				} else {
					if (DEBUG)
						Log.d(TAG,
								"Phone number was not detected on block list");
				}
			}
		}

	}

	private void getHelper() {
		if (helper == null) {
			helper = new DatabaseHelper(context);
			if (DEBUG) {
				Log.d(TAG,
						"Creating a new instance of the database helper object");
			}
		} else {
			if (DEBUG) {
				Log.d(TAG, "Using existing database helper");
			}
		}
	}

	private boolean isOnBlockList(String incomingNum) {
		Log.d(TAG, incomingNum);
		int count = 0;
		Cursor c = null;

		c = db.rawQuery("SELECT " + BlockItemTable.COLUMN_NUMBER + " FROM "
				+ BlockItemTable.CALLBLOCK_TABLE_NAME, null);

		c.moveToFirst();
		while (!c.isAfterLast()) {
			Log.d(TAG,
					c.getString(c.getColumnIndex(BlockItemTable.COLUMN_NUMBER)));
			if (incomingNum.contains(c.getString(c
					.getColumnIndex(BlockItemTable.COLUMN_NUMBER)))) {

				count++;
				Log.d(TAG, "Count inside if: " + count);
			}
			Log.d(TAG, "Count outside if: " + count);
			c.moveToNext();
		}
		Log.d(TAG, "Count outside while: " + count);
		return count > 0;

	}
}
