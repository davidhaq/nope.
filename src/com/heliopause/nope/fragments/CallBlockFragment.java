package com.heliopause.nope.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.telephony.PhoneNumberUtils;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.commonsware.cwac.loaderex.acl.SQLiteCursorLoader;
import com.heliopause.nope.R;
import com.heliopause.nope.database.BlockItem;
import com.heliopause.nope.database.BlockItemTable;
import com.heliopause.nope.database.DatabaseHelper;
import com.heliopause.nope.fragments.blockitems.BlocklistAdapter;

public class CallBlockFragment extends SherlockListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	// debugging tags
	private static final String TAG = CallBlockFragment.class.getSimpleName();
	private static final boolean DEBUG = false;

	// A few locally used objects
	private DatabaseHelper db = null;
	private BlocklistAdapter adapter = null;
	public static SQLiteCursorLoader loader;
	private Cursor mCursor;
	private static final int LOADER_ID = 1;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Create the DBhelper object
		getHelper();

		// Setup actionbar
		String[] menuListArray = getResources().getStringArray(
				R.array.menu_list);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(menuListArray[0]);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.show();
		setHasOptionsMenu(true);

		// Setup the list adapter
		adapter = new BlocklistAdapter(getSherlockActivity(), null);

		// Set listview and make items long clickable
		ListView listView = (ListView) getSherlockActivity().findViewById(
				android.R.id.list);
		listView.setItemsCanFocus(false);
		listView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				deleteItem(position);
				return true;
			}

		});

		// Actually set the list adapter after all the list setup
		setListAdapter(adapter);

		// Setup the blockitem loader
		getSherlockActivity().getSupportLoaderManager().initLoader(LOADER_ID,
				null, this);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_call_blocklist,
				container, false);
		return view;
	}

	private void getHelper() {
		if (db == null) {
			db = new DatabaseHelper(getSherlockActivity());
			if (DEBUG) {
				Log.d(TAG,
						"Creating a new instance of the database helper object");
			}
		} else {
			if (DEBUG) {
				Log.d(TAG, "Using existing database helper");
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

		if (DEBUG) {
			Log.d(TAG, "++++ onCreateLoader ++++");
		}

		loader = new SQLiteCursorLoader(getSherlockActivity(), db, "SELECT "
				+ BlockItemTable.COLUMN_ID + ", "
				+ BlockItemTable.COLUMN_NUMBER + ", "
				+ BlockItemTable.COLUMN_LAST_CONTACT + " FROM "
				+ BlockItemTable.CALLBLOCK_TABLE_NAME + " ORDER BY "
				+ BlockItemTable.COLUMN_LAST_CONTACT + " DESC", null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (DEBUG) {
			Log.d(TAG, "++++ onLoadFinished ++++");
		}
		CallBlockFragment.loader = (SQLiteCursorLoader) loader;
		mCursor = cursor;
		adapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (DEBUG) {
			Log.d(TAG, "++++ onLoaderReset ++++");
		}
		adapter.changeCursor(null);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getSherlockActivity().getSupportMenuInflater().inflate(
				R.menu.block_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// handle item selection
		switch (item.getItemId()) {
		case R.id.add_block_item:
			add();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void add() {

		final EditText input = new EditText(getActivity());
		input.setInputType(InputType.TYPE_CLASS_PHONE);

		final AlertDialog alert = new AlertDialog.Builder(getSherlockActivity())
				.setTitle("Add Call Block")
				.setMessage("Please enter a phone number below").setView(input)
				.setCancelable(true)
				.setNegativeButton("Cancel", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (DEBUG) {
							Log.d(TAG, "Canceling add item dialog");
						}

					}

				}).setPositiveButton("Done", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}

				}).create();

		alert.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				System.err.println("Dismissing add numberdialog");
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
						String temp = input.getText().toString();
						String phoneNum = PhoneNumberUtils.formatNumber(temp);

						// Create a block item in the database
						ContentValues values = new ContentValues(2);
						values.put(BlockItemTable.COLUMN_NUMBER,
								PhoneNumberUtils.stripSeparators(phoneNum));
						values.put(BlockItemTable.COLUMN_LAST_CONTACT, -1337);
						loader.insert(BlockItemTable.CALLBLOCK_TABLE_NAME,
								null, values);

						alert.dismiss();

					}
				});
			}
		});
		alert.show();

	}

	protected void deleteItem(int position) {

		final AlertDialog alert = new AlertDialog.Builder(getSherlockActivity())
				.setTitle("Delete?")
				.setMessage("This block item will be deleted.")
				.setCancelable(true)
				.setNegativeButton("Cancel", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (DEBUG) {
							Log.d(TAG, "Canceling delete dialog");
						}

					}

				}).setPositiveButton("Delete", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						processDelete(cursorToBlockItem(mCursor));
					}

				}).create();
		alert.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				System.err.println("Dismissing delete dialog");
				adapter.notifyDataSetChanged();

			}
		});
		alert.show();

	}

	protected void processDelete(BlockItem block) {
		loader.delete(BlockItemTable.CALLBLOCK_TABLE_NAME, "_NUMBER=?",
				new String[] { String.valueOf(block.getNumber()) });

	}

	private BlockItem cursorToBlockItem(Cursor cursor) {
		BlockItem block = new BlockItem();
		block.setId(cursor.getInt(cursor
				.getColumnIndex(BlockItemTable.COLUMN_ID)));
		block.setNumber(cursor.getString(cursor
				.getColumnIndex(BlockItemTable.COLUMN_NUMBER)));
		block.setLastContact(cursor.getInt(cursor
				.getColumnIndex(BlockItemTable.COLUMN_LAST_CONTACT)));
		return block;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

}
