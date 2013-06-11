package com.jphsoftware.nope.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.jphsoftware.nope.MainActivity;
import com.jphsoftware.nope.R;

public class MenuListFragment extends SherlockListFragment {

	private String[] menuListArray;
	private int[] menuIconListArray;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SlideMenuAdapter adapter;
		menuListArray = getResources().getStringArray(R.array.menu_list);
		menuIconListArray = getResources().getIntArray(R.array.menu_icon_list);
		adapter = new SlideMenuAdapter(getActivity());

		for (int i = 0; i < menuListArray.length; i++) {
			adapter.add(new SlideMenuItem(menuListArray[i],
					menuIconListArray[i]));
		}
		setListAdapter(adapter);
	}

	private class SlideMenuItem {
		public String tag;
		public int iconRes;

		public SlideMenuItem(String tag, int iconRes) {
			this.tag = tag;
			this.iconRes = iconRes;
		}
	}

	public class SlideMenuAdapter extends ArrayAdapter<SlideMenuItem> {

		public SlideMenuAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.slide_menu_row, null);
			}
			ImageView icon = (ImageView) convertView
					.findViewById(R.id.row_icon);
			icon.setImageResource(getItem(position).iconRes);
			TextView title = (TextView) convertView
					.findViewById(R.id.row_title);
			title.setText(getItem(position).tag);

			return convertView;
		}

	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		switch (position) {
		case 0:
			newContent = new CallBlockFragment();
			System.err.println("Call Block");
			break;
		case 1:
			newContent = new SMSBlockFragment();
			System.err.println("SMS Blocklist");
			break;
		case 2:
			newContent = new AntiSMSSpamFragment();
			System.err.println("Anti Text Spam");
			break;
		case 3:
			newContent = new SettingsFragment();
			System.err.println("Settings");
			break;
		case 4:
			newContent = new AboutFragment();
			System.err.println("About");
			break;
		}
		if (newContent != null) {
			System.err.println("Switching fragment");
			switchFragment(newContent);
		}
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment newContent) {
		if (getActivity() == null) {
			return;
		}

		if (getActivity() instanceof MainActivity) {
			System.err.println("Grabbing activity");
			MainActivity activity = (MainActivity) getActivity();
			System.err.println("switching content");
			activity.switchContent(newContent);
		}
	}
}