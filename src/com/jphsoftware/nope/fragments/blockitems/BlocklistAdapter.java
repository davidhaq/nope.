package com.jphsoftware.nope.fragments.blockitems;

import java.io.InputStream;
import java.util.List;

import android.app.Activity;
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
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.jphsoftware.nope.R;
import com.jphsoftware.nope.database.BlockItem;
import com.jphsoftware.nope.database.BlockItemDatabaseHelper;

public class BlocklistAdapter extends CursorAdapter {

	private LayoutInflater inflator;
	private static final String TAG = BlocklistAdapter.class.getSimpleName();

	private static int s180DipInPixel = -1;

	public BlocklistAdapter(Context context, Cursor cursor, int flags) {
		super(context, cursor, flags);
		inflator = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// TODO Auto-generated method stub

	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		// TODO Auto-generated method stub
		return null;
	}

	private class ViewHolder {

		/** The quick contact badge for the contact. */
		QuickContactBadge quickContactView;
		/** The primary action view of the entry. */
		View primaryActionView;
		TextView phoneNum;
		TextView name;
		TextView lastContact;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		String name = null;
		String lastContacted = null;
		String contactId = null;
		Uri contactUri = null;
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.call_block_item, null);
			holder = new ViewHolder();
			holder.primaryActionView = convertView
					.findViewById(R.id.primary_action_view);
			holder.quickContactView = (QuickContactBadge) convertView
					.findViewById(R.id.quick_contact_photo);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.phoneNum = (TextView) convertView
					.findViewById(R.id.phoneNum);
			holder.lastContact = (TextView) convertView
					.findViewById(R.id.lastCalled);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(callBlocks.get(position).getNumber()));
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
			holder.quickContactView.assignContactFromPhone(
					callBlocks.get(position).getNumber(), true);
			loadThumbnail(holder.quickContactView, contactUri);
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(context.getContentResolver(),
							contactUri);
			holder.quickContactView.setImageBitmap(BitmapFactory
					.decodeStream(input));

			holder.name.setText(name);
			holder.phoneNum.setText(callBlocks.get(position).getNumber());
			holder.lastContact.setText(lastContacted);
			cur.close();
		} else {
			holder.quickContactView.assignContactFromPhone(
					callBlocks.get(position).getNumber(), true);
			loadThumbnail(holder.quickContactView, null);
			holder.name.setText(callBlocks.get(position).getNumber());
			holder.phoneNum.setText("-");
			holder.lastContact.setVisibility(View.INVISIBLE);
			cur.close();
		}

		holder.primaryActionView.setVisibility(View.VISIBLE);
		convertView
				.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x9934B5E4
						: Color.TRANSPARENT);
		return convertView;
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