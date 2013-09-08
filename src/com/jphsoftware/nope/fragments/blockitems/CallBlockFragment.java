package com.jphsoftware.nope.fragments.blockitems;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.InputType;
import android.util.Log;
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
import com.jphsoftware.nope.R;
import com.jphsoftware.nope.database.BlockItem;
import com.jphsoftware.nope.database.BlockItemDataSource;

public class CallBlockFragment extends SherlockListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	// Class debugging tags
	private static final String TAG = CallBlockFragment.class.getSimpleName();
	private static final boolean DEBUG = true;

	private ListView listView;
	private BlockItemDataSource db;
	private BlocklistAdapter adapter;
	private ActionMode mMode;

	boolean mListShown;
	View mProgressContainer;
	View mListContainer;

	private static final int LOADER_ID = 1;

	public CallBlockFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		db = new BlockItemDataSource(getSherlockActivity());
		Cursor c = db.query(BlockItemDataSource.CALLBLOCK_TABLE_NAME);
		adapter = new BlocklistAdapter(getSherlockActivity(), c,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		db = new BlockItemDataSource(getSherlockActivity());
		Cursor c = db.query(BlockItemDataSource.CALLBLOCK_TABLE_NAME);
		adapter = new BlocklistAdapter(getSherlockActivity(), c,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		listView = (ListView) getSherlockActivity().findViewById(
				android.R.id.list);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				Cursor cursor = adapter.getCursor();
				cursor.moveToPosition(position);
				onListItemSelect(position);
				return true;
			}
		});
		if (DEBUG) {
			Log.d(TAG, "+++ Calling initLoader()! +++");
			if (getLoaderManager().getLoader(LOADER_ID) == null) {
				Log.d(TAG, "+++ Initializing the new Loader... +++");
			} else {
				Log.d(TAG,
						"+++ Reconnecting with existing Loader (id '1')... +++");
			}
		}
		// Initialize a Loader with id '1'. If the Loader with this id already
		// exists, then the LoaderManager will reuse the existing Loader.
		getLoaderManager().initLoader(LOADER_ID, null, this);
		listView.setAdapter(adapter);

		// new UiThread().execute();
	}

	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
		return new BlockItemCursorLoader(getActivity());
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (DEBUG) {
			Log.d(TAG, "+++ onLoadFinished() called! +++");
		}

		// Swap the new cursor in. (The framework will take care of closing the
		// old cursor once we return)
		adapter.swapCursor(cursor);
		if (cursor.getCount() == 0) {
			((TextView) getListView().getEmptyView())
					.setText(getSherlockActivity().getResources().getString(
							R.string.empty_list));
			// TextView tvEmpty = (TextView) getListView().getEmptyView();
		}
		// The list should now be shown.
		if (isResumed()) {
			// setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.swapCursor(null);
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_call_blocklist,
				container, false);
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
				adapter.notifyDataSetChanged();

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
							// Fill in for call block item addition
							db.insert(
									BlockItemDataSource.CALLBLOCK_TABLE_NAME,
									DbUtil.generateContentValuesFromObject(new BlockItem(
											phoneNum)));
							adapter.notifyDataSetChanged();
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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	private void onListItemSelect(int position) {

		SparseBooleanArray checked = listView.getCheckedItemPositions();
		boolean hasCheckedElement = false;
		for (int i = 0; i < checked.size() && !hasCheckedElement; i++) {
			hasCheckedElement = checked.valueAt(i);
		}

		if (hasCheckedElement) {
			if (mMode == null) {
				mMode = (getSherlockActivity())
						.startActionMode(new ModeCallback());
				mMode.setTitle(String.valueOf(checked.size()) + " selected");
				mMode.invalidate();
			} else {
				mMode.setTitle(String.valueOf(checked.size()) + " selected");
				mMode.invalidate();
			}
		} else {
			if (mMode != null) {
				mMode.finish();
			}
		}
	}

	protected final class ModeCallback implements ActionMode.Callback {

		private View mMultiSelectActionBarView;
		@SuppressWarnings("unused")
		private TextView mSelectedConvCount;
		@SuppressWarnings("unused")
		private HashSet<Integer> mSelectedItemIds;

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// Create the menu from the xml file
			MenuInflater inflater = getSherlockActivity()
					.getSupportMenuInflater();
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
				System.err.println("Item count: " + itemCount);

				for (int i = itemCount - 1; i >= 0; i--) {
					if (checkedItemPositions.get(i)) {
						db.remove(BlockItemDataSource.CALLBLOCK_TABLE_NAME, i);
					}
				}
				checkedItemPositions.clear();
				adapter.notifyDataSetChanged();

				mode.finish(); // Action picked, so close the CAB
				return true;
			default:
				return false;
			}

		}
	}

	public static final class BlockItemCursorLoader extends SimpleCursorLoader {

		Context mContext;

		public BlockItemCursorLoader(Context context) {
			super(context);

			mContext = context;
		}

		@Override
		public Cursor loadInBackground() {
			BlockItemDataSource datasource = new BlockItemDataSource(mContext);

			return datasource.query(BlockItemDataSource.CALLBLOCK_TABLE_NAME);
		}

	}

}
