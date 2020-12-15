package com.example.notificationtest.homemulity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.Scroller;

public class MyMultiView extends FrameLayout {
    private static final String TAG = MyMultiWindowActivity.TAG;
    private Context mContext;
    private int mScreenHeight;
    private int totalHeight;
    private OverScroller mScroller;
    private VelocityTracker mVelocityTracker;
    private int mTouchSlop;
    private int mMinimumVelocity;
    private float mLastY;
    private int downX = 0;
    private boolean isPull;

    public MyMultiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyMultiView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mScreenHeight = getScreenSize(mContext).heightPixels;
        mScroller = new OverScroller(mContext);
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledPagingTouchSlop();
        mMinimumVelocity = config.getScaledMinimumFlingVelocity();
    }

    /***
     * 获取真实的宽高 比如200px
     *
     * @param widthMeasureSpec
     * @return
     */
    public int measureRealWidth(int widthMeasureSpec) {
        int result = 200;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int realWidth = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
                //MeasureSpec.EXACTLY：精确值模式： 控件的layout_width或layout_heiht指定为具体值，比如200dp，或者指定为match_parent（占据父view的大小），系统返回的是这个模式
                result = realWidth;
                Log.d(TAG, "EXACTLY result " + result);
                break;
            case MeasureSpec.AT_MOST:
                // MeasureSpec.AT_MOST: 最大值模式，控件的layout_width或layout_heiht指定为wrap_content时，控件大小一般随着控件的子控件或内容的变化而变化，此时控件的尺寸不能超过父控件
                result = Math.min(result, realWidth);
                Log.d(TAG, "AT_MOST result " + result);
                break;
            case MeasureSpec.UNSPECIFIED:
                // MeasureSpec.UNSPECIFIED:不指定其大小测量模式，通常在绘制定义view的时候才会使用，即多大由开发者在onDraw()的时候指定大小
                result = realWidth;
                Log.d(TAG, "UNSPECIFIED result " + result);
                break;
        }
        return result;
    }

    /***
     * @param widthMeasureSpec  系统测量的宽 一共是32位的 高2位代表模式 低30位表示大小
     * @param heightMeasureSpec 系统测量的高 一共是32位的 高2位代表模式 低30位表示大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "widthMeasureSpec " + widthMeasureSpec);
        Log.d(TAG, "heightMeasureSpec " + heightMeasureSpec);
        /***自身宽*/
        int measureSelfWidth = measureRealWidth(widthMeasureSpec);
        int measureSelfHeight = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "widthMeasure " + measureSelfWidth);
        Log.d(TAG, "widthMode " + MeasureSpec.getMode(widthMeasureSpec));
        Log.d(TAG, "heightMeasure " + MeasureSpec.getSize(heightMeasureSpec));
        Log.d(TAG, "heightMode " + MeasureSpec.getMode(heightMeasureSpec));

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
        //设置viewGroup的宽高，也可以在onlayout中通过layoutParams设置
        totalHeight = getScreenSize(mContext).heightPixels * childCount;
        setMeasuredDimension(measureSelfWidth, totalHeight);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout left " + l);
        Log.d(TAG, "onLayout top " + t);
        Log.d(TAG, "onLayout right " + r);
        Log.d(TAG, "onLayout bottom " + b);
        Log.d(TAG, "onLayout heightPixels " + getScreenSize(mContext).heightPixels);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            childView.layout(l, i * mScreenHeight, r, (i + 1) * mScreenHeight);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        mVelocityTracker.addMovement(event);
        int y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(!mScroller.isFinished()){
                    mScroller.abortAnimation();
                }
                mLastY = y;
                downX = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dy = (int) (mLastY - y);
                scrollBy(0,dy);
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                int offsetY = (int) (downX - event.getY());
                if(offsetY>0){
                    isPull = false;
                    Log.d(TAG, "onTouchEvent  offsetY:"+offsetY+"   向上");
                }
                if(offsetY<0){
                    isPull = true;
                    Log.d(TAG, "onTouchEvent  offsetY:"+offsetY+"   向下");
                }
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000);
                int initVelocity = (int) velocityTracker.getXVelocity();
                if ((Math.abs(initVelocity) > mMinimumVelocity)) {
                    completeMove(isPull?-initVelocity:initVelocity);
                } else if (mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0,
                        getScrollRange())) {
                    postInvalidateOnAnimation();
                }
                recycleVelocityTracker();
                break;
        }
        return true;
    }
    private void completeMove(float velocityY) {
        int mScrollY = getScrollY();
        Log.d(TAG, "computeScroll  mScrollY:"+mScrollY+"  velocityY:"+velocityY+"  totalHeight:"+totalHeight);
        mScroller.fling(0, mScrollY, 0, (int) (velocityY * 2f), 0, 0, 0, totalHeight);
        postInvalidate();
        postInvalidateOnAnimation();
    }
    /**
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
//        Log.d(TAG, "mScroller.getCurrY() " + mScroller.getCurrY());
        if (mScroller.computeScrollOffset()) {//是否已经滚动完成
            scrollTo(0, mScroller.getCurrY());//获取当前值，startScroll（）初始化后，调用就能获取区间值
            postInvalidate();
        }
    }
    private int getScrollRange() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            View child = getChildAt(0);
            scrollRange = Math.max(0,
                    child.getHeight() - (getHeight() - getPaddingBottom() - getPaddingTop()));
        }
        return scrollRange;
    }
    /**
     * 获取屏幕大小，这个可以用一个常量不用每次都获取
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getScreenSize(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics;
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
