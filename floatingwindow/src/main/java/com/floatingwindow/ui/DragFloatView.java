package com.floatingwindow.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.common.utils.Utils;
import com.floatingwindow.R;
import com.floatingwindow.biz.FloatBiz;
import com.floatingwindow.views.LeBaseView;

/***/
public class DragFloatView extends LeBaseView {
    private Activity mActivity;
    private ImageView iv_drag_float;
    private int width, height;//悬浮球的宽高
    private float lastX = 0f;//上次X位置
    private float lastY = 0f;//上次Y位置
    private float touchX = 0f;//点击X位置
    private float touchY = 0f;//点击Y位置
    private float curX = 0f;//当前X位置
    private float curY = 0f;//当前Y位置

    private float minX = 0f;//可以拖动最小x轴坐标
    private float maxX = 0f;//可以拖动最大y轴坐标

    private float minY = 0f;//可以拖动最小x轴坐标
    private float maxY = 0f;//可以拖动最大y轴坐标

    private int centerX = 0;//当前悬浮球中心点x轴坐标
    private int centerY = 0;//当前悬浮球中心点y轴坐标

    private int leftLength = 0;//悬浮球中心点离左边的具体
    private int rightLength = 0;//悬浮球中心点离右边的具体
    private int topLength = 0;//悬浮球中心点离上边的具体
    private int bottomLength = 0;//悬浮球中心点离下边的具体



    public DragFloatView(Activity context, ViewGroup parentView) {
        super(context, parentView, R.layout.layout_drag_float);
        mActivity = context;
    }

    /**
     * 初始化数据
     */
    private void initData(View view) {
        boolean isPortrait = (mActivity.getChangingConfigurations() == Configuration.ORIENTATION_PORTRAIT);
        view.measure(0, 0);
        width = view.getMeasuredWidth();
        height = view.getMeasuredHeight();
        minX = 0f;
        maxX = Utils.getScreenWidth(getContext()) - width;
        minY = 0f;
        maxY = Utils.getScreenHeight(getContext()) - height;
        Log.i(FloatBiz.TAG, "----maxX----- = " + maxX + "   maxY = " + maxY + "  width = " + width + "  height = " + height + "  isPortrait = " + isPortrait);
    }

    /**
     * 贴边滑行动画
     * 根据横竖屏，对应悬浮球的吸边策略：
     */
    private void animationSlide() {
        if (contentView != null) {
            leftLength = centerX;
            rightLength = Utils.getScreenWidth(getContext()) - centerX;
            topLength = centerY;
            bottomLength = Utils.getScreenHeight(getContext()) - centerY;
            int lengths[] = {leftLength, rightLength, topLength, bottomLength};

            Log.i(FloatBiz.TAG, "leftLength = "+leftLength+"  rightLength = "+rightLength+"  topLength = "+topLength+"  bottomLength = "+bottomLength);
            Log.i(FloatBiz.TAG, "curX = "+curX+"  curY = "+curY);
            int min = Utils.getMin(lengths);
            if(min == leftLength){//向左移动
                Log.i(FloatBiz.TAG, "----向左移动-----");
            }
            if(min == rightLength){//向右移动
                Log.i(FloatBiz.TAG, "----向右移动-----");
            }
            if(min == topLength){//向上移动
                Log.i(FloatBiz.TAG, "----向上移动-----");
            }
            if(min == bottomLength){//向下移动
                Log.i(FloatBiz.TAG, "----向下移动-----");
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void initView() {
        super.initView();
        iv_drag_float = findView(R.id.iv_drag_float);
        initData(contentView);
        contentView.setOnTouchListener(onTouchListener);
    }

    /**
     * 图片拖动监听
     */
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchX = event.getX();
                    touchY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    curX = (int) (lastX - touchX);
                    curY = (int) (lastY - touchY);
                    if (curX < minX) {
                        curX = minX;
                    }
                    if (curX > maxX) {
                        curX = maxX;
                    }
                    if (curY < minY) {
                        curY = minY;
                    }
                    if (curY > maxY) {
                        curY = maxY;
                    }
                    contentView.setX(curX);
                    contentView.setY(curY);
                    centerX = (int) (curX + width/2);
                    centerY = (int) (curY + height/2);
                    break;
                case MotionEvent.ACTION_UP:
                    animationSlide();
                    break;
            }
            lastX = event.getRawX();
            lastY = event.getRawY() - Utils.getStatusBarHeight(getContext());
            return true;
        }
    };


}
