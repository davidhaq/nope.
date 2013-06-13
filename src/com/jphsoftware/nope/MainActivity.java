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
	private CharSequence mDrawerTitle;
	private String[] menuListArray;
	private CharSequence mTitle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Request window feature before content is displayed
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		// set the Above View
		setContentView(R.layout.main);

		menuListArray = getResources().getStringArray(R.array.menu_list);
		mTitle = mDrawerTitle = getTitle();

		mDrawerList = (ListView) findViewById(R.id.menu_frame);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.slide_menu_row, menuListArray));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		// If there isn't a fragment, make one, and commit it.
		if (mContent == null) {
			mContent = new CallBlockFragment();
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		// customize the ActionBar
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	/** Swaps fragments in the main content view */
	private void selectItem(int position) {
		// Create a new fragment and specify the planet to show based on
		// position
		Fragment fragment = null;
		switch (position) {
		case 0:
			fragment = new CallBlockFragment();
			System.err.println("Call Block");
			break;
		case 1:
			fragment = new SMSBlockFragment();
			System.err.println("SMS Blocklist");
			break;
		case 2:
			fragment = new AntiSMSSpamFragment();
			System.err.println("Anti Text Spam");
			break;
		case 3:
			fragment = new SettingsFragment();
			System.err.println("Settings");
			break;
		case 4:
			fragment = new AboutFragment();
			System.err.println("About");
			break;
		}
		if (fragment != null) {
			// Insert the fragment by replacing any existing fragment
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction()
					.replace(R.id.content_frame, fragment).commit();

			// Highlight the selected item, update the title, and close the
			// drawer
			mDrawerList.setItemChecked(position, true);
			setTitle(menuListArray[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		}
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
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

}