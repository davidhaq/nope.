package com.jphsoftware.nope.fragments;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jphsoftware.nope.Constants;
import com.jphsoftware.nope.R;

public class CallBlockFragment extends SherlockListFragment {

	private ListView listView;
	private CallBlocklistAdapter callBlockAdapter;
	private GsonBuilder gsonb;
	private Gson gson;
	private SharedPreferences sharedPrefs;
	private static int s180DipInPixel = -1;

	public CallBlockFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		callBlockAdapter = new CallBlocklistAdapter(getActivity(),
				getCallBlockDataArrayList(Constants.BLOCKED_NUMBERS).toArray(
						new String[getCallBlockDataArrayList(
								Constants.BLOCKED_NUMBERS).size()]),
				getCallBlockDataArrayList(Constants.BLOCKED_NUMBERS_LAST_CALL)
						.toArray(
								new String[getCallBlockDataArrayList(
										Constants.BLOCKED_NUMBERS_LAST_CALL)
										.size()]));
		gsonb = new GsonBuilder();
		gson = gsonb.create();

		sharedPrefs = getActivity().getSharedPreferences(
				Constants.CALLBLOCK_DATA, Context.MODE_PRIVATE);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		callBlockAdapter = new CallBlocklistAdapter(getActivity(),
				getCallBlockDataArrayList(Constants.BLOCKED_NUMBERS).toArray(
						new String[getCallBlockDataArrayList(
								Constants.BLOCKED_NUMBERS).size()]),
				getCallBlockDataArrayList(Constants.BLOCKED_NUMBERS_LAST_CALL)
						.toArray(
								new String[getCallBlockDataArrayList(
										Constants.BLOCKED_NUMBERS_LAST_CALL)
										.size()]));
		gsonb = new GsonBuilder();
		gson = gsonb.create();

		sharedPrefs = getActivity().getSharedPreferences(
				Constants.CALLBLOCK_DATA, Context.MODE_PRIVATE);
		listView.setAdapter(callBlockAdapter);
//		new UiThread().execute();
	}

	@Override
	public void onResume() {
		super.onResume();
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

	public ArrayList<String> getCallBlockDataArrayList(String callData) {
		SharedPreferences prefs = getActivity().getSharedPreferences(
				Constants.CALLBLOCK_DATA, Context.MODE_PRIVATE);
		gsonb = new GsonBuilder();
		gson = gsonb.create();
		String value = prefs.getString(callData, null);
//		System.err.println("Value: " + value);
		if (value != null) {
//			System.err.println("String Value not null!");

			@SuppressWarnings("unused")
			String nullOrNot;
//			System.err.println(nullOrNot = (gson != null) ? "gson is not null"
//					: "gson is null");
			String[] list = gson.fromJson(value, String[].class);
			ArrayList<String> arrayList = new ArrayList<String>(
					Arrays.asList(list));
//			System.err.println("Array contents" + arrayList.toString());
			return arrayList;
		} else {
//			System.err
//					.println("String value is null, so creating a blank arraylist");
			ArrayList<String> arrayList = new ArrayList<String>();
			return arrayList;
		}

	}

	public ArrayList<String> addPhoneNumbertoArraylist(String phoneNumber) {
		ArrayList<String> returnArray = getCallBlockDataArrayList(Constants.BLOCKED_NUMBERS);
		if (!getCallBlockDataArrayList(Constants.BLOCKED_NUMBERS).contains(
				phoneNumber)) {
			returnArray.add(phoneNumber);
			System.err.println("Added phone number to arraylist");
			return returnArray;
		} else {
			System.err.println("ArrayList already has that number!");
			return returnArray;
		}

	}

	private ArrayList<String> removePhoneNumberFromArrayList(String phoneNum) {
		ArrayList<String> returnArray = getCallBlockDataArrayList(Constants.BLOCKED_NUMBERS);
		if (getCallBlockDataArrayList(Constants.BLOCKED_NUMBERS).contains(
				phoneNum)) {
			returnArray.remove(phoneNum);
			return returnArray;
		} else {
			System.err.println("ArrayList doesn't have that number!");
			return returnArray;
		}
	}

	public void writeArrayToPrefs(String[] array, String prefs) {
		String value = gson.toJson(array);
		System.err.println(value);
		Editor e = sharedPrefs.edit();
		e.putString(prefs, value);
		e.commit();
	}

	public void updateList() {
		callBlockAdapter = new CallBlocklistAdapter(getActivity(),
				getCallBlockDataArrayList(Constants.BLOCKED_NUMBERS).toArray(
						new String[getCallBlockDataArrayList(
								Constants.BLOCKED_NUMBERS).size()]),
				getCallBlockDataArrayList(Constants.BLOCKED_NUMBERS_LAST_CALL)
						.toArray(
								new String[getCallBlockDataArrayList(
										Constants.BLOCKED_NUMBERS_LAST_CALL)
										.size()]));
		callBlockAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_call_blocklist,
				container, false);
		callBlockAdapter = new CallBlocklistAdapter(getSherlockActivity(),
				getCallBlockDataArrayList(Constants.BLOCKED_NUMBERS).toArray(
						new String[getCallBlockDataArrayList(
								Constants.BLOCKED_NUMBERS).size()]),
				getCallBlockDataArrayList(Constants.BLOCKED_NUMBERS_LAST_CALL)
						.toArray(
								new String[getCallBlockDataArrayList(
										Constants.BLOCKED_NUMBERS_LAST_CALL)
										.size()]));
		TypedArray layouts = getResources().obtainTypedArray(
				R.array.layout_resources_list);
		layouts.getResourceId(0, 0);

		// customize the ActionBar
		String[] menuListArray = getResources().getStringArray(
				R.array.menu_list);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(menuListArray[0]);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.show();
		listView = (ListView) view.findViewById(android.R.id.list);
		// listView = (ListView) inflater.inflate(mLayoutRes, null);
		// listView.setAdapter(callBlockAdapter);
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

		final EditText input = new EditText(getActivity());
		input.setInputType(InputType.TYPE_CLASS_PHONE);

		final AlertDialog alert = new AlertDialog.Builder(getActivity())
				.setTitle("Add Number")
				.setMessage("Please enter a phone number below").setView(input)
				.setCancelable(true)
				.setPositiveButton("Done", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						System.out
								.println("Doing nothing since we're overriding");
					}

				}).create();

		alert.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				System.err.println("Dismissing dialog");
				new UiThread().execute();

			}
		});

		alert.setOnShowListener(new DialogInterface.OnShowListener() {

			@Override
			public void onShow(DialogInterface dialog) {

				Button b = alert.getButton(DialogInterface.BUTTON_POSITIVE);
				b.setOnClickListener(new View.OnClickListener() {

					// Overriding the positive button on click to prevent the
					// dialog from dismissing with an incorrectly formatted
					// number.
					@Override
					public void onClick(View view) {
						String phoneNum = input.getText().toString();
						Pattern validPhone = Pattern
								.compile("\\(\\d{3}\\)\\d{3}-\\d{4}");
						Matcher matcher = validPhone.matcher(phoneNum);

						if (matcher.matches()) {
							Toast.makeText(getSherlockActivity(),
									"Phone number matches!:" + phoneNum,
									Toast.LENGTH_LONG).show();
							addToBlockList(phoneNum);
							new UiThread().execute();
							alert.dismiss();
						} else {
							Toast.makeText(
									getSherlockActivity(),
									"Phone number must be in the format (XXX)XXX-XXXX. Please try again.",
									Toast.LENGTH_LONG).show();
							input.requestFocus();
						}

						// Dismiss once everything is OK.
						// alert.dismiss();
					}
				});
			}
		});
		alert.show();

	}

	protected void addToBlockList(String phoneNum) {

		writeArrayToPrefs(
				addPhoneNumbertoArraylist(phoneNum).toArray(
						new String[getCallBlockDataArrayList(
								Constants.BLOCKED_NUMBERS).size()]),
				Constants.BLOCKED_NUMBERS);

	}

	protected void removeFromBlockList(String phoneNum) {
		writeArrayToPrefs(
				removePhoneNumberFromArrayList(phoneNum).toArray(
						new String[getCallBlockDataArrayList(
								Constants.BLOCKED_NUMBERS).size()]),
				Constants.BLOCKED_NUMBERS);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	public class CallBlocklistAdapter extends BaseAdapter implements
			ListAdapter {

		String[] phoneNums, lastCalled;
		private LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		public CallBlocklistAdapter(Context context, String[] phoneNums,
				String[] lastCalled) {
			super();
			this.phoneNums = phoneNums;
			this.lastCalled = lastCalled;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			String name = null;
			String lastContacted = null;
			String contactId = null;
			Uri contactUri = null;
			CallBlockItem holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.call_block_item, null);
				holder = new CallBlockItem();
				holder.primaryActionView = convertView
						.findViewById(R.id.primary_action_view);
				holder.quickContactView = (QuickContactBadge) convertView
						.findViewById(R.id.quick_contact_photo);
				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.phoneNum = (TextView) convertView
						.findViewById(R.id.phoneNum);
				holder.lastCall = (TextView) convertView
						.findViewById(R.id.lastCalled);
				convertView.setTag(holder);
			} else {
				holder = (CallBlockItem) convertView.getTag();
			}

			Uri lookupUri = Uri.withAppendedPath(
					PhoneLookup.CONTENT_FILTER_URI,
					Uri.encode(phoneNums[position]));
			String[] mPhoneNumberProjection = { PhoneLookup.DISPLAY_NAME,
					BaseColumns._ID, PhoneLookup.LAST_TIME_CONTACTED };
			Cursor cur = getActivity().getContentResolver().query(lookupUri,
					mPhoneNumberProjection, null, null, null);

			if (cur.moveToFirst()) {

				name = cur
						.getString(cur
								.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
				contactId = cur.getString(cur
						.getColumnIndex(BaseColumns._ID));
				contactUri = Uri.withAppendedPath(
						ContactsContract.Contacts.CONTENT_URI,
						String.valueOf(contactId));
				lastContacted = cur.getString(cur
						.getColumnIndex(PhoneLookup.LAST_TIME_CONTACTED));
				holder.quickContactView.assignContactFromPhone(
						phoneNums[position], true);
				loadThumbnail(holder.quickContactView, contactUri);
				InputStream input = ContactsContract.Contacts
						.openContactPhotoInputStream(getActivity()
								.getContentResolver(), contactUri);
				holder.quickContactView.setImageBitmap(BitmapFactory
						.decodeStream(input));
				
				holder.name.setText(name);
				holder.phoneNum.setText(phoneNums[position]);
				holder.lastCall.setText(lastContacted);
				cur.close();
			} else {
				holder.quickContactView.assignContactFromPhone(
						phoneNums[position], true);
				loadThumbnail(holder.quickContactView, null);
				holder.name.setText(phoneNums[position]);
				holder.phoneNum.setText("-");
				holder.lastCall.setVisibility(View.INVISIBLE);
				cur.close();
			}

			holder.primaryActionView.setVisibility(View.VISIBLE);
			// holder.phoneNum.setText(phoneNums[position]);
			// holder.lastCall.setText("Last Called:" + lastCalled[position]);
			// holder.lastCall.setText("Last Called:");

			return convertView;
		}

		private class CallBlockItem {
			/** The quick contact badge for the contact. */
			QuickContactBadge quickContactView;
			/** The primary action view of the entry. */
			View primaryActionView;
			TextView phoneNum;
			TextView name;
			TextView lastCall;
		}

		@Override
		public int getCount() {
			return phoneNums.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return phoneNums[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

	}
	public static int getDefaultAvatarResId(Context context, int extent) {
        // TODO: Is it worth finding a nicer way to do hires/lores here? In practice, the
        // default avatar doesn't look too different when stretched
        if (s180DipInPixel == -1) {
            Resources r = context.getResources();
            s180DipInPixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180,
                    r.getDisplayMetrics());
        }

        final boolean hires = (extent != -1) && (extent > s180DipInPixel);
        return getDefaultAvatarResId(hires);
    }

    public static int getDefaultAvatarResId(boolean hires) {
        if (hires) return R.drawable.ic_contact_picture_180_holo_light;
        return R.drawable.ic_contact_picture_holo_light;
    }
	public void loadThumbnail(ImageView view, Uri contactUri ) {
        if (contactUri == null) {
            // No photo is needed
        	view.setImageResource(getDefaultAvatarResId(view.getContext(), -1));
        } else {
        	
        	InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(getActivity()
							.getContentResolver(), contactUri);
			view.setImageBitmap(BitmapFactory
					.decodeStream(input));
        }
    }

	private class UiThread extends AsyncTask<Void, Void, Void> {
		CallBlocklistAdapter adapter = callBlockAdapter;

		@Override
		protected void onPreExecute() {
			getActivity().setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			updateList();
			return null;
		}

		@Override
		protected void onPostExecute(Void results) {
			listView.setAdapter(adapter);
			getActivity().setProgressBarIndeterminateVisibility(false);
		}

	}
}
