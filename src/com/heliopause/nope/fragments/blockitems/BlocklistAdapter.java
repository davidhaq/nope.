package com.heliopause.nope.fragments.blockitems;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.widget.CursorAdapter;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.heliopause.nope.R;
import com.heliopause.nope.database.BlockItemTable;

public class BlocklistAdapter extends CursorAdapter {

	// Debugging
	private static final boolean DEBUG = false;
	private static final String TAG = BlocklistAdapter.class.getSimpleName();

	private LayoutInflater inflator;
	private Context context;
	private static int s180DipInPixel = -1;

	public BlocklistAdapter(Context context, Cursor cursor) {
		super(context, cursor, 0);
		this.context = context;
		inflator = LayoutInflater.from(context);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = inflator.inflate(R.layout.block_item, parent, false);
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (DEBUG) {
			Log.d(TAG, "+++bindView called+++");
			Log.d(TAG, "number value: " + cursor.getString(1));
		}
		String name;
		long lastContacted;
		String lastContactedString = null;
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

		// Set the last contacted string based on whether or not we've ever been
		// contacted
		lastContacted = cursor.getLong(cursor
				.getColumnIndex(BlockItemTable.COLUMN_LAST_CONTACT));

		if (DEBUG)
			Log.d(TAG, "Stored time value: " + lastContacted);

		boolean contactedBefore = (lastContacted == -1337) ? false : true;
		if (contactedBefore) {
			lastContactedString = formatTimeStampString(context, lastContacted);
		} else {
			lastContactedString = context.getResources().getString(
					R.string.never_contacted);
		}

		if (cur.moveToFirst()) {

			name = cur.getString(cur
					.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
			contactId = cur.getString(cur.getColumnIndex(BaseColumns._ID));
			contactUri = Uri.withAppendedPath(
					ContactsContract.Contacts.CONTENT_URI,
					String.valueOf(contactId));

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
			holder.phoneNum.setText(PhoneNumberUtils.formatNumber(cursor
					.getString(cursor
							.getColumnIndex(BlockItemTable.COLUMN_NUMBER))));
			holder.lastContact.setText(lastContactedString);
			cur.close();
		} else {
			holder.quickContactView.assignContactFromPhone(cursor
					.getString(cursor
							.getColumnIndex(BlockItemTable.COLUMN_NUMBER)),
					true);
			loadThumbnail(holder.quickContactView, null);
			holder.name.setText(PhoneNumberUtils.formatNumber(cursor
					.getString(cursor
							.getColumnIndex(BlockItemTable.COLUMN_NUMBER))));
			holder.phoneNum.setText("-");
			holder.lastContact.setText(lastContactedString);
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

	public static String formatTimeStampString(Context context, long when) {
		return formatTimeStampString(context, when, false);
	}

	@SuppressWarnings("deprecation")
	public static String formatTimeStampString(Context context, long when,
			boolean fullFormat) {
		Time then = new Time();
		then.set(when);

		if (DEBUG)
			Log.d(TAG, "Contacted: " + then.toString());

		Time now = new Time();
		now.setToNow();

		// Basic settings for formatDateTime() we want for all cases.
		int format_flags = DateUtils.FORMAT_NO_NOON_MIDNIGHT
				| DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_CAP_AMPM;

		// If the message is from a different year, show the date and year.
		if (then.year != now.year) {
			format_flags |= DateUtils.FORMAT_SHOW_YEAR
					| DateUtils.FORMAT_SHOW_DATE;
		} else if (then.yearDay != now.yearDay) {
			// If it is from a different day than today, show only the date.
			format_flags |= DateUtils.FORMAT_SHOW_DATE;
		} else {
			// Otherwise, if the message is from today, show the time.
			format_flags |= DateUtils.FORMAT_SHOW_TIME;
		}

		// If the caller has asked for full details, make sure to show the date
		// and time no matter what we've determined above (but still make
		// showing
		// the year only happen if it is a different year from today).
		if (fullFormat) {
			format_flags |= (DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME);
		}

		return DateUtils.formatDateTime(context, when, format_flags);
	}
}