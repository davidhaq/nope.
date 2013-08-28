package com.jphsoftware.nope;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
<<<<<<< HEAD
=======
import android.content.res.Resources;
>>>>>>> origin/dev-unstable
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.jphsoftware.nope.fragments.AboutFragment;
import com.jphsoftware.nope.fragments.AntiSMSSpamFragment;
import com.jphsoftware.nope.fragments.CallBlockFragment;
import com.jphsoftware.nope.fragments.SMSBlockFragment;
import com.jphsoftware.nope.fragments.SettingsFragment;

public class MainActivity extends SherlockFragmentActivity {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] menuListArray;
	private CharSequence mTitle;
	private Integer mMenuPosition;
	private ActionBar actionBar;

	private static final String OPENED_KEY = "OPENED_KEY";
	private SharedPreferences prefs = null;
	private Boolean opened = null;

	Fragment cbFrag = new CallBlockFragment();
	Fragment smFrag = new SMSBlockFragment();
	Fragment asFrag = new AntiSMSSpamFragment();
	Fragment sFrag = new SettingsFragment();
	Fragment abFrag = new AboutFragment();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Request window feature before content is displayed
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		// set the main container view
		setContentView(R.layout.main);

		// grab and set some things in the actionBarSherlock
		actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Make sure we get the array of menu item strings.
		menuListArray = getResources().getStringArray(R.array.menu_list);

		// Define the drawer list view
		mDrawerList = (ListView) findViewById(R.id.menu_frame);
		mDrawerList.setAdapter(new HomeMenuAdapter(this,
				R.layout.side_menu_list_item, menuListArray));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerList.getLayoutParams().width = calculateSideDrawerWidth();
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
				if (opened != null && opened == false) {
					opened = true;
					if (prefs != null) {
						Editor editor = prefs.edit();
						editor.putBoolean(OPENED_KEY, true);
						editor.apply();
					}
				}
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerSlide(View drawerView, float offset) {
				super.onDrawerSlide(drawerView, offset);
				// getActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		if (savedInstanceState == null) {
			mMenuPosition = 0;
			selectItem(0, true);

<<<<<<< HEAD
=======
		} else {

			mMenuPosition = savedInstanceState.getInt("position");
			selectItem(mMenuPosition, false);
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				prefs = getPreferences(MODE_PRIVATE);
				opened = prefs.getBoolean(OPENED_KEY, false);
				if (opened == false) {
					mDrawerLayout.openDrawer(mDrawerList);
				}
			}
		}).start();

>>>>>>> origin/dev-unstable
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		outState.putInt("position", mMenuPosition);
		super.onSaveInstanceState(outState);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
				mDrawerLayout.closeDrawer(mDrawerList);
			} else {
				mDrawerLayout.openDrawer(mDrawerList);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Called whenever we call invalidateOptionsMenu()
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view

		// System.err.println("onPrepareOptionsMenu called");
		boolean drawerOpen = mDrawerLayout.isDrawerVisible(mDrawerList);
		if (menu.hasVisibleItems()) {
			if (mMenuPosition == 0 || mMenuPosition == 1) {
				if (menu.findItem(R.id.add_call_block) != null) {
					menu.findItem(R.id.add_call_block).setVisible(!drawerOpen);
				}
			}
		}
		return super.onPrepareOptionsMenu(menu);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			view.setSelected(true);
			selectItem(position, false);
		}
	}

	// Swaps fragments that are shown in the main view
	private void selectItem(int position, boolean firstload) {

		if (firstload = true) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction ft = fragmentManager.beginTransaction();

			// Create a new fragment and specify
			// the fragment to show based on position
			switch (position) {
			case 0:
				ft.replace(R.id.content_frame, cbFrag);
				mMenuPosition = 0;
				((HomeMenuAdapter) mDrawerList.getAdapter())
						.onItemSelected(mMenuPosition);
				System.err.println("Call Block");
				break;
			case 1:
				ft.replace(R.id.content_frame, smFrag);
				mMenuPosition = 1;
				((HomeMenuAdapter) mDrawerList.getAdapter())
						.onItemSelected(mMenuPosition);
				System.err.println("SMS Blocklist");
				break;
			case 2:
				ft.replace(R.id.content_frame, asFrag);
				mMenuPosition = 2;
				((HomeMenuAdapter) mDrawerList.getAdapter())
						.onItemSelected(mMenuPosition);
				System.err.println("Anti Text Spam");
				break;
			case 3:
				ft.replace(R.id.content_frame, sFrag);
				mMenuPosition = 3;
				((HomeMenuAdapter) mDrawerList.getAdapter())
						.onItemSelected(mMenuPosition);
				System.err.println("Settings");
				break;
			case 4:
				ft.replace(R.id.content_frame, abFrag);
				mMenuPosition = 4;
				((HomeMenuAdapter) mDrawerList.getAdapter())
						.onItemSelected(mMenuPosition);
				System.err.println("About");
				break;
			}

			// Highlight the selected item, update the title, and close the
			// drawer
			// mDrawerList.setItemChecked(position, true);
			setTitle(menuListArray[position]);
			mDrawerLayout.closeDrawer(mDrawerList);

			// Insert the fragment by replacing any existing fragment
			ft.commit();
		} else {
			if (mMenuPosition == position) {

				// Highlight the selected item, update the title, and close the
				// drawer
				setTitle(menuListArray[position]);
				mDrawerLayout.closeDrawer(mDrawerList);

			} else {

				FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction ft = fragmentManager.beginTransaction();

				// Create a new fragment and specify
				// the fragment to show based on position
				switch (position) {
				case 0:
					ft.replace(R.id.content_frame, cbFrag);
					mMenuPosition = 0;
					((HomeMenuAdapter) mDrawerList.getAdapter())
							.onItemSelected(mMenuPosition);
					System.err.println("Call Block");
					break;
				case 1:
					ft.replace(R.id.content_frame, smFrag);
					mMenuPosition = 1;
					((HomeMenuAdapter) mDrawerList.getAdapter())
							.onItemSelected(mMenuPosition);
					System.err.println("SMS Blocklist");
					break;
				case 2:
					ft.replace(R.id.content_frame, asFrag);
					mMenuPosition = 2;
					((HomeMenuAdapter) mDrawerList.getAdapter())
							.onItemSelected(mMenuPosition);
					System.err.println("Anti Text Spam");
					break;
				case 3:
					ft.replace(R.id.content_frame, sFrag);
					mMenuPosition = 3;
					((HomeMenuAdapter) mDrawerList.getAdapter())
							.onItemSelected(mMenuPosition);
					System.err.println("Settings");
					break;
				case 4:
					ft.replace(R.id.content_frame, abFrag);
					mMenuPosition = 4;
					((HomeMenuAdapter) mDrawerList.getAdapter())
							.onItemSelected(mMenuPosition);
					System.err.println("About");
					break;
				}

				// Highlight the selected item, update the title, and close the
				// drawer
				// mDrawerList.setItemChecked(position, true);
				setTitle(menuListArray[position]);
				mDrawerLayout.closeDrawer(mDrawerList);

<<<<<<< HEAD
		// Highlight the selected item, update the title, and close the
		// drawer

		// Debugging
		System.err.println("Child count: " + mDrawerLayout.getChildCount());
		System.err.println("View: " + mDrawerLayout.getChildAt(1).toString());
		ListView temp = (ListView) mDrawerLayout.getChildAt(1);
		System.err.println("item count of listview: "
				+ temp.getAdapter().getCount());
		// debugging

		setTitle(menuListArray[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
=======
				// Insert the fragment by replacing any existing fragment
				ft.commit();
			}
		}
>>>>>>> origin/dev-unstable

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggles
		mDrawerToggle.onConfigurationChanged(newConfig);

	}

	@SuppressWarnings("deprecation")
	protected int calculateSideDrawerWidth() {

		Resources localResources = getResources();
		Display display = getWindowManager().getDefaultDisplay();

		int i = display.getWidth();
		System.err.println("width:" + i);
		int j = localResources
				.getDimensionPixelSize(R.dimen.side_panel_max_width);
		int k = localResources.getConfiguration().orientation;
		int m;

		if (k == 1) {
			m = Math.min(i * 4 / 5, j);
		} else {
			m = Math.min(i / 2, j);
		}
		return m;

	}

	private class HomeMenuAdapter extends ArrayAdapter<String> {
		private final int mNormalColor;
		private final int mSelectedColor;
		private int mSelectedIndex;

		public HomeMenuAdapter(Context paramContext, int paramInt,
				String[] paramArrayOfString) {
			super(paramContext, paramInt, paramArrayOfString);
			Resources localResources = paramContext.getResources();
			this.mSelectedColor = localResources
					.getColor(R.color.side_drawer_item_current_screen_color);
			this.mNormalColor = localResources.getColor(R.color.transparent);
		}

		public View getView(int paramInt, View paramView,
				ViewGroup paramViewGroup) {
			TextView localTextView = (TextView) super.getView(paramInt,
					paramView, paramViewGroup);
			this.mSelectedIndex = mMenuPosition;
			if (paramInt == this.mSelectedIndex) {
				localTextView.setBackgroundColor(this.mSelectedColor);
				return localTextView;
			} else {
				localTextView.setBackgroundColor(this.mNormalColor);
				return localTextView;
			}
		}

		public boolean onItemSelected(int paramInt) {
			if (this.mSelectedIndex != paramInt) {
				this.mSelectedIndex = paramInt;
				notifyDataSetChanged();
			}
			return true;
		}
	}

}