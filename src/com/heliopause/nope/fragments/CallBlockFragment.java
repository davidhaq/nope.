package com.heliopause.nope.fragments;

import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

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
	private static final boolean DEBUG = true;

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
			if (DEBUG)
				Log.d(TAG, "Creating new instance of database helper object");

		} else {
			if (DEBUG)
				Log.d(TAG, "Using existing database helper");

		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

		if (DEBUG)
			Log.d(TAG, "++++ onCreateLoader ++++");

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
		if (DEBUG)
			Log.d(TAG, "++++ onLoadFinished ++++");
		CallBlockFragment.loader = (SQLiteCursorLoader) loader;
		mCursor = cursor;
		adapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (DEBUG)
			Log.d(TAG, "++++ onLoaderReset ++++");
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

		Cursor peopleCursor = getSherlockActivity().getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		ContactListAdapter contactadapter = new ContactListAdapter(
				getSherlockActivity(), peopleCursor);
		final MultiAutoCompleteTextView input = new MultiAutoCompleteTextView(
				getActivity());
		input.setInputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE);

		final AlertDialog alert = new AlertDialog.Builder(getSherlockActivity())
				.setTitle("Add Call Block")
				.setMessage("Please enter a phone number below").setView(input)
				.setCancelable(true)
				.setNegativeButton("Cancel", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (DEBUG)
							Log.d(TAG, "Canceling add item dialog");

					}

				}).setPositiveButton("Done", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						String temp = input.getText().toString();
						String phoneNum = PhoneNumberUtils.formatNumber(temp);

						// Create a block item in the database
						ContentValues values = new ContentValues(2);
						values.put(BlockItemTable.COLUMN_NUMBER,
								PhoneNumberUtils.stripSeparators(phoneNum));
						values.put(BlockItemTable.COLUMN_LAST_CONTACT, -1337);
						loader.insert(BlockItemTable.CALLBLOCK_TABLE_NAME,
								null, values);

						dialog.dismiss();
					}

				}).create();

		alert.setOnDismissListener(new DialogInterface.OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {

				adapter.notifyDataSetChanged();

			}
		});
		input.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable editable) {
				if (editable.toString().length() == 0) {
					alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
							false);
				} else {
					alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
							true);
				}

			}
		});

		alert.show();
		alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
		input.setAdapter(contactadapter);
		input.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

	}

	protected void deleteItem(int position) {

		final AlertDialog alert = new AlertDialog.Builder(getSherlockActivity())
				.setTitle("Delete?")
				.setMessage("This block item will be deleted.")
				.setCancelable(true)
				.setNegativeButton("Cancel", new Dialog.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (DEBUG)
							Log.d(TAG, "Canceling delete dialog");

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

	public static class ContactListAdapter extends CursorAdapter implements
			Filterable {

		private LayoutInflater inflator;
		private ContentResolver mContent;

		public ContactListAdapter(Context context, Cursor c) {
			super(context, c, 0);
			inflator = LayoutInflater.from(context);
			mContent = context.getContentResolver();
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View v = inflator.inflate(R.layout.suggest_dropdown_item, parent,
					false);
			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {

			String displayName;
			String phoneNumber;
			String phoneNumberType;
			long contactId;

			ViewHolder holder = new ViewHolder();
			holder.primaryActionView = view
					.findViewById(R.id.primary_action_view);
			holder.imageView = (ImageView) view.findViewById(android.R.id.icon);
			holder.displayName = (TextView) view
					.findViewById(android.R.id.title);
			holder.phoneNumber = (TextView) view
					.findViewById(android.R.id.text1);
			holder.phoneNumberType = (TextView) view
					.findViewById(android.R.id.text2);

			if (cursor.moveToFirst()) {

				contactId = cursor
						.getLong(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
				if (DEBUG)
					Log.d(TAG, "contactId: " + contactId);

				displayName = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				if (DEBUG)
					Log.d(TAG, "displayName: " + displayName.toString());

				phoneNumber = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				if (DEBUG)
					Log.d(TAG, "phoneNumber: " + phoneNumber.toString());

				phoneNumberType = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
				if (DEBUG)
					Log.d(TAG, "phoneNumberType: " + phoneNumberType.toString());

				if (TextUtils.isEmpty(displayName)
						|| TextUtils.equals(displayName, phoneNumber)) {
					displayName = phoneNumber;

				}

				holder.displayName.setText(displayName);

				if (!TextUtils.isEmpty(phoneNumber)) {
					holder.phoneNumber.setText(phoneNumber);
					holder.phoneNumber.setVisibility(View.VISIBLE);
				} else {
					holder.phoneNumber.setText(null);
				}
				if (holder.phoneNumberType != null) {

					holder.phoneNumberType.setText(phoneNumberType);
					holder.phoneNumberType.setVisibility(View.VISIBLE);
				}
				holder.displayName.setVisibility(View.VISIBLE);

				if (holder.imageView != null) {
					holder.imageView.setVisibility(View.VISIBLE);

					addThumbnail(phoneNumber, holder.imageView);

				} else {
					holder.imageView
							.setImageResource(R.drawable.ic_contact_picture_holo_light);
				}
			}
			holder.primaryActionView.setVisibility(View.VISIBLE);

		}

		private class ViewHolder {

			// The primary action view of the entry.
			View primaryActionView;

			// Textviews
			TextView displayName;
			TextView phoneNumber;
			TextView phoneNumberType;

			// The contact badge for the contact.
			ImageView imageView;

		}

		@Override
		public String convertToString(Cursor cursor) {

			return cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			if (getFilterQueryProvider() != null) {
				return getFilterQueryProvider().runQuery(constraint);
			}

			StringBuilder buffer = null;
			String[] args = null;
			if (constraint != null) {
				buffer = new StringBuilder();
				buffer.append("UPPER(");
				buffer.append(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
				buffer.append(") GLOB ?");
				args = new String[] { constraint.toString().toUpperCase(
						Locale.getDefault())
						+ "*" };
			}

			return mContent.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					buffer == null ? null : buffer.toString(), args, null);
		}

		public void addThumbnail(String number, ImageView imageView) {

			final Integer thumbnailId = fetchThumbnailId(number);
			if (thumbnailId != null) {
				final Bitmap thumbnail = fetchThumbnail(thumbnailId);
				if (thumbnail != null) {
					imageView.setImageBitmap(thumbnail);
				} else {
					imageView
							.setImageResource(R.drawable.ic_contact_picture_holo_light);
				}

			}
		}

		private Integer fetchThumbnailId(String number) {

			final Uri uri = Uri.withAppendedPath(
					ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
					Uri.encode(number));
			final Cursor cursor = mContent
					.query(uri, PHOTO_ID_PROJECTION, null, null,
							ContactsContract.Contacts.DISPLAY_NAME + " ASC");

			try {
				Integer thumbnailId = null;
				if (cursor.moveToFirst()) {
					thumbnailId = cursor
							.getInt(cursor
									.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
				}
				return thumbnailId;
			} finally {
				cursor.close();
			}

		}

		public Bitmap fetchThumbnail(int thumbnailId) {

			final Uri uri = ContentUris.withAppendedId(
					ContactsContract.Data.CONTENT_URI, thumbnailId);
			final Cursor cursor = mContent.query(uri, PHOTO_BITMAP_PROJECTION,
					null, null, null);

			try {
				Bitmap thumbnail = null;
				if (cursor.moveToFirst()) {
					final byte[] thumbnailBytes = cursor.getBlob(0);
					if (thumbnailBytes != null) {
						thumbnail = BitmapFactory.decodeByteArray(
								thumbnailBytes, 0, thumbnailBytes.length);
					}
				}
				return thumbnail;
			} finally {
				cursor.close();
			}

		}

		final String[] PHOTO_ID_PROJECTION = new String[] { ContactsContract.Contacts.PHOTO_ID };

		final String[] PHOTO_BITMAP_PROJECTION = new String[] { ContactsContract.CommonDataKinds.Photo.PHOTO };
	}
}
