package com.heliopause.nope.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.heliopause.nope.Constants;
import com.heliopause.nope.database.BlockItemTable;
import com.heliopause.nope.database.DatabaseHelper;

public class MsgReceiver extends BroadcastReceiver {

	// Debug constants
	private static final boolean DEBUG = true;
	private static final String TAG = MsgReceiver.class.getSimpleName();

	// Database checking objects
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
		boolean turnedOn = prefs.getBoolean(Constants.MSG_BLOCK_SERVICE_STATUS, true);
		if (!turnedOn) {
			return;
		}

		getHelper();
		db = helper.getReadableDatabase();

		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		String str = "";
		if (bundle != null) {
			// ---retrieve the sender of the sms received.---
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];
			for (int i = 0; i < msgs.length; i++) {
				msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				str += msgs[i].getOriginatingAddress();
				if (DEBUG) {
					Log.d(TAG, "Sender: " + str);
				}
			}
			if (isOnBlockList(str)) {
				if (DEBUG)
					Log.d(TAG, "Phone number is on block list!");
			} else {
				if (DEBUG)
					Log.d(TAG, "Phone number was not detected on block list");
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

		Cursor c = db.rawQuery("SELECT " + BlockItemTable.COLUMN_NUMBER
				+ " FROM " + BlockItemTable.MSGBLOCK_TABLE_NAME + " WHERE "
				+ BlockItemTable.COLUMN_NUMBER + "=?;",
				new String[] { incomingNum });
		if (c != null) {
			return true;
		} else {
			return false;
		}

	}

}
