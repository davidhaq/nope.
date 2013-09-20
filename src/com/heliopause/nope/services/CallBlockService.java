package com.heliopause.nope.services;

import java.lang.reflect.Method;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;
import com.heliopause.nope.Constants;

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
	public static final String e = CallBlockService.class.toString()
			+ ".ACTION_ON_PHONE_GONE_IDLE";
	public static final String f = CallBlockService.class.toString()
			+ ".ACTION_ON_OFFHOOK_CALL";

	SharedPreferences settings;

	private static int version;
	private ITelephony telephonyService;

	private void handleAction(Intent paramIntent, int startid) {

		Context localContext = getBaseContext();

		if ((paramIntent == null) || (paramIntent.getAction() == null)) {
			Log.d(TAG, "Fresh start of the service!");
			startForeground(startid, null);
		}

		String action = paramIntent.getAction();
		String method = settings.getString("pref_key_call_block_method",
				Constants.CALL_BLOCK_METHOD_THREE);
		int version = getVersion();
		if (action.equals(c)) {

			if (method.equalsIgnoreCase(Constants.CALL_BLOCK_METHOD_ONE)) {

				incomingCallActionMethodOne(localContext, version);
			}

		} else if (action.equals(f)) {

			if (method.equalsIgnoreCase(Constants.CALL_BLOCK_METHOD_ONE)) {
				offHookCallActionMethodOne(localContext, version);
			}

		} else if (action.equals(e)) {

			if (method.equalsIgnoreCase(Constants.CALL_BLOCK_METHOD_ONE)) {

			}

		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		settings = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onStart(Intent intent, int startid) {

		handleAction(intent, startid);

	}

	public static int getVersion() {
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

	public static int getCallState(Context paramContext) {

		return ((TelephonyManager) paramContext.getSystemService("phone"))
				.getCallState();

	}

	// ********Call Blocking methods begin here************

	// Method 1 (Answer and Hang up)
	private void incomingCallActionMethodOne(Context context, int version) {

		if (version == 7) {
			// Simulate a press of the headset button to pick up the call
			Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
			buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
					KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
			context.sendOrderedBroadcast(buttonDown,
					"android.permission.CALL_PRIVILEGED");
		} else if ((version > 7) && (version < 16)) {
			// froyo and beyond trigger on buttonUp instead of buttonDown
			Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
			buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
					KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
			context.sendOrderedBroadcast(buttonUp,
					"android.permission.CALL_PRIVILEGED");
		} else if (version > 16) {
			// simulate a headphone jack being plugged in for android 16 and up.
			Intent headSetUnPluggedintent = new Intent(
					Intent.ACTION_HEADSET_PLUG);
			headSetUnPluggedintent
					.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
			headSetUnPluggedintent.putExtra("state", 1);
			headSetUnPluggedintent.putExtra("name", "Headset");
		}
	}

	private void offHookCallActionMethodOne(Context context, int version) {

		if (version == 7) {
			getTeleService(context);
			telephonyService.endCall();
		} else if ((version > 7) && (version < 16)) {
			Intent headSetUnPluggedintent = new Intent(
					Intent.ACTION_HEADSET_PLUG);
			headSetUnPluggedintent
					.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
			headSetUnPluggedintent.putExtra("state", 0);
			headSetUnPluggedintent.putExtra("name", "Headset");
			sendOrderedBroadcast(headSetUnPluggedintent, null);

		} else if (version > 16) {
			getTeleService(context);
			telephonyService.endCall();
		}

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
}
