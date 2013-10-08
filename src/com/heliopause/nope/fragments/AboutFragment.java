package com.heliopause.nope.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.preference.PreferenceFragment;
import android.webkit.WebView;

import com.heliopause.nope.Constants;
import com.heliopause.nope.R;

public class AboutFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener, OnPreferenceClickListener {

	private Preference mVersionPref;
	private Preference mOpenSourcePref;

	private AlertDialog mTextDlg;
	private WebView mWebView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.about_list);

		mWebView = new WebView(getPreferenceScreen().getContext());
		mWebView.loadUrl("file:///android_asset/licenses/licenses_html");

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCancelable(true).setView(mWebView)
				.setTitle(R.string.license_title);
		mTextDlg = builder.create();
		mTextDlg.setOnDismissListener(new OnDismissListener() {

			public void onDismiss(DialogInterface dialog) {
				dialog.dismiss();
			}
		});

		mVersionPref = (Preference) getPreferenceScreen().findPreference(
				"nope_version_key");
		mOpenSourcePref = (Preference) getPreferenceScreen().findPreference(
				Constants.OPEN_SOURCE_LICENSE_KEY);
		mOpenSourcePref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						mTextDlg.show();
						return true;
					}
				});

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
	public void onDestroy() {
		if (mTextDlg != null && mTextDlg.isShowing()) {
			mTextDlg.dismiss();
		}
		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("nope_version_key")) {
			mVersionPref.setSummary(sharedPreferences.getString(
					"nope_version_key", "Unknown version"));
		}

	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		return false;
	}
}