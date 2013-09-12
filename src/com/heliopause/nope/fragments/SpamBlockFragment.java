package com.heliopause.nope.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.heliopause.nope.R;

public class SpamBlockFragment extends SherlockFragment {

	private CompoundButton toggle;
	private MenuItem toggleMenuItem;
	private SharedPreferences prefs;
	private TextView message;

	private Boolean toggleState = null;
	private static final String SPAM_TOGGLE_KEY = "spam_block_service_state";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		String[] menuListArray = getResources().getStringArray(
				R.array.menu_list);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(menuListArray[0]);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.show();

		setHasOptionsMenu(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_spamblock, container,
				false);
		message = (TextView) view.findViewById(R.id.spamblock_text);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		prefs = getSherlockActivity().getPreferences(Context.MODE_PRIVATE);
		toggleState = prefs.getBoolean(SPAM_TOGGLE_KEY, false);
		if (toggleState) {
			message.setText(getSherlockActivity().getResources().getString(
					R.string.spam_block_on));
		} else {
			message.setText(getSherlockActivity().getResources().getString(
					R.string.spam_block_off));
		}

		getSherlockActivity().getSupportMenuInflater().inflate(
				R.menu.spam_menu, menu);
		toggleMenuItem = menu.findItem(R.id.spam_block_toggle);
		toggle = (CompoundButton) toggleMenuItem.getActionView().findViewById(
				R.id.toggle_spam_button);
		toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (prefs != null) {
					if (isChecked) {
						//Placeholder to turn on spam blocking service
						message.setText(getSherlockActivity().getResources()
								.getString(R.string.spam_block_on));
					} else {
						//placeholder to turn off spam blocking service
						message.setText(getSherlockActivity().getResources()
								.getString(R.string.spam_block_off));
					}
					toggle.setChecked(isChecked);
					Editor editor = prefs.edit();
					editor.putBoolean(SPAM_TOGGLE_KEY, isChecked);
					editor.commit();

				}

			}
		});
		toggle.setChecked(toggleState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

}
