package com.jphsoftware.nope.fragments.blockitems;

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
import com.jphsoftware.nope.R;
import com.jphsoftware.nope.database.BlockItemTable;
import com.jphsoftware.nope.database.DatabaseHelper;

public class CallBlockFragment extends SherlockListFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	// debugging tags
	private static final String TAG = CallBlockFragment.class.getSimpleName();
	private static final boolean DEBUG = true;

	// A few locally used objects
	private DatabaseHelper db = null;
	private BlocklistAdapter adapter = null;
	private SQLiteCursorLoader loader = null;
	private static final int LOADER_ID = 1;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		getHelper();

		String[] menuListArray = getResources().getStringArray(
				R.array.menu_list);
		ActionBar actionBar = getSherlockActivity().getSupportActionBar();
		actionBar.setTitle(menuListArray[0]);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.show();

		setHasOptionsMenu(true);

		adapter = new BlocklistAdapter(getSherlockActivity(), null);

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
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		setListAdapter(adapter);

		if (DEBUG) {
			Log.d(TAG, "+++ Calling initLoader()! +++");
			getSherlockActivity().getSupportLoaderManager().initLoader(
					LOADER_ID, null, this);
			if (getLoaderManager().getLoader(LOADER_ID) == null) {
				Log.d(TAG, "+++ Initializing the new Loader... +++");
			} else {
				Log.d(TAG,
						"+++ Reconnecting with existing Loader (id '1')... +++");
			}
		}

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

		loader = new SQLiteCursorLoader(getSherlockActivity(), db, "SELECT "
				+ BlockItemTable.COLUMN_ID + ", "
				+ BlockItemTable.COLUMN_NUMBER + " FROM "
				+ BlockItemTable.CALLBLOCK_TABLE_NAME + " ORDER BY "
				+ BlockItemTable.COLUMN_ID, null);
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		adapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		adapter.changeCursor(null);
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
				.setTitle("Add Number")
				.setMessage("Please enter a phone number below").setView(input)
				.setCancelable(true)
				.setPositiveButton("Done", new Dialog.OnClickListener() {

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
						// Fill in for call block item addition

						ContentValues values = new ContentValues(2);

						values.put(BlockItemTable.COLUMN_NUMBER, phoneNum);
						values.put(BlockItemTable.COLUMN_LAST_CONTACT, 1);
						loader.insert(BlockItemTable.CALLBLOCK_TABLE_NAME,
								null, values);

						alert.dismiss();

						// Dismiss once everything is OK.
						// alert.dismiss();
					}
				});
			}
		});
		alert.show();

	}

	protected void deleteItem(final int position) {

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
						processDelete(position);
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

	protected void processDelete(int position) {
		loader.delete(BlockItemTable.CALLBLOCK_TABLE_NAME, "_ID=?",
				new String[] { String.valueOf(position + 1) });

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

}
