package com.jphsoftware.nope.fragments.blockitems;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

	// Class debugging tags
	private static final String TAG = CallBlockFragment.class.getSimpleName();
	private static final boolean DEBUG = true;

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
		return inflater.inflate(R.layout.fragment_call_blocklist, container,
				false);
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

							ContentValues values = new ContentValues(1);
							values.put(BlockItemTable.COLUMN_NUMBER, input
									.getText().toString());
							loader.insert(BlockItemTable.CALLBLOCK_TABLE_NAME,
									null, values);

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

}
