package com.heliopause.nope.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.heliopause.nope.Constants;

public class SpamReceiver extends BroadcastReceiver {

    // Debug constants
    private static final boolean DEBUG = false;
    private static final String TAG = SpamReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        // Check is the listener is disabled
        SharedPreferences prefs = context.getSharedPreferences(
                Constants.SETTINGS_PREFS, Context.MODE_PRIVATE);
        boolean turnedOn = prefs.getBoolean(
                Constants.SPAM_BLOCK_SERVICE_STATUS, true);
        if (!turnedOn) {
            return;
        }

        // Grab the bundle from the incoming message
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs;

        String address;
        String body;
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

                if (localConfiguration.mEnabled) {
                    if (DEBUG)
                        Log.d(TAG, "Configuration enabled");
                    if (localConfiguration.checkAndUpdateSMS(context, address,
                            body)) {
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