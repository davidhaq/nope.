package com.heliopause.nope.services;

import android.app.Notification;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class CallBlockService extends Service {

	// Debugging constants
	private static final boolean DEBUG = true;
	private static final String TAG = CallBlockService.class.getSimpleName();

	// CallBlock service actions
	public static final String a = CallBlockService.class.toString()
			+ ".ACTION_FOREGROUND";
	public static final String b = CallBlockService.class.toString()
			+ ".ACTION_BACKGROUND";
	public static final String c = CallBlockService.class.toString()
			+ ".ACTION_ON_INCOMING_CALL";
	public static final String d = CallBlockService.class.toString()
			+ ".ACTION_ON_OUTGOING_CALL";
	public static final String e = CallBlockService.class.toString()
			+ ".ACTION_ON_PHONE_GONE_IDLE";
	public static final String f = CallBlockService.class.toString()
			+ ".ACTION_ON_OFFHOOK_CALL";
	public static final String g = CallBlockService.class.toString()
			+ ".ACTION_TOGGLE_NOTIFICATION";
	public static final String h = CallBlockService.class.toString()
			+ ".ACTION_ON_EMERGENCY_CALL";
	public static final String i = CallBlockService.class.toString()
			+ ".ACTION_ON_INCOMING_SMS";
	public static final String j = CallBlockService.class.toString()
			+ ".ACTION_ON_INCOMING_MMS";

	public static CallReceiver receiver = new CallReceiver();
	SharedPreferences prefs;
	static ContentResolver cr = null;

	private void handleAction(Intent paramIntent) {

		Context localContext = getBaseContext();

		if ((paramIntent == null) || (paramIntent.getAction() == null)) {
			Log.d(TAG, "Fresh start of the service!");
			freshStart();
		}

		String action = paramIntent.getAction();

		if (action.equals(c)) {

		}

	}

	private void freshStart() {

		startForeground(0, null);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (DEBUG) {
			Log.d(TAG, "CallBlock service onDestroy; unregistering receiver");
		}

		unregisterReceiver(receiver);

	}

	@Override
	public void onStart(Intent intent, int startid) {

	}
}
