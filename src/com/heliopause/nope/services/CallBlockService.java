package com.heliopause.nope.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallBlockService extends Service {

    // Debugging constants
    private static final boolean DEBUG = false;
    private static final String TAG = CallBlockService.class.getSimpleName();

    private static CallReceiver receiver = new CallReceiver();
    // --Commented out by Inspection (11/8/13, 1:39 AM):SharedPreferences prefs;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (DEBUG)
            Log.d(TAG, "onCreate called.");
        IntentFilter filter = new IntentFilter();
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        filter.setPriority(999);
        registerReceiver(receiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG)
            Log.d(TAG, "onDestroy called.");
        unregisterReceiver(receiver);
    }

    @Override
    public void onStart(Intent intent, int startid) {
        if (DEBUG)
            Log.d(TAG, "onStart called.");
        startForeground(startid, new Notification());
    }
}
