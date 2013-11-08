package com.heliopause.nope;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.heliopause.nope.fragments.AboutFragment;
import com.heliopause.nope.fragments.CallBlockFragment;
import com.heliopause.nope.fragments.MsgBlockFragment;
import com.heliopause.nope.fragments.SettingsFragment;
import com.heliopause.nope.fragments.SpamBlockFragment;
import com.heliopause.nope.services.CallBlockService;
import com.heliopause.nope.services.MsgBlockService;
import com.heliopause.nope.services.SpamBlockService;

public class ContainerActivity extends SherlockFragmentActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] menuListArray;
    private CharSequence mTitle;
    private Integer mMenuPosition;

    private static final String OPENED_KEY = "OPENED_KEY";
    private SharedPreferences prefs = null;
    private SharedPreferences settings = null;
    private Boolean opened = null;

    private Fragment fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request window feature before content is displayed
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        // set the main container view
        setContentView(R.layout.main);

        // grab and set some things in the actionBarSherlock
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setSupportProgressBarIndeterminateVisibility(false);

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
                getSupportActionBar().setTitle(menuListArray[mMenuPosition]);
                supportInvalidateOptionsMenu();
                if (opened != null && opened != null && !opened) {
                    opened = true;
                    if (prefs != null) {
                        Editor editor = prefs.edit();
                        editor.putBoolean(Constants.SPAM_BLOCK_SERVICE_STATUS,
                                true);
                        editor.putBoolean(OPENED_KEY, true);
                        editor.commit();
                    }
                }
            }

            public void onDrawerOpened(View drawerView) {
                mTitle = "nope.";
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

        if (savedInstanceState != null) {
            mMenuPosition = savedInstanceState.getInt("position");

        } else {
            mMenuPosition = 0;
            selectItem(0);
        }

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        new Thread(new Runnable() {
            @Override
            public void run() {

                prefs = getPreferences(MODE_PRIVATE);
                opened = prefs.getBoolean(OPENED_KEY, false);
                if (opened != null && !opened) {

                    mDrawerLayout.openDrawer(mDrawerList);
                }
            }
        }).start();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            PreferenceManager.setDefaultValues(this, R.xml.preferences_compat,
                    false);
        } else {
            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        }
        startServicesOnFirstOpen();
    }

    private void startServicesOnFirstOpen() {

        try {
            settings.edit()
                    .putString(
                            "nope_version_key",
                            getPackageManager().getPackageInfo(
                                    getPackageName(), 0).versionName).commit();
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Intent callBlock = new Intent(this, CallBlockService.class);
        Intent textBlock = new Intent(this, MsgBlockService.class);
        Intent spamBlock = new Intent(this, SpamBlockService.class);

        if (settings.getBoolean(Constants.MSG_BLOCK_SERVICE_STATUS, true)) {
            startService(textBlock);
        }
        if (settings.getBoolean(Constants.CALL_BLOCK_SERVICE_STATUS, true)) {

            startService(callBlock);
        }
        if (settings.getBoolean(Constants.SPAM_BLOCK_SERVICE_STATUS, true)) {

            startService(spamBlock);
        }
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

        boolean drawerOpen = mDrawerLayout.isDrawerVisible(mDrawerList);
        if (menu.hasVisibleItems()) {
            if (mMenuPosition == 0 || mMenuPosition == 1) {
                if (menu.findItem(R.id.add_block_item) != null) {
                    menu.findItem(R.id.add_block_item).setVisible(!drawerOpen);
                }
            } else if (mMenuPosition == 2) {
                if (menu.findItem(R.id.spam_block_toggle) != null) {
                    menu.findItem(R.id.spam_block_toggle).setVisible(
                            !drawerOpen);
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
            selectItem(position);
        }
    }

    // Swaps fragments that are shown in the main view
    private void selectItem(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        String tag = null;
        boolean firstload;
        if (firstload = true) {
            // Create a new fragment and specify
            // the fragment to show based on position
            switch (position) {
                case 0:
                    fragment = new CallBlockFragment();
                    tag = "cbFrag";
                    mMenuPosition = 0;
                    ((HomeMenuAdapter) mDrawerList.getAdapter())
                            .onItemSelected(mMenuPosition);
                    break;
                case 1:
                    fragment = new MsgBlockFragment();
                    tag = "mbFrag";
                    mMenuPosition = 1;
                    ((HomeMenuAdapter) mDrawerList.getAdapter())
                            .onItemSelected(mMenuPosition);
                    break;
                case 2:
                    fragment = new SpamBlockFragment();
                    tag = "sbFrag";
                    mMenuPosition = 2;
                    ((HomeMenuAdapter) mDrawerList.getAdapter())
                            .onItemSelected(mMenuPosition);
                    break;
                case 3:
                    fragment = new SettingsFragment();
                    tag = "stFrag";
                    mMenuPosition = 3;
                    ((HomeMenuAdapter) mDrawerList.getAdapter())
                            .onItemSelected(mMenuPosition);
                    break;
                case 4:
                    fragment = new AboutFragment();
                    tag = "abFrag";
                    mMenuPosition = 4;
                    ((HomeMenuAdapter) mDrawerList.getAdapter())
                            .onItemSelected(mMenuPosition);
                    break;
            }

            // Highlight the selected item, update the title, and close the
            // drawer
            // mDrawerList.setItemChecked(position, true);
            setTitle(menuListArray[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

            // Insert the fragment by replacing any existing fragment
            ft.replace(R.id.content_frame, fragment, tag);
            ft.commit();
        } else {

            if (mMenuPosition == position) {

                // Highlight the selected item, update the title, and close the
                // drawer
                mDrawerList.setItemChecked(position, true);
                setTitle(menuListArray[position]);
                mDrawerLayout.closeDrawer(mDrawerList);

            } else {
                // Set the fragment by ID and specify
                // the fragment to show based on position
                switch (position) {
                    case 0:
                        fragment = getSupportFragmentManager()
                                .findFragmentByTag("cbFrag");
                        mMenuPosition = 0;
                        ((HomeMenuAdapter) mDrawerList.getAdapter())
                                .onItemSelected(mMenuPosition);
                        break;
                    case 1:
                        fragment = getSupportFragmentManager()
                                .findFragmentByTag("mbFrag");
                        mMenuPosition = 1;
                        ((HomeMenuAdapter) mDrawerList.getAdapter())
                                .onItemSelected(mMenuPosition);
                        break;
                    case 2:
                        fragment = getSupportFragmentManager()
                                .findFragmentByTag("sbFrag");
                        mMenuPosition = 2;
                        ((HomeMenuAdapter) mDrawerList.getAdapter())
                                .onItemSelected(mMenuPosition);
                        break;
                    case 3:
                        fragment = getSupportFragmentManager()
                                .findFragmentByTag("stFrag");
                        mMenuPosition = 3;
                        ((HomeMenuAdapter) mDrawerList.getAdapter())
                                .onItemSelected(mMenuPosition);
                        break;
                    case 4:
                        fragment = getSupportFragmentManager()
                                .findFragmentByTag("abFrag");
                        mMenuPosition = 4;
                        ((HomeMenuAdapter) mDrawerList.getAdapter())
                                .onItemSelected(mMenuPosition);
                        break;
                }

                // Highlight the selected item, update the title, and close the
                // drawer
                // mDrawerList.setItemChecked(position, true);
                setTitle(menuListArray[position]);
                mDrawerLayout.closeDrawer(mDrawerList);

                // attach to the fragment and commit
                ft.replace(R.id.content_frame, fragment);
                ft.commit();
            }
        }

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

    @SuppressWarnings("SameReturnValue")
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

            Typeface tf = Typeface.createFromAsset(getAssets(),
                    "fonts/robotoLight.ttf");
            if (tf != null) {
                localTextView.setTypeface(tf);
            }

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