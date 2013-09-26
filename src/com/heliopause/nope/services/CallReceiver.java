package com.heliopause.nope.services;

import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Build;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;
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
	private SharedPreferences prefs;
	private ITelephony telephonyService;

	private int version;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (!intent.getAction().equals("android.intent.action.PHONE_STATE")) {
			return;
		} else {
			if (DEBUG) {
				Log.d(TAG, "CallReceiver pinged");
				Log.d(TAG,
						""
								+ intent.getStringExtra(TelephonyManager.EXTRA_STATE));
			}

			helper = new DatabaseHelper(context);
			db = helper.getReadableDatabase();

			// Grab the SharedPrefs
			settings = PreferenceManager.getDefaultSharedPreferences(context);
			prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);

			version = getVersion();
			String method = settings.getString("pref_key_call_block_method",
					Constants.CALL_BLOCK_METHOD_THREE);

			if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
					TelephonyManager.EXTRA_STATE_RINGING)) {

				boolean isBlocked = isOnBlockList(intent
						.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
				if (isBlocked) {
					if (DEBUG) {
						Log.d(TAG, "Phone number is on block list!");
					}
					if (method
							.equalsIgnoreCase(Constants.CALL_BLOCK_METHOD_ONE)) {
						incomingCallActionMethodOne(intent, context, version);
					} else if (method
							.equalsIgnoreCase(Constants.CALL_BLOCK_METHOD_TWO)) {
						// Ignore call method
						incomingCallActionMethodTwo(context);
						new Thread(new Runnable() {
							@Override
							public void run() {
								prefs.edit().putBoolean("blocked", true)
										.commit();
							}
						}).start();

					} else if (method
							.equalsIgnoreCase(Constants.CALL_BLOCK_METHOD_THREE)) {
						incomingCallActionMethodThree(context);
						new Thread(new Runnable() {
							@Override
							public void run() {
								prefs.edit().putBoolean("blocked", true)
										.commit();
							}
						}).start();

					}
				} else {
					if (DEBUG)
						Log.d(TAG,
								"Phone number was not detected on block list");
					return;
				}
			}
			if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
					TelephonyManager.EXTRA_STATE_OFFHOOK)) {
				if (DEBUG)
					Log.d(TAG, "WE in offhook state now.");
				if (method.equalsIgnoreCase(Constants.CALL_BLOCK_METHOD_ONE)) {
					try {
						synchronized (this) {
							Log.d(TAG, "Waiting for 1 sec ");
							this.wait(1000);
						}
					} catch (Exception e) {
						Log.d(TAG, "Exception while waiting !!");
						e.printStackTrace();
					}
					offHookCallActionMethodOne(context, version);

				}
			}
			if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(
					TelephonyManager.EXTRA_STATE_IDLE)) {
				if (DEBUG)
					Log.d(TAG, "idle state");
				if (method.equalsIgnoreCase(Constants.CALL_BLOCK_METHOD_TWO)) {
					if (prefs.getBoolean("blocked", false)) {
						if (DEBUG)
							Log.d(TAG, "hurray! it was blocked!");
						new Thread(new Runnable() {
							@Override
							public void run() {
								prefs.edit().putBoolean("blocked", false);
								prefs.edit().commit();
							}
						}).start();
						idleCallActionMethodThree(context);
					}

				} else if (method
						.equalsIgnoreCase(Constants.CALL_BLOCK_METHOD_THREE)) {
					if (prefs.getBoolean("blocked", false)) {
						if (DEBUG)
							Log.d(TAG, "hurray! it was blocked!");
						try {
							synchronized (this) {
								Log.d(TAG, "Waiting for 1 sec ");
								this.wait(1000);
							}
						} catch (Exception e) {
							Log.d(TAG, "Exception while waiting!");
							e.printStackTrace();
						}
						if (DEBUG)
							Log.d(TAG, "is blocked!");
						new Thread(new Runnable() {
							@Override
							public void run() {
								prefs.edit().putBoolean("blocked", false);
								prefs.edit().commit();
							}
						}).start();
						idleCallActionMethodThree(context);
					}
				}
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

	// ********Call Blocking methods begin here************

	// Method 1 (Answer and Hang up)
	private void incomingCallActionMethodOne(Intent intent, Context context,
			int version) {

		if (DEBUG) {
			Log.d(TAG, "Inside of incomingCallActionMethodOne");
		}
		if (version == 7) {
			if (DEBUG) {
				Log.d(TAG, "Answering call using API 7 method");
			}
			// Simulate a press of the headset button to pick up the call
			Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
			buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
					KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
			context.sendOrderedBroadcast(buttonDown,
					"android.permission.CALL_PRIVILEGED");
			return;
		} else if (version > 7) {
			if (DEBUG) {
				Log.d(TAG, "Answering call using API 8 and above methods");
			}
			Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
			buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
					KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
			try {
				context.sendOrderedBroadcast(buttonUp,
						"android.permission.CALL_PRIVILEGED");
				Log.d(TAG, "ACTION_MEDIA_BUTTON broadcasted...");
			} catch (Exception e) {
				Log.d(TAG, "Catch block of ACTION_MEDIA_BUTTON broadcast !");
			}

		}
	}

	private void offHookCallActionMethodOne(Context context, int version) {

		if (version == 7) {
			getTeleService(context);
			try {
				telephonyService.endCall();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if ((version > 7) && (version < 16)) {
			Intent headSetUnPluggedintent = new Intent(
					Intent.ACTION_HEADSET_PLUG);
			headSetUnPluggedintent
					.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
			headSetUnPluggedintent.putExtra("state", 0);
			headSetUnPluggedintent.putExtra("name", "Headset");
			context.sendOrderedBroadcast(headSetUnPluggedintent, null);

		} else if (version > 16) {
			getTeleService(context);
			try {
				telephonyService.endCall();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// Method 2 (Send to voicemail)
	private void incomingCallActionMethodTwo(Context context) {
		AudioManager am;
		am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		getTeleService(context);

		try {
			telephonyService.endCall();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Method 3 (Silence)
	private void incomingCallActionMethodThree(Context context) {
		AudioManager am;
		am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		return;
	}

	private void idleCallActionMethodThree(Context context) {
		AudioManager am;
		am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
	}

	private void getTeleService(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		try {
			// Java reflection to gain access to TelephonyManager's
			// ITelephony getter
			Log.v(TAG, "Get getTeleService...");
			Class<?> c = Class.forName(tm.getClass().getName());
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			telephonyService = (ITelephony) m.invoke(tm);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "FATAL ERROR: could not connect to telephony subsystem");
			Log.e(TAG, "Exception object: " + e);
		}
	}

	public int getVersion() {
		try {
			int n = Build.VERSION.class.getField("SDK_INT").getInt(null);
			version = n;
			return version;
		} catch (SecurityException localSecurityException) {
			while (true)
				version = Build.VERSION.SDK_INT;
		} catch (NoSuchFieldException localNoSuchFieldException) {
			while (true)
				version = Build.VERSION.SDK_INT;
		} catch (IllegalArgumentException localIllegalArgumentException) {
			while (true)
				version = Build.VERSION.SDK_INT;
		} catch (IllegalAccessException localIllegalAccessException) {
			while (true)
				version = Build.VERSION.SDK_INT;
		}
	}
}