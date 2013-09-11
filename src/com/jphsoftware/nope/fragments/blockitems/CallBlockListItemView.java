package com.jphsoftware.nope.fragments.blockitems;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * An entry in the call log.
 */
public class CallBlockListItemView extends LinearLayout {
    public CallBlockListItemView(Context context) {
        super(context);
    }

    public CallBlockListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CallBlockListItemView(Context context, AttributeSet attrs, int defStyle) {
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