package com.floatingwindow.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

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

    private int shockSpace = 0;//吸边震荡距离

    private long downTime = 0L;//记录点击的时间


    public DragFloatView(Activity context, ViewGroup parentView) {
        super(context, parentView, R.layout.layout_drag_float);
        mActivity = context;
    }

    public void addParent() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT
                , FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.START;
        addToParent(params);
    }

    /**
     * 初始化数据
     */
    private void initData(View view) {
        boolean isPortrait = (mActivity.getChangingConfigurations() == Configuration.ORIENTATION_PORTRAIT);
        view.measure(0, 0);
        shockSpace = Utils.dip2px(getContext(), 10);
        width = view.getMeasuredWidth();
        height = view.getMeasuredHeight();
        minX = 0f;
        maxX = Utils.getScreenWidth(getContext()) - width;
        minY = 0f;
        maxY = Utils.getScreenHeight(getContext()) - height - Utils.dip2px(getContext(), 1);
        curY = maxY/2;
        contentView.setY(curY);
        animationSlide();
        Log.i(FloatBiz.TAG, "----maxX----- = " + maxX + "   maxY = " + maxY + "  width = " + width + "  height = " + height + "  isPortrait = " + isPortrait);
    }

    /**
     * 贴边滑行动画
     * 根据横竖屏，对应悬浮球的吸边策略：
     */
    private void animationSlide() {
        if (contentView != null) {
            float[] moveValues = null;
            float[] shockValues = null;
            String propertyName = "translationX";
            leftLength = centerX;
            rightLength = Utils.getScreenWidth(getContext()) - centerX;
            topLength = centerY;
            bottomLength = Utils.getScreenHeight(getContext()) - centerY;
            int lengths[] = {leftLength, rightLength, topLength, bottomLength};

            Log.i(FloatBiz.TAG, "leftLength = " + leftLength + "  rightLength = " + rightLength + "  topLength = " + topLength + "  bottomLength = " + bottomLength);
            Log.i(FloatBiz.TAG, "curX = " + curX + "  curY = " + curY);
            int min = Utils.getMin(lengths);
            if (min == bottomLength) {//向下移动
                Log.i(FloatBiz.TAG, "----向下移动-----");
                propertyName = "translationY";
                moveValues = new float[]{curY, maxY};
                shockValues = new float[]{maxY, maxY - shockSpace, maxY + (height / 2)};
            }
            if (min == topLength) {//向上移动
                Log.i(FloatBiz.TAG, "----向上移动-----");
                propertyName = "translationY";
                moveValues = new float[]{curY, 0};
                shockValues = new float[]{0, shockSpace, -(height / 2)};
            }
            if (min == rightLength) {//向右移动
                Log.i(FloatBiz.TAG, "----向右移动-----");
                propertyName = "translationX";
                moveValues = new float[]{curX, maxX};
                shockValues = new float[]{maxX, maxX - shockSpace, maxX + (width / 2)};
            }
            if (min == leftLength) {//向左移动
                Log.i(FloatBiz.TAG, "----向左移动-----");
                propertyName = "translationX";
                moveValues = new float[]{curX, 0};
                shockValues = new float[]{0, shockSpace, -(width / 2)};
            }
            //移动动画
            ObjectAnimator move = ObjectAnimator.ofFloat(contentView, propertyName, moveValues);
            move.setInterpolator(new DecelerateInterpolator());
            move.setDuration(200);
            //震荡动画
            ObjectAnimator shock = ObjectAnimator.ofFloat(contentView, propertyName, shockValues);
            shock.setInterpolator(new DecelerateInterpolator());
            shock.setDuration(150);
            AnimatorSet animatorSet = new AnimatorSet();//创建动画集
            animatorSet.play(move).before(shock);
            animatorSet.setDuration(350);
            animatorSet.start();//开始执行动画
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
                    downTime = SystemClock.elapsedRealtime();
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
                    centerX = (int) (curX + width / 2);
                    centerY = (int) (curY + height / 2);
                    break;
                case MotionEvent.ACTION_UP:
                    long stopTime = SystemClock.elapsedRealtime() - downTime;
                    if(stopTime < 200){
                        TODO://PopupWindow实现会员中心，可以实现返回键关闭
                        Toast.makeText(mContext, "显示会员中心", Toast.LENGTH_LONG).show();
                    }
                    animationSlide();
                    break;
            }
            lastX = event.getRawX();
            lastY = event.getRawY() - Utils.getStatusBarHeight(getContext());
            return true;
        }
    };


}
