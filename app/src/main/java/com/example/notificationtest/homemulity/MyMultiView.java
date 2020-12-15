package com.example.notificationtest.homemulity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class MyMultiView extends FrameLayout {

    private int mLastY;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMaxVelocity;
    /**
     * 当前显示的是第几个屏幕
     */
    private int mCurrentPage = 0;

    public MyMultiView(Context context) {
        super(context);
        init(context);
    }

    public MyMultiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyMultiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledPagingTouchSlop();
        mMaxVelocity = config.getScaledMinimumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for(int i = 0; i < count; i++){
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        Log.i(MyMultiWindowActivity.TAG, "--l-->"+l+",--t-->"+t+",-->r-->"+r+",--b-->"+b);
        for(int i = 0; i < count; i++){
            View child = getChildAt(i);
            child.layout(l, i*getHeight(), r, (i + 1) * getHeight());
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(ev);
        int y = (int) ev.getY();
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                mLastY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int dy = mLastY - y;
                scrollBy(0,dy);
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int initVelocity = (int) velocityTracker.getXVelocity();
                if(initVelocity > mMaxVelocity && mCurrentPage > 0){//如果是快速的向右滑，则需要显示上一个屏幕
                    Log.i(MyMultiWindowActivity.TAG, "----------------快速的向右滑--------------------");
                    scrollToPage(mCurrentPage - 1);
                }else if(initVelocity < -mMaxVelocity && mCurrentPage < (getChildCount() - 1)){//如果是快速向左滑动，则需要显示下一个屏幕
                    Log.i(MyMultiWindowActivity.TAG, "----------------快速的向左滑--------------------");
                    scrollToPage(mCurrentPage + 1);
                }else{//不是快速滑动的情况，此时需要计算是滑动到
                    Log.i(MyMultiWindowActivity.TAG, "----------------慢慢的滑动--------------------");
                    slowScrollToPage();
                }
                recycleVelocityTracker();
                break;
        }
        return true;
    }

    /**
     * 缓慢滑动抬起手指的情形，需要判断是停留在本Page还是往前、往后滑动
     */
    private void slowScrollToPage() {
        //当前的偏移位置
        int scrollX = getScrollX();
        int scrollY = getScrollY();
        //判断是停留在本Page还是往前一个page滑动或者是往后一个page滑动
        int whichPage = (getScrollY() + getHeight() / 2 ) / getHeight() ;
        scrollToPage(whichPage);
    }

    /**
     * 滑动到指定屏幕
     * @param indexPage
     */
    private void scrollToPage(int indexPage) {
        mCurrentPage = indexPage;
        if(mCurrentPage > getChildCount() - 1){
            mCurrentPage = getChildCount() - 1;
        }
        //计算滑动到指定Page还需要滑动的距离
        int dy = mCurrentPage * getHeight() - getScrollY();
        mScroller.startScroll(0,getScrollY(),0,dy,Math.abs(dy) * 2);//动画时间设置为Math.abs(dx) * 2 ms
        //记住，使用Scroller类需要手动invalidate
        invalidate();
    }

    @Override
    public void computeScroll() {
        Log.i(MyMultiWindowActivity.TAG, "---------computeScrollcomputeScrollcomputeScroll--------------");
        super.computeScroll();
//        if(mScroller.computeScrollOffset()){
//            scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
//            invalidate();
//        }
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if(mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

}
