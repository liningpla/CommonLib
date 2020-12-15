package com.example.notificationtest.homemulity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.OverScroller;

public class MyMultiView extends FrameLayout {
    private VelocityTracker velocityTracker;
    private int mPointerId;
    private float y;
    private OverScroller mScroller;
    private int maxFlingVelocity, minFlingVelocity;
    private int mTouchSlop;
    protected Boolean isMove = false;
    protected float downY = 0;
    private int top_hight = 0;
    private int scrollYButtom = 0;
    private int nScrollYButtom = 0;
    private int desireWidth, desireHeight;
    private int pullDownMin = 0;
    private Boolean isEnablePullDown = true;
    private Boolean isFirst = true;
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
        mScroller = new OverScroller(context);
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledPagingTouchSlop();
        minFlingVelocity = config.getScaledMinimumFlingVelocity();
        maxFlingVelocity = config.getScaledMaximumFlingVelocity();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, heightMeasureSpec);
        }
        // count with padding
        desireWidth += getPaddingLeft() + getPaddingRight();
        desireHeight += getPaddingTop() + getPaddingBottom();
        // see if the size is big enough
        desireWidth = Math.max(desireWidth, getSuggestedMinimumWidth());
        desireHeight = Math.max(desireHeight, getSuggestedMinimumHeight());
        setMeasuredDimension(resolveSize(desireWidth, widthMeasureSpec),
                resolveSize(desireHeight, heightMeasureSpec));

        scrollYButtom = desireHeight - getMeasuredHeight() - top_hight;
        nScrollYButtom = desireHeight - getMeasuredHeight();
        //如果上啦拖出一半的高度，就代表将要执行上啦
        pullDownMin = nScrollYButtom - top_hight / 2;
        if (isFirst) {
            scrollTo(0, top_hight);
            isFirst = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        Log.i(MyMultiWindowActivity.TAG, "--l-->" + l + ",--t-->" + t + ",-->r-->" + r + ",--b-->" + b);
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.layout(l, i * getHeight(), r, (i + 1) * getHeight());
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
                mPointerId = ev.getPointerId(0);
                downY = y = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerIndex = ev.findPointerIndex(mPointerId);
                Log.d(MyMultiWindowActivity.TAG, "onInterceptTouchEventpointer----Index: " + pointerIndex
                        + ", pointerId: " + mPointerId);
                float my = ev.getY(pointerIndex);
                Log.d(MyMultiWindowActivity.TAG, "onInterceptTouchEventpointer----action_move [touchSlop: "
                        + mTouchSlop + ", deltaY: " + (y - my) + "]");

                // 根据方向进行拦截，（其实这样，如果我们的方向是水平的，里面有一个ScrollView，那么我们是支持嵌套的）
                if (Math.abs(y - my) >= mTouchSlop) {
                    isIntercept = true;
                }
                if (isIntercept) {
                    y = my;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // 这是触摸的最后一个事件，无论如何都不会拦截
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
        }
        return isIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 获取索引为0的手指id
                isMove = false;
                mPointerId = event.getPointerId(0);
                y = event.getY();
                if (!mScroller.isFinished())
                    mScroller.abortAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                isMove = true;
                // 获取当前手指id所对应的索引，虽然在ACTION_DOWN的时候，我们默认选取索引为0
                // 的手指，但当有第二个手指触摸，并且先前有效的手指up之后，我们会调整有效手指

                // 屏幕上可能有多个手指，我们需要保证使用的是同一个手指的移动轨迹，
                // 因此此处不能使用event.getActionIndex()来获得索引
                final int pointerIndex = event.findPointerIndex(mPointerId);
                float mx = event.getX(pointerIndex);
                float my = event.getY(pointerIndex);
                scrollBy(0, (int) (y - my));
                y = my;
                break;
            case MotionEvent.ACTION_UP:
                isMove = false;
                velocityTracker.computeCurrentVelocity(1000, maxFlingVelocity);
                float velocityX = velocityTracker.getXVelocity(mPointerId);
                float velocityY = velocityTracker.getYVelocity(mPointerId);

                completeMove(-velocityX, -velocityY);
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                // 获取离开屏幕的手指的索引
                isMove = false;
                int pointerIndexLeave = event.getActionIndex();
                int pointerIdLeave = event.getPointerId(pointerIndexLeave);
                if (mPointerId == pointerIdLeave) {
                    // 离开屏幕的正是目前的有效手指，此处需要重新调整，并且需要重置VelocityTracker
                    int reIndex = pointerIndexLeave == 0 ? 1 : 0;
                    mPointerId = event.getPointerId(reIndex);
                    // 调整触摸位置，防止出现跳动
                    y = event.getY(reIndex);
                    if (velocityTracker != null)
                        velocityTracker.clear();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                isMove = false;
                break;
        }
        return true;
    }

    private void completeMove(float velocityX, float velocityY) {
        int mScrollY = getScrollY();
        int maxY = scrollYButtom;
        int minY = top_hight;
        if (mScrollY >= maxY) {//如果滚动，超过了 下边界，就回弹到下边界
            Log.d(MyMultiWindowActivity.TAG, "completeMove-----------------------1");
            mScroller.startScroll(0, mScrollY, 0, maxY - mScrollY);
            invalidate();
            Log.d(MyMultiWindowActivity.TAG, "isPull: true");
        } else if (mScrollY <= minY) {//如果滚动，超过了上边界，就回弹到上边界
            Log.d(MyMultiWindowActivity.TAG, "completeMove-----------------------2");
            // 超出了上边界，弹回
            mScroller.startScroll(0, mScrollY, 0, minY - mScrollY);
            invalidate();
        } else if (Math.abs(velocityY) >= minFlingVelocity && maxY > 0) {//大于1页的时候
            Log.d(MyMultiWindowActivity.TAG, "completeMove-----------------------3");
            mScroller.fling(0, mScrollY, 0, (int) (velocityY * 2f), 0, 0, top_hight, scrollYButtom);
            invalidate();
        }
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
//        if (mScroller.computeScrollOffset()) {
//            scrollTo(0, mScroller.getCurrY());
//            postInvalidate();
//        }
    }

    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    private void initVelocityTrackerIfNotExists(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
            velocityTracker.addMovement(event);
        }
    }

}
