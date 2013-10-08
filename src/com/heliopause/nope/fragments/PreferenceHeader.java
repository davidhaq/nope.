package com.heliopause.nope.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.heliopause.nope.R;

public class PreferenceHeader extends Preference {

	public Context context;

	public PreferenceHeader(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public PreferenceHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;

	}

	public PreferenceHeader(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		TextView nopeAboutTitle = (TextView) view
				.findViewById(R.id.about_title);
		TextView nopeAboutSummary = (TextView) view
				.findViewById(R.id.about_summary);
		Typeface tfThin = Typeface.createFromAsset(context.getAssets(),
				"fonts/robotoThin.ttf");
		Typeface tfLight = Typeface.createFromAsset(context.getAssets(),
				"fonts/robotoLight.ttf");
		nopeAboutTitle.setTypeface(tfThin);
		nopeAboutSummary.setTypeface(tfLight);
	}

}
