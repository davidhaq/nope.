package com.heliopause.nope.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import com.heliopause.nope.Constants;
import com.heliopause.nope.database.BlockItemTable;
import com.heliopause.nope.database.DatabaseHelper;

public class CallReceiver extends BroadcastReceiver {

	// Debug constants
	private static final boolean DEBUG = true;
	private static final String TAG = CallReceiver.class.getSimpleName();

	private SQLiteDatabase db;
	private DatabaseHelper helper;
	private SharedPreferences settings;

	@Override
	public void onReceive(Context context, Intent intent) {

		// Grab the SharedPrefs
		settings = PreferenceManager.getDefaultSharedPreferences(context);

		helper = new DatabaseHelper(context);
		db = helper.getReadableDatabase();

		// Get the action if the intent isn't null.
		String state = (intent == null) ? null : intent.getStringExtra("state");

		if (settings.getBoolean(Constants.CALL_BLOCK_SERVICE_STATUS, true)
				&& isOnBlockList(intent
						.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER))) {

			if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
				Intent ringingIntent = new Intent(context,
						CallBlockService.class);
				ringingIntent.setAction(CallBlockService.c);
				String incomingNum = intent
						.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
				ringingIntent.putExtra(TelephonyManager.EXTRA_INCOMING_NUMBER,
						incomingNum);
				context.startService(ringingIntent);

			} else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {

				Intent offHookIntent = new Intent(context,
						CallBlockService.class);
				offHookIntent.setAction(CallBlockService.f);
				offHookIntent.putExtras(intent);
				context.startService(offHookIntent);

			} else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
				Intent idleIntent = new Intent(context, CallBlockService.class);
				idleIntent.setAction(CallBlockService.e);
				idleIntent.putExtras(intent);
				context.startService(idleIntent);
			}
		} else {
			return;
		}
	}

	private boolean isOnBlockList(String incomingNum) {
		// Counter to tell if a number is on the blocklist.
		int count = 0;
		Cursor c = db.rawQuery("SELECT " + BlockItemTable.COLUMN_NUMBER
				+ " FROM " + BlockItemTable.CALLBLOCK_TABLE_NAME, null);

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
		return count > 0;

	}

}
