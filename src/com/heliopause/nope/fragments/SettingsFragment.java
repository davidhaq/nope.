package com.heliopause.nope.fragments;

import com.heliopause.nope.Constants;
import com.heliopause.nope.R;
import com.heliopause.nope.services.CallBlockService;
import com.heliopause.nope.services.MsgBlockService;
import com.heliopause.nope.services.SpamBlockService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;
import android.util.Log;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		Log.d("SettingsFragment", "" + Build.VERSION.SDK_INT);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			addPreferencesFromResource(R.xml.preferences_compat);
		} else {
			addPreferencesFromResource(R.xml.preferences);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onPause() {
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
		super.onPause();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (key.equals(Constants.CALL_BLOCK_SERVICE_STATUS)) {
			if (sharedPreferences.getBoolean(
					Constants.CALL_BLOCK_SERVICE_STATUS, false)) {
				getActivity().startService(
						new Intent(getActivity(), CallBlockService.class));
			} else {
				getActivity().stopService(
						new Intent(getActivity(), CallBlockService.class));
			}
		}
		if (key.equals(Constants.MSG_BLOCK_SERVICE_STATUS)) {
			if (sharedPreferences.getBoolean(
					Constants.MSG_BLOCK_SERVICE_STATUS, false)) {
				getActivity().startService(
						new Intent(getActivity(), MsgBlockService.class));
			} else {
				getActivity().stopService(
						new Intent(getActivity(), MsgBlockService.class));
			}
		}
		if (key.equals(Constants.SPAM_BLOCK_SERVICE_STATUS)) {
			if (sharedPreferences.getBoolean(
					Constants.SPAM_BLOCK_SERVICE_STATUS, false)) {
				getActivity().startService(
						new Intent(getActivity(), SpamBlockService.class));
			} else {
				getActivity().stopService(
						new Intent(getActivity(), SpamBlockService.class));
			}
		}

	}

}
