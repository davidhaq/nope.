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
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jphsoftware.nope.R;

public class CallBlockFragment extends SherlockFragment {

	private int mLayoutRes;

	public CallBlockFragment() {

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
		mLayoutRes = layouts.getResourceId(0, 0);

		// customize the ActionBar
		String[] menuListArray = getResources().getStringArray(
				R.array.menu_list);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(menuListArray[0]);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.show();

		ListView view = (ListView) inflater.inflate(mLayoutRes, null);
		view.setAdapter(new CallBlocklistAdapter(getActivity(), menuListArray, menuListArray));
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
			addCallBlock();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void addCallBlock() {

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

	public class CallBlocklistAdapter extends BaseAdapter {

		String[] phoneNums, lastCalled;
		private LayoutInflater inflater;

		public CallBlocklistAdapter(Context context, String[] phoneNums,
				String[] lastCalled) {
			super();
			this.phoneNums = phoneNums;
			this.lastCalled = lastCalled;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			CallBlockItem holder;
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.call_block_item, null);
				holder = new CallBlockItem();
				holder.phoneNum = (TextView) convertView.findViewById(R.id.phoneNum);
				holder.lastCall = (TextView) convertView
						.findViewById(R.id.lastCalled);
				holder.icon = (ImageView) convertView.findViewById(R.id.call_block_item_icon);
				convertView.setTag(holder);
			} else {
				holder = (CallBlockItem) convertView.getTag();
			}

			holder.phoneNum.setText(phoneNums[position]);
			holder.lastCall.setText("Last Called:" +lastCalled[position]);
			holder.icon.setImageResource(R.id.call_block_item_icon);

			return convertView;
		}

		private class CallBlockItem {
			TextView phoneNum;
			TextView lastCall;
			ImageView icon;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
}
