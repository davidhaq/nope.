package com.heliopause.nope.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallBlockService extends Service {

	// Debugging constants
	private static final boolean DEBUG = true;
	private static final String TAG = CallBlockService.class.getSimpleName();

	public static CallReceiver receiver = new CallReceiver();
	SharedPreferences prefs;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (DEBUG) {
			Log.d(TAG, TAG+" onCreate called.");
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		filter.setPriority(999);
		registerReceiver(receiver, filter);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (DEBUG) {
			Log.d(TAG, TAG+" onDestroy called.");
		}
		unregisterReceiver(receiver);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		if (DEBUG) {
			Log.d(TAG, TAG+" onStart called.");
		}
		startForeground(startid, new Notification());
	}
}
