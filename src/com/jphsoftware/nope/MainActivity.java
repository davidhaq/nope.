package com.jphsoftware.nope;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.jphsoftware.nope.fragments.CallBlockFragment;
import com.jphsoftware.nope.fragments.MenuListFragment;
import com.slidingmenu.lib.SlidingMenu;

public class MainActivity extends SlidingSherlockFragmentBaseActivity {

	private SherlockFragment mContent;

	public MainActivity() {
		super(R.string.app_name);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the Above View
		setContentView(R.layout.main);

		// check if the content frame contains the menu frame
		if (findViewById(R.id.menu_frame) == null) {

			setBehindContentView(R.layout.menu_frame);
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu()
					.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			getSlidingMenu().setMode(SlidingMenu.LEFT);
			// show home as up so we can toggle
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			// add a dummy view
			View v = new View(this);
			setBehindContentView(v);
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}

		// set the Above View Fragment
		if (mContent == null) {
			mContent = new CallBlockFragment();
			// } else {
			// System.out.println("HEY1");
			// mContent = CallBlockFragment.newInstance(savedInstanceState
			// .getInt("mPos"));
			// // mContent = (ContentFragment) getSupportFragmentManager()
			// // .getFragment(savedInstanceState, "mPos");
			// }
		}
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, mContent).commit();

		// set the Behind View Fragment
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new MenuListFragment()).commit();

		// customize the SlidingMenu
		this.setSlidingActionBarEnabled(true);
		getSlidingMenu().setShadowWidthRes(R.dimen.shadow_width);
		getSlidingMenu().setShadowDrawable(R.drawable.shadow);
		getSlidingMenu().setBehindOffsetRes(R.dimen.actionbar_home_width);
		getSlidingMenu().setBehindScrollScale(0.25f);
		getSlidingMenu().setFadeDegree(0.35f);

		// customize the ActionBar
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Set actionbar back button icon here when we have the icon
		// actionBar.setIcon(resId);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	public void switchContent(SherlockFragment fragment) {
		System.err.println("We're at MainActivity.switchContent now");
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, fragment).commit();
		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				getSlidingMenu().showContent();
			}
		}, 50);
	}
}