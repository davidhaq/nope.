package com.heliopause.nope.services;

import com.heliopause.nope.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class BootReceiver extends BroadcastReceiver {

	private SharedPreferences prefs;

	// Receive boot completed system broadcast
	@Override
	public void onReceive(Context context, Intent intent) {
		// initializing shared prefs object
		prefs = context.getSharedPreferences(Constants.SETTINGS_PREFS,
				Context.MODE_PRIVATE);

		Intent callBlock = new Intent(context, CallBlockService.class);
		Intent textBlock = new Intent(context, MsgBlockService.class);

		// Check if Spies GEAPM Service is set to be enabled. If it is, start
		// the service. If it isn't don't do anything.
		if (prefs.getBoolean(Constants.SERVICE_STATUS, false)) {

			// start service and notify user with toast notification
			context.startService(i);
			Toast.makeText(context, "GEAPM Service is starting",
					Toast.LENGTH_SHORT).show();
		} else {
			// do nothing
			return;
		}
	}