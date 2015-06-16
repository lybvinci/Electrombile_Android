package com.xunce.electrombile.viewpager;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by lybvinci on 2015/6/5.
 */
public class CustomViewPager extends LazyViewPager{

    private boolean isOnTouch = false;
    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isOnTouch)
            return super.onTouchEvent(ev);
        else
            return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isOnTouch)
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }
}
