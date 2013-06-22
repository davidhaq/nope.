package com.jphsoftware.nope;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

	private Fragment mContent;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] menuListArray;
	private CharSequence mTitle;
	private Integer mMenuPosition;
	private ActionBar actionBar;

	private boolean firstLoad = true;

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
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.side_menu_list_item, menuListArray));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long arg3) {
				view.setSelected(true);
				super.onItemClick(parent, view, position, arg3);
			}
		});
		mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// mDrawerLayout.getLayoutParams().width = calculateSideDrawerWidth();

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerSlide(View drawerView, float offset) {
				super.onDrawerSlide(drawerView, offset);
				// getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {

			System.err.println("New activity, so default to callblockfragment");
			mMenuPosition = 0;
			selectItem(mMenuPosition, firstLoad);
			setTitle(menuListArray[mMenuPosition]);
		} else {
			mMenuPosition = savedInstanceState.getInt("position");
			if (mMenuPosition != null) {
				System.err.println("MenuPosition: ");
				selectItem(mMenuPosition, firstLoad);
			} else {
				selectItem(0, true);
			}
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		outState.putInt("position", mMenuPosition);
		System.err.println("Putting in position: " + mMenuPosition);
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
			selectItem(position);
		}
	}

	private void selectItem(int position, boolean first) {

		if (first) {
			selectItem(position);
			this.firstLoad = !first;
			System.err.println(firstLoad);
		} else {
			if (mMenuPosition == position) {

				// Highlight the selected item, update the title, and close the
				// drawer
				mDrawerList.setItemChecked(position, true);
				setTitle(menuListArray[position]);
				mDrawerLayout.closeDrawer(mDrawerList);

			} else {
				selectItem(position);
			}
		}
	}

	// Swaps fragments that are shown in the main view
	private void selectItem(int position) {

		// Create a new fragment and specify
		// the fragment to show based on position
		switch (position) {
		case 0:
			mContent = new CallBlockFragment();
			mMenuPosition = 0;
			System.err.println("Call Block");
			break;
		case 1:
			mContent = new SMSBlockFragment();
			mMenuPosition = 1;
			System.err.println("SMS Blocklist");
			break;
		case 2:
			mContent = new AntiSMSSpamFragment();
			mMenuPosition = 2;
			System.err.println("Anti Text Spam");
			break;
		case 3:
			mContent = new SettingsFragment();
			mMenuPosition = 3;
			System.err.println("Settings");
			break;
		case 4:
			mContent = new AboutFragment();
			mMenuPosition = 4;
			System.err.println("About");
			break;
		}

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// Highlight the selected item, update the title, and close the
		// drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(menuListArray[position]);
		mDrawerLayout.closeDrawer(mDrawerList);

	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
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

}