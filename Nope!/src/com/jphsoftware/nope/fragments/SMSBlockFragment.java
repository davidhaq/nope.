package com.jphsoftware.nope.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jphsoftware.nope.R;

public class SMSBlockFragment extends SherlockFragment {

	private int mLayoutRes;

	public SMSBlockFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		setHasOptionsMenu(true);
	}

	@Override
	public void onStop() {
		super.onStop();
		setHasOptionsMenu(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		TypedArray layouts = getResources().obtainTypedArray(
				R.array.layout_resources_list);
		mLayoutRes = layouts.getResourceId(1, 0);

		// customize the ActionBar
		String[] menuListArray = getResources().getStringArray(
				R.array.menu_list);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(menuListArray[1]);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.show();

		ListView view = (ListView) inflater.inflate(mLayoutRes, null);
		view.setAdapter(new SMSBlocklistAdapter(getActivity()));
		layouts.recycle();
		return view;

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getSherlockActivity().getSupportMenuInflater().inflate(
				R.menu.call_block_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.add_call_block:
			addSMSBlock();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addSMSBlock() {

		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
		alert.setTitle("Add Number");
		alert.setMessage("Please enter a phone number below");

		final EditText input = new EditText(getActivity());
		input.setInputType(InputType.TYPE_CLASS_PHONE);
		alert.setView(input);
		alert.setCancelable(true);
		alert.setPositiveButton("Done", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				String phoneNum = input.getText().toString();
				Toast.makeText(getSherlockActivity(), phoneNum,
						Toast.LENGTH_LONG).show();

			}

		});
		alert.create().show();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	private class SMSBlockItem {
		public String tag;
		public int iconRes;

		public SMSBlockItem(String tag, int iconRes) {
			this.tag = tag;
			this.iconRes = iconRes;
		}
	}

	public class SMSBlocklistAdapter extends ArrayAdapter<SMSBlockItem> {

		public SMSBlocklistAdapter(Context context) {
			super(context, 0);
			// TODO Auto-generated constructor stub
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.slide_menu_row, null);
			}
			return null;
		}

	}
}
