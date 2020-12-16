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

import com.common.utils.Utils;

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
    private int offset;
    private int mOverflingDistance;

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
        mScreenHeight = Utils.getScreenHeight(mContext);
        mScroller = new OverScroller(mContext);
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledPagingTouchSlop();
        mMinimumVelocity = config.getScaledMinimumFlingVelocity();
        mOverflingDistance = config.getScaledOverflingDistance();
        offset = Utils.dip2px(mContext, 108);
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
        /***自身宽*/
        int measureSelfWidth = measureRealWidth(widthMeasureSpec);
        int measureSelfHeight = MeasureSpec.getSize(heightMeasureSpec);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
        //设置viewGroup的宽高，也可以在onlayout中通过layoutParams设置

        totalHeight = mScreenHeight + childCount * offset;
        setMeasuredDimension(measureSelfWidth, totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout left:" + l+" top:" + t+" right:"+r+" bottom:"+b+" height:"+mScreenHeight);
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            childView.layout(l, i * mScreenHeight - i *(mScreenHeight-  offset),
                            r, (i + 1) * mScreenHeight - i *(mScreenHeight-  offset));
        }
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        Log.d(MyMultiWindowActivity.TAG, "onInterceptTouchEvent----action: " + action);
        if (action == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            // 该事件可能不是我们的
            return false;
        }
        boolean isIntercept = false;
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 如果动画还未结束，则将此事件交给onTouchEvet()处理，
                // 否则，先分发给子View
                isIntercept = !mScroller.isFinished();
                // 如果此时不拦截ACTION_DOWN时间，应该记录下触摸地址及手指id，当我们决定拦截ACTION_MOVE的event时，
                // 将会需要这些初始信息（因为我们的onTouchEvent将可能接收不到ACTION_DOWN事件）
                mLastY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float my = ev.getY();
                // 根据方向进行拦截，（其实这样，如果我们的方向是水平的，里面有一个ScrollView，那么我们是支持嵌套的）
                if (Math.abs(mLastY - my) >= mTouchSlop) {
                    isIntercept = true;
                }
                if (isIntercept) {
                    mLastY = my;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // 这是触摸的最后一个事件，无论如何都不会拦截
                recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        return isIntercept;
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
                completeMove(isPull?-initVelocity:initVelocity);
                recycleVelocityTracker();
                break;
        }
        return true;
    }
    private void completeMove(float velocityY) {
        int mScrollY = getScrollY();
        Log.d(TAG, "completeMove  mScrollY:"+mScrollY+"  currY:"+mScroller.getCurrY()+"  velocityY:"+velocityY+"  totalHeight:"+totalHeight);
        mScroller.fling(0, mScrollY, 0, (int) (velocityY * 2f), 0, 0, 0, totalHeight);
        postInvalidate();
    }
    /**
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            int oldY = getScrollY();
            int y = mScroller.getCurrY();
//            Log.d(TAG, "computeScroll  oldY:"+oldY+"  y:"+y);
            if (oldY != y) {
                scrollTo(0, y);
            }
            if (!awakenScrollBars()) {
                postInvalidateOnAnimation();
            }
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
