package com.heliopause.nope.fragments;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;

import com.heliopause.nope.R;

public class AboutFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	private Preference mVersionPref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.about_list);

		mVersionPref = (Preference) getPreferenceScreen().findPreference(
				"nope_version_key");

	}

	@Override
	public void onResume() {
		super.onResume();
		mVersionPref.setSummary(getPreferenceScreen().getSharedPreferences()
				.getString("nope_version_key", "Unknown version"));
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
		if (key.equals("nope_version_key")) {
			mVersionPref.setSummary(sharedPreferences.getString(
					"nope_version_key", "Unknown version"));
		}

	}
}