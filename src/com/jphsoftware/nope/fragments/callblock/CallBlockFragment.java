package com.jphsoftware.nope.fragments.callblock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jphsoftware.nope.BlockItem;
import com.jphsoftware.nope.Constants;
import com.jphsoftware.nope.R;

public class CallBlockFragment extends SherlockListFragment {

	private ListView listView;
	private BlocklistAdapter callBlockAdapter;
	List<BlockItem> callBlocks;

	private GsonBuilder gsonb;
	private Gson gson;
	private SharedPreferences sharedPrefs;
	private ActionMode mMode;

	public CallBlockFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (callBlocks == null) {
			setCallBlockList();
		}
		callBlockAdapter = new BlocklistAdapter(getSherlockActivity(),
				R.layout.call_block_item, callBlocks);
		gsonb = new GsonBuilder();
		gson = gsonb.create();

		sharedPrefs = getActivity().getSharedPreferences(
				Constants.CALLBLOCK_DATA, Context.MODE_PRIVATE);

	}

	private void setCallBlockList() {
		callBlocks = new ArrayList<BlockItem>();
		String[] numbers = getCallBlockDataArrayList(Constants.BLOCKED_NUMBERS)
				.toArray(
						new String[getCallBlockDataArrayList(
								Constants.BLOCKED_NUMBERS).size()]);

		for (int i = 0; i < numbers.length; i++) {
			BlockItem block = new BlockItem(numbers[i]);
			callBlocks.add(block);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		callBlockAdapter = new BlocklistAdapter(getSherlockActivity(),
				R.layout.call_block_item, callBlocks);
		gsonb = new GsonBuilder();
		gson = gsonb.create();

		sharedPrefs = getActivity().getSharedPreferences(
				Constants.CALLBLOCK_DATA, Context.MODE_PRIVATE);
		listView.setAdapter(callBlockAdapter);

		// new UiThread().execute();
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
		// System.err.println("Value: " + value);
		if (value != null) {
			// System.err.println("String Value not null!");

			@SuppressWarnings("unused")
			String nullOrNot;
			// System.err.println(nullOrNot = (gson != null) ?
			// "gson is not null"
			// : "gson is null");
			String[] list = gson.fromJson(value, String[].class);
			ArrayList<String> arrayList = new ArrayList<String>(
					Arrays.asList(list));
			// System.err.println("Array contents" + arrayList.toString());
			return arrayList;
		} else {
			// System.err
			// .println("String value is null, so creating a blank arraylist");
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
		setCallBlockList();
		callBlockAdapter = new BlocklistAdapter(getSherlockActivity(),
				R.layout.call_block_item, callBlocks);
		callBlockAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_call_blocklist,
				container, false);
		callBlockAdapter = new BlocklistAdapter(getSherlockActivity(),
				R.layout.call_block_item, callBlocks);
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

		System.err.println("onCreateView called");
		listView = (ListView) view.findViewById(android.R.id.list);

		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				System.err.println("onCreateView called");
				onListItemSelect(position);
				return true;
			}
		});

		// listView = (ListView) inflater.inflate(R.id.call_block_list,
		// container);
		// listView.setAdapter(callBlockAdapter);
		layouts.recycle();
		return view;

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		if (mMode == null) {
			/*
			 * no items selected, so perform item click actions like moving to
			 * next activity
			 */
			System.err.println("No Items selected");

		} else {// add or remove selection for current list item
			onListItemSelect(position);
			v.setSelected(true);
		}
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

	private class UiThread extends AsyncTask<Void, Void, Void> {
		BlocklistAdapter adapter = callBlockAdapter;

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

	private void onListItemSelect(int position) {

		callBlockAdapter.toggleSelection(position);
		boolean hasCheckedItems = callBlockAdapter.getSelectedCount() > 0;

		if (hasCheckedItems && mMode == null)
			// there are some selected items, start the actionMode
			mMode = getSherlockActivity().startActionMode(new ModeCallback());
		else if (!hasCheckedItems && mMode != null)
			// there no selected items, finish the actionMode
			mMode.finish();

		if (mMode != null)
			mMode.setTitle(String.valueOf(callBlockAdapter.getSelectedCount())
					+ " selected");
	}

	protected final class ModeCallback implements ActionMode.Callback {

		private View mMultiSelectActionBarView;
		private TextView mSelectedConvCount;
		private HashSet<Integer> mSelectedItemIds;

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Create the menu from the xml file
			MenuInflater inflater = getSherlockActivity()
					.getSupportMenuInflater();
			mSelectedItemIds = new HashSet<Integer>();
			inflater.inflate(R.menu.item_multi_select_menu, menu);

			if (mMultiSelectActionBarView == null) {
				mMultiSelectActionBarView = LayoutInflater
						.from(getSherlockActivity())
						.inflate(
								R.layout.conversation_list_multi_select_actionbar,
								null);

				mSelectedConvCount = (TextView) mMultiSelectActionBarView
						.findViewById(R.id.selected_conv_count);
			}
			mode.setCustomView(mMultiSelectActionBarView);
			((TextView) mMultiSelectActionBarView.findViewById(R.id.title))
					.setText(R.string.select_items);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// Here, you can checked selected items to adapt available actions
			if (mMultiSelectActionBarView == null) {
				ViewGroup v = (ViewGroup) LayoutInflater
						.from(getSherlockActivity())
						.inflate(
								R.layout.conversation_list_multi_select_actionbar,
								null);
				mode.setCustomView(v);

				mSelectedConvCount = (TextView) v
						.findViewById(R.id.selected_conv_count);
			}
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// Destroying action mode, let's unselect all items
			callBlockAdapter.removeSelection();
			mMode = null;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.delete:

				/** Getting the checked items from the listview */
				SparseBooleanArray checkedItemPositions = getListView()
						.getCheckedItemPositions();
				int itemCount = getListView().getCount();
				System.err.println("Item count: "+itemCount);

				for (int i = itemCount - 1; i >= 0; i--) {
					if (checkedItemPositions.get(i)) {
						callBlockAdapter.remove(callBlocks.get(i));
					}
				}
				checkedItemPositions.clear();
				callBlockAdapter.notifyDataSetChanged();

				mode.finish(); // Action picked, so close the CAB
				return true;
			default:
				return false;
			}

		}
	}

}
