package com.heliopause.nope.fragments;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.heliopause.nope.Constants;
import com.heliopause.nope.R;

public class SpamBlockFragment extends SherlockFragment {

    private TextView message;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String[] menuListArray = getResources().getStringArray(
                R.array.menu_list);
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(menuListArray[0]);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.show();

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spamblock, container,
                false);
        message = (TextView) view.findViewById(R.id.spamblock_text);
        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/robotoLight.ttf");
        message.setTypeface(tf);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getSherlockActivity());
        Boolean toggleState = prefs.getBoolean(Constants.SPAM_BLOCK_SERVICE_STATUS,
                false);
        if (toggleState) {
            message.setText(getSherlockActivity().getResources().getString(
                    R.string.spam_block_on));
        } else {
            message.setText(getSherlockActivity().getResources().getString(
                    R.string.spam_block_off));
        }

    }

}
