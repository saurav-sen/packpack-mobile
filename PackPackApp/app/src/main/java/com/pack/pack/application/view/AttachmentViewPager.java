package com.pack.pack.application.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Saurav on 12-03-2017.
 */
public class AttachmentViewPager extends ViewPager {

    private boolean paginationSupported = true;

    public AttachmentViewPager(Context context) {
        super(context);
    }

    public AttachmentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPaginationSupported(boolean paginationSupported) {
        this.paginationSupported = paginationSupported;
    }

    private boolean isPaginationSupported() {
        return paginationSupported;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return isPaginationSupported() && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isPaginationSupported() && super.onInterceptTouchEvent(ev);
    }
}
