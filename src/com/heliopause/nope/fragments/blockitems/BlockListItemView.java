package com.heliopause.nope.fragments.blockitems;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * An entry in the call log.
 */

public class BlockListItemView extends LinearLayout {
	public BlockListItemView(Context context) {
		super(context);
	}

	public BlockListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public BlockListItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void requestLayout() {
		// We will assume that once measured this will not need to resize
		// itself, so there is no need to pass the layout request to the parent
		// view (ListView).
		forceLayout();
	}
}