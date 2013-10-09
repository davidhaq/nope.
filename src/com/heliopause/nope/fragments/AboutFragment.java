package com.heliopause.nope.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.support.v4.preference.PreferenceFragment;
import android.webkit.WebView;

import com.heliopause.nope.Constants;
import com.heliopause.nope.R;

public class AboutFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener, OnPreferenceClickListener {

	private Preference mRateAppPref;
	private Preference mContactDevPref;
	private Preference mReportBugPref;
	private Preference mOpenSourcePref;
	private Preference mVersionPref;

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

		// Setup pref items
		mRateAppPref = (Preference) getPreferenceScreen().findPreference(
				Constants.ABOUT_KEY_RATE_APP);
		mRateAppPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						final String appName = "com.heliopause.nope";
						try {
							startActivity(new Intent(Intent.ACTION_VIEW, Uri
									.parse("market://details?id=" + appName)));
						} catch (android.content.ActivityNotFoundException anfe) {
							startActivity(new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("http://play.google.com/store/apps/details?id="
											+ appName)));
						}
						return true;
					}

				});
		mContactDevPref = (Preference) getPreferenceScreen().findPreference(
				Constants.ABOUT_KEY_CONTACT_DEV);
		mContactDevPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						Uri data = Uri.parse("mailto:"
								+ getActivity().getString(
										R.string.developer_email));
						intent.setData(data);
						startActivity(intent);
						return true;
					}
				});
		mReportBugPref = (Preference) getPreferenceScreen().findPreference(
				Constants.ABOUT_KEY_REPORT_BUG);
		mReportBugPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {

						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(getActivity().getString(
								R.string.report_bug_url)));
						startActivity(intent);
						return true;
					}
				});
		mOpenSourcePref = (Preference) getPreferenceScreen().findPreference(
				Constants.ABOUT_KEY_OPEN_SOURCE);
		mOpenSourcePref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						mTextDlg.show();
						return true;
					}
				});
		mVersionPref = (Preference) getPreferenceScreen().findPreference(
				Constants.ABOUT_KEY_VERSION);

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