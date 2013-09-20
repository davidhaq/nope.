package com.heliopause.nope.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;

public class CallBlockService extends Service {

	// // Debugging constants
	// private static final boolean DEBUG = true;
	// private static final String TAG = CallBlockService.class.getSimpleName();

	public static CallReceiver receiver = new CallReceiver();
	SharedPreferences prefs;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(99999999);
		registerReceiver(receiver, filter);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		startForeground(startid, new Notification());
	}
}
