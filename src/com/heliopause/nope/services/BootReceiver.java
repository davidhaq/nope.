package com.heliopause.nope.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.heliopause.nope.Constants;

public class BootReceiver extends BroadcastReceiver {

	private SharedPreferences settings;

	// Receive boot completed system broadcast
	@Override
	public void onReceive(Context context, Intent intent) {
		// initializing shared prefs object
		settings = PreferenceManager.getDefaultSharedPreferences(context);

		Intent callBlock = new Intent(context, CallBlockService.class);
		Intent textBlock = new Intent(context, MsgBlockService.class);

		// Check if Services are set to be enabled. If they are, start
		// them. If they aren't, don't do anything.
		if (settings.getBoolean(Constants.MSG_BLOCK_SERVICE_STATUS, true)) {

			// start service
			context.startService(textBlock);
		} else {
			// do nothing
			return;
		}
		if (settings.getBoolean(Constants.CALL_BLOCK_SERVICE_STATUS, true)) {

			// start service
			context.startService(callBlock);
		} else {
			// do nothing
			return;
		}
	}
}