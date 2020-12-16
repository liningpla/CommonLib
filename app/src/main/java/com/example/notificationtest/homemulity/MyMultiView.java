package com.example.notificationtest.homemulity;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.OverScroller;
import android.widget.ScrollView;

import com.common.utils.Utils;

public class MyMultiView extends ScrollView {
    private static final String TAG = MyMultiWindowActivity.TAG;
    private Context context;
    private OverScroller mScroller;
    private MyParent myParent;
    private int mScreenHeight;
    private int mScreenWidth;
    private int totalHeight;
    private int offset;
    private int mTouchSlop;
    private int lastScrollY;


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
        this.context = context;
        ViewConfiguration config = ViewConfiguration.get(context);
        mTouchSlop = config.getScaledPagingTouchSlop();
        setFillViewport(true);
        mScreenHeight = Utils.getScreenHeight(context);
        mScreenWidth = Utils.getScreenWidth(context);
        offset = Utils.dip2px(context, 180);
        myParent = new MyParent(context);
        ScrollView.LayoutParams params = new ScrollView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(myParent, params);
    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        int rawY = (int) getY();
        int scollY = getScrollY();
//        Log.d(TAG, "onScrollChanged l:" + l+" oldl:" +oldl+"  t:"+ t+" oldt:"+oldt+"rawY:" + rawY+" scollY:" +scollY);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (myParent != null) {
            int deltaY = lastScrollY - scrollY;
            myParent.transformPage(deltaY);
            Log.d(TAG, "onOverScrolled  clampedX:"+ clampedX+" clampedY:"+clampedY);
        }
        lastScrollY = scrollY;
    }

    public void addContent(View view) {
        if (myParent != null && view != null) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(mScreenWidth, mScreenHeight);
            myParent.addView(view, params);
            fullScroll(ScrollView.FOCUS_DOWN);//滚动到底部
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d(TAG, "MyMultiView onLayout left:" + l + " top:" + t + " right:" + r + " bottom:" + b);
    }

    private class MyParent extends ViewGroup {
        private Context mContext;

        public MyParent(Context context) {
            super(context);
            init(context);
        }

        public MyParent(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public MyParent(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context);
        }

        private void init(Context context) {
            mContext = context;
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
            Log.d(TAG, "onMeasure totalHeight:" + totalHeight);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            Log.d(TAG, "MyParent onLayout left:" + l + " top:" + t + " right:" + r + " bottom:" + b + " height:" + mScreenHeight);
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                childView.layout(l, i * mScreenHeight - i * (mScreenHeight - offset),
                        r, (i + 1) * mScreenHeight - i * (mScreenHeight - offset));
            }
        }

        public void transformPage(int deltaY) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                View beforeView = null;//前一个view
                View nextView = null;//后一个view
                int currY = (int) childView.getY();
                int beforeY = 0, nextY = 0;
                if(i - 1>=0){
                    beforeView = getChildAt(i - 1);
                    beforeY = (int) beforeView.getY();
                }
                if(i + 1 < childCount){
                    nextView = getChildAt(i + 1);
                    nextY = (int) nextView.getY();
                }
                if (i == 0) {
                    childView.setTranslationY(currY-deltaY);
                }else{
//                    if(currY <= beforeY){
//                        childView.setTranslationY(beforeY);
//                    }
//                    childView.setTranslationY(beforeY - currY - deltaY);
                }
                Log.d(TAG, "MyParent transformPage currY:" + currY + " beforeY:" + beforeY + " nextY:" + nextY + " deltaY:" + deltaY +" i-->" + i);
                if (deltaY < 0) {//上滑
                } else {//下拉
                }
                MyParent.this.postInvalidate();
            }
        }
    }
}
