package com.heliopause.nope.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.heliopause.nope.Constants;

public class BootReceiver extends BroadcastReceiver {

	private SharedPreferences prefs;

	// Receive boot completed system broadcast
	@Override
	public void onReceive(Context context, Intent intent) {
		// initializing shared prefs object
		prefs = PreferenceManager.getDefaultSharedPreferences(context);

		Intent callBlock = new Intent(context, CallBlockService.class);
		Intent textBlock = new Intent(context, MsgBlockService.class);
		Intent spamBlock = new Intent(context, SpamBlockService.class);

		// Check if Services are set to be enabled. If they are, start
		// them. If they aren't, don't do anything.
		if (prefs.getBoolean(Constants.MSG_BLOCK_SERVICE_STATUS, true)) {

			// start service and notify user with toast notification
			context.startService(textBlock);
		} else {
			// do nothing
			return;
		}
		if (prefs.getBoolean(Constants.CALL_BLOCK_SERVICE_STATUS, true)) {

			// start service and notify user with toast notification
			context.startService(callBlock);
		} else {
			// do nothing
			return;
		}
		if (prefs.getBoolean(Constants.SPAM_BLOCK_SERVICE_STATUS, true)) {

			// start service and notify user with toast notification
			context.startService(spamBlock);
		} else {
			// do nothing
			return;
		}
	}
}