package com.heliopause.nope.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

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
	private SharedPreferences settings;

	@Override
	public void onReceive(Context context, Intent intent) {

		// Set the context
		this.context = context;

		// Grab the SharedPrefs
		settings = PreferenceManager.getDefaultSharedPreferences(context);

		getHelper();
		db = helper.getReadableDatabase();

		// Get the action if the intent isn't null.
		String action = (intent == null) ? null : intent.getAction();

		if (action.equalsIgnoreCase("android.intent.action.PHONE_STATE")) {
			if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
					TelephonyManager.EXTRA_STATE_RINGING)) {
				if (isOnBlockList(intent
						.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER))) {
					if (DEBUG) {
						Log.d(TAG, "Phone number is on block list!");
					}
					blockCall(context);
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

	private void blockCall(Context context) {

		String method = settings.getString("pref_key_call_block_method",
				Constants.CALL_BLOCK_METHOD_THREE);

		if (method.equalsIgnoreCase(Constants.CALL_BLOCK_METHOD_ONE)) {
			if (DEBUG) {
				Log.d(TAG, "Blocking call with method 1");
			}
			// froyo and beyond trigger on buttonUp instead of buttonDown
			Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
			buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
					KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
			context.sendOrderedBroadcast(buttonUp,
					"android.permission.CALL_PRIVILEGED");
			
			Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
		    //headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
		    headSetUnPluggedintent.putExtra("state", 0);
		    headSetUnPluggedintent.putExtra("name", "Headset");
		    try {
		        context.sendOrderedBroadcast(headSetUnPluggedintent, null);
		    } catch (Exception e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		    }
		}

	}
}
