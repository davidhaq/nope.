package com.jphsoftware.nope.fragments.blockitems;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.jphsoftware.nope.R;

public class BlocklistAdapter extends SimpleCursorAdapter {

	// Debugging
	private static final String TAG = BlocklistAdapter.class.getSimpleName();
	private static final boolean DEBUG = true;

	private LayoutInflater inflator;
	private Context context;
	private static int s180DipInPixel = -1;

	@SuppressWarnings("deprecation")
	public BlocklistAdapter(Context context, int layout, Cursor cursor,
			String[] from, int[] to) {
		super(context, layout, cursor, from, to);
		this.context = context;
		inflator = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
		return inflator.inflate(R.layout.call_block_item, null);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (DEBUG) {
			Log.d(TAG, "+++bindView called+++");
		}
		String name = null;
		String lastContacted = null;
		String contactId = null;
		Uri contactUri = null;

		ViewHolder holder;
		if (view == null) {
			view = inflator.inflate(R.layout.call_block_item, null);
			holder = new ViewHolder();
			holder.primaryActionView = view
					.findViewById(R.id.primary_action_view);
			holder.quickContactView = (QuickContactBadge) view
					.findViewById(R.id.quick_contact_photo);
			holder.name = (TextView) view.findViewById(R.id.name);
			holder.phoneNum = (TextView) view.findViewById(R.id.phoneNum);
			holder.lastContact = (TextView) view.findViewById(R.id.lastCalled);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		Uri lookupUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(cursor.getString(0)));
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
			holder.quickContactView.assignContactFromPhone(cursor.getString(1),
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
			holder.quickContactView.assignContactFromPhone(cursor.getString(1),
					true);
			loadThumbnail(holder.quickContactView, null);
			holder.name.setText(cursor.getString(1));
			holder.phoneNum.setText("-");
			holder.lastContact.setVisibility(View.INVISIBLE);
			cur.close();
		}

		holder.primaryActionView.setVisibility(View.VISIBLE);

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