package com.heliopause.nope.services;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.util.Log;

import com.heliopause.nope.database.BlockItemTable;
import com.heliopause.nope.database.DatabaseHelper;
import com.heliopause.nope.fragments.MsgBlockFragment;

public class MsgReceiver extends BroadcastReceiver {

	// Debug constants
	private static final boolean DEBUG = true;
	private static final String TAG = MsgReceiver.class.getSimpleName();

	// Database checking objects
	private SQLiteDatabase db;
	private DatabaseHelper helper;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (!intent.getAction().equals(
				"android.provider.Telephony.SMS_RECEIVED")) {
			return;
		} else {

			if (DEBUG)
				Log.d(TAG, "Msg onReceive");

			// Grab the database
			helper = new DatabaseHelper(context);
			db = helper.getReadableDatabase();

			// Grab the bundle from the incoming message
			Bundle bundle = intent.getExtras();
			SmsMessage[] msgs = null;

			String address = "";
			String body = "";
			Configuration localConfiguration;

			if (!intent.getExtras().isEmpty()) {
				// ---retrieve the sender of the sms received.---
				Object[] pdus = (Object[]) bundle.get("pdus");
				msgs = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

					address = msgs[i].getOriginatingAddress();
					body = msgs[i].getMessageBody();
					localConfiguration = Configuration.getInstance(context);

					if (DEBUG) {
						Log.d(TAG, "Sender: " + address);
					}

					if (isOnBlockList(address)) {
						if (DEBUG)
							Log.d(TAG, "Phone number is on block list!");

						updateItemTime(PhoneNumberUtils
								.stripSeparators(address));
						abortBroadcast();
						return;
					} else {

						if (DEBUG)
							Log.d(TAG, "Phone number not on block list!");
						if (localConfiguration.mEnabled) {
							if (DEBUG)
								Log.d(TAG, "Configuration enabled");
							if (localConfiguration.checkAndUpdateSMS(context,
									address, body)) {
								if (DEBUG)
									Log.d(TAG, "aborting broadcast");
								abortBroadcast();
								return;
							}
						} else {
							if (DEBUG)
								Log.d(TAG, "Configuration disabled");
							i++;
							if (DEBUG)
								Log.d(TAG, "i: " + i);
							return;
						}
					}

				}

			}
		}

	}

	private boolean isOnBlockList(String incomingNum) {
		// Counter to tell if a number is on the blocklist.
		int count = 0;
		Cursor c = db.rawQuery("SELECT " + BlockItemTable.COLUMN_NUMBER
				+ " FROM " + BlockItemTable.MSGBLOCK_TABLE_NAME, null);

		// Move to the first row, just in case.
		c.moveToFirst();

		// Scan through all the numbers in the column. If any match, count is
		// incremented signifying that the number was found in the list
		while (!c.isAfterLast()) {
			if (incomingNum.contains(c.getString(c
					.getColumnIndex(BlockItemTable.COLUMN_NUMBER)))) {

				count++;
			}
			c.moveToNext();
		}
		c.close();
		return count > 0;

	}

	private void updateItemTime(String number) {

		// Integer to store row ID
		int ID = 0;
		Cursor c = db.rawQuery("SELECT * FROM "
				+ BlockItemTable.MSGBLOCK_TABLE_NAME, null);

		// Move to the first row, just in case.
		c.moveToFirst();

		// Scan through all the numbers in the column. If any match, count is
		// incremented signifying that the number was found in the list
		while (!c.isAfterLast()) {
			if (number.contains(c.getString(c
					.getColumnIndex(BlockItemTable.COLUMN_NUMBER)))) {

				ID = c.getInt(c.getColumnIndex(BlockItemTable.COLUMN_ID));
			}
			c.moveToNext();
		}

		if (DEBUG)
			Log.d(TAG, "ROW ID: " + ID);

		//Closing the cursor because we're done with it.
		c.close();
		
		ContentValues cv = new ContentValues();
		cv.put(BlockItemTable.COLUMN_LAST_CONTACT, System.currentTimeMillis());
		MsgBlockFragment.loader.update(BlockItemTable.MSGBLOCK_TABLE_NAME, cv,
				BlockItemTable.COLUMN_ID + "='" + ID + "'", null);
	}

}
