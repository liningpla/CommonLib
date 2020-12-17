package com.example.notificationtest.homemulity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;

import com.common.utils.Utils;

/**首页适配View*/
public class HomeViewPager extends VerticalViewPager {
    private static final String TAG = MyMultiWindowActivity.TAG;
    private Context context;
    private OverScroller mScroller;
    private int mScreenHeight;
    private int mScreenWidth;
    private int totalHeight;
    private int offset;
    private int mTouchSlop;
    private int lastScrollY;
    private int baseTop;
    private boolean isScroll;

    public HomeViewPager(Context context) {
        super(context);
        init(context);
    }

    public HomeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void setScroll(boolean isScroll) {
        this.isScroll = isScroll;
    }
    private void init(Context context) {
        this.context = context;
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledPagingTouchSlop();
        setOverScrollMode(ScrollView.OVER_SCROLL_NEVER);
        mScreenHeight = Utils.getScreenHeight(context);
        mScreenWidth = Utils.getScreenWidth(context);
        offset = Utils.dip2px(context, 180);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
//            View childView = getChildAt(i);
//            baseTop = i * mScreenHeight - i * (mScreenHeight - offset);
//            childView.layout(l, baseTop, r, baseTop + mScreenHeight);
            Log.d(TAG, "MyParent onLayout testTop:" + baseTop);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (!isScroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (!isScroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }
}
