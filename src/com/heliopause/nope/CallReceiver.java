package com.heliopause.nope;

import com.heliopause.nope.database.BlockItemTable;
import com.heliopause.nope.database.DatabaseHelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallReceiver extends BroadcastReceiver {

	// Debug constants
	private static final boolean DEBUG = true;
	private static final String TAG = CallReceiver.class.getSimpleName();

	private SQLiteDatabase db;
	private DatabaseHelper helper;
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {

		this.context = context;
		getHelper();
		db = helper.getReadableDatabase();
		String action = intent.getAction();
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

		Cursor c = db.rawQuery("SELECT * FROM "
				+ BlockItemTable.CALLBLOCK_TABLE_NAME + " WHERE "
				+ BlockItemTable.COLUMN_NUMBER + "=?;",
				new String[] { incomingNum });
		if (c != null) {
			return true;
		} else {
			return false;
		}

	}
}
