package com.jphsoftware.nope.fragments.blockitems;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.jphsoftware.nope.R;
import com.jphsoftware.nope.database.BlockItemTable;

public class BlocklistAdapter extends CursorAdapter {

	// Debugging
	private static final String TAG = BlocklistAdapter.class.getSimpleName();
	private static final boolean DEBUG = false;

	private LayoutInflater inflator;
	private SparseBooleanArray mSelectedItemsIds;
	private Context context;
	private static int s180DipInPixel = -1;

	public BlocklistAdapter(Context context, Cursor cursor) {
		super(context, cursor, 0);
		this.context = context;
		mSelectedItemsIds = new SparseBooleanArray();
		inflator = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = inflator.inflate(R.layout.call_block_item, parent, false);
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (DEBUG) {
			Log.d(TAG, "+++bindView called+++");
			Log.d(TAG, "number value: " + cursor.getString(1));
		}
		String name;
		String lastContacted;
		String contactId;
		Uri contactUri;

		ViewHolder holder = new ViewHolder();
		holder.primaryActionView = view.findViewById(R.id.primary_action_view);
		holder.quickContactView = (QuickContactBadge) view
				.findViewById(R.id.quick_contact_photo);
		holder.name = (TextView) view.findViewById(R.id.name);
		holder.phoneNum = (TextView) view.findViewById(R.id.phoneNum);
		holder.lastContact = (TextView) view.findViewById(R.id.lastCalled);

		Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(cursor.getString(cursor
						.getColumnIndex(BlockItemTable.COLUMN_NUMBER))));
		String[] mPhoneNumberProjection = { PhoneLookup.DISPLAY_NAME,
				BaseColumns._ID, PhoneLookup.LAST_TIME_CONTACTED };
		Cursor cur = context.getContentResolver().query(lookupUri,
				mPhoneNumberProjection, null, null, null);

		if (cur.moveToFirst()) {

			name = cur.getString(cur
					.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
			contactId = cur.getString(cur.getColumnIndex(BaseColumns._ID));
			contactUri = Uri.withAppendedPath(
					ContactsContract.Contacts.CONTENT_URI,
					String.valueOf(contactId));
			lastContacted = cur.getString(cur
					.getColumnIndex(PhoneLookup.LAST_TIME_CONTACTED));
			holder.quickContactView.assignContactFromPhone(cursor
					.getString(cursor
							.getColumnIndex(BlockItemTable.COLUMN_NUMBER)),
					true);
			loadThumbnail(holder.quickContactView, contactUri);
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(context.getContentResolver(),
							contactUri);
			holder.quickContactView.setImageBitmap(BitmapFactory
					.decodeStream(input));

			holder.name.setText(name);
			holder.phoneNum.setText(cursor.getString(1));
			holder.lastContact.setText(lastContacted);
			cur.close();
		} else {
			holder.quickContactView.assignContactFromPhone(cursor
					.getString(cursor
							.getColumnIndex(BlockItemTable.COLUMN_NUMBER)),
					true);
			loadThumbnail(holder.quickContactView, null);
			holder.name.setText(cursor.getString(cursor
					.getColumnIndex(BlockItemTable.COLUMN_NUMBER)));
			holder.phoneNum.setText("-");
			holder.lastContact.setVisibility(View.INVISIBLE);
			cur.close();
		}

		holder.primaryActionView.setVisibility(View.VISIBLE);
		view.setBackgroundColor(mSelectedItemsIds.get(cursor.getPosition()) ? 0x9934B5E4
				: Color.TRANSPARENT);

	}

	public void toggleSelection(int position) {
		selectView(position, !mSelectedItemsIds.get(position));
	}

	public int getSelectedCount() {
		return mSelectedItemsIds.size();
	}

	public void selectView(int position, boolean value) {
		if (value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);

		notifyDataSetChanged();
	}

	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}

	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}

	private class ViewHolder {

		// The quick contact badge for the contact.
		QuickContactBadge quickContactView;
		// The primary action view of the entry.
		View primaryActionView;
		TextView phoneNum;
		TextView name;
		TextView lastContact;

	}

	public static int getDefaultAvatarResId(Context context, int extent) {

		if (s180DipInPixel == -1) {
			Resources r = context.getResources();
			s180DipInPixel = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 180, r.getDisplayMetrics());
		}

		final boolean hires = (extent != -1) && (extent > s180DipInPixel);
		return getDefaultAvatarResId(hires);
	}

	public static int getDefaultAvatarResId(boolean hires) {
		if (hires)
			return R.drawable.ic_contact_picture_180_holo_light;
		return R.drawable.ic_contact_picture_holo_light;
	}

	public void loadThumbnail(ImageView view, Uri contactUri) {
		if (contactUri == null) {
			// No photo is needed
			view.setImageResource(getDefaultAvatarResId(view.getContext(), -1));
		} else {

			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(context.getContentResolver(),
							contactUri);
			view.setImageBitmap(BitmapFactory.decodeStream(input));
		}
	}

}