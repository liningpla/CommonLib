package com.floatingwindow.viewhelpers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.common.log.SDLog;
import com.common.utils.ScreenUtil;
import com.floatingwindow.R;

/**游戏助手悬浮视图辅助类*/
public class AssistentHelper {

    public static final String UU_TAG = "UU_TAG";
    private Context context;
    private WindowManager wManager;// 窗口管理者
    private WindowManager.LayoutParams mParams;// 窗口的属性
    private ConstraintLayout cl_float_parent;

    private float lastX = 0f;//上次X位置
    private float lastY = 0f;//上次Y位置
    private float touchX = 0f;//点击X位置
    private float touchY = 0f;//点击Y位置
    private int limitMaxX = 0;//X最大限制位置
    private int limitMaxY = 0;//Y最大限制位置
    private boolean isPortrait;//是否是竖屏。

    private static final int LOCATION_RIGHT = 1;//位置在
    private static final int LOCATION_RIGHT_TOP = 2;
    private static final int LOCATION_RIGHT_BOTTOM = 3;

    private static final int LOCATION_LEFT = 4;
    private static final int LOCATION_LEFT_TOP = 5;
    private static final int LOCATION_LEFT_BOTTOM = 6;

    private View myView;
    private ImageView iv_assistent_main;//主视图
    private ImageView iv_assistent_recording;//录屏
    private ImageView iv_assistent_screenshot;//截屏
    private ImageView iv_assistent_accelerate;//加速
    private ImageView iv_assistent_welfare;//福利
    private ImageView iv_assistent_chitchat;//聊天

    public AssistentHelper(Context context) {
        this.context = context;
        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        if (context == null) {
            return;
        }
        wManager = (WindowManager) context.getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        pession();
        myView = LayoutInflater.from(context).inflate(R.layout.layout_assistent_floating, null);
        myView.getRootView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == View.SYSTEM_UI_FLAG_FULLSCREEN || visibility == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) {
                    SDLog.create().i(AssistentHelper.UU_TAG, "AssistentHelper", "---------全屏状态--------" + visibility);
                } else {
                    SDLog.create().i(AssistentHelper.UU_TAG, "AssistentHelper", "---------非全屏状态--------" + visibility);
                }

            }
        });
        cl_float_parent = myView.findViewById(R.id.cl_float_parent);
        iv_assistent_main = myView.findViewById(R.id.iv_assistent_main);
        iv_assistent_recording = myView.findViewById(R.id.iv_assistent_recording);
        iv_assistent_screenshot = myView.findViewById(R.id.iv_assistent_screenshot);
        iv_assistent_accelerate = myView.findViewById(R.id.iv_assistent_accelerate);
        iv_assistent_welfare = myView.findViewById(R.id.iv_assistent_welfare);
        iv_assistent_chitchat = myView.findViewById(R.id.iv_assistent_chitchat);
        mParams.format = PixelFormat.TRANSLUCENT;// 支持透明
        //允许移除屏幕外且不捕获焦点
        mParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.height = ScreenUtil.dip2px(context, 198);
        mParams.width = ScreenUtil.dip2px(context, 198);
        mParams.x = 0;//窗口位置的偏移量
        mParams.y = 0;
        showWindow();

        myView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isPortrait = (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)?true:false;
                        initLimit();
                        touchX = event.getX();
                        touchY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateLoaction();
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                lastX = event.getRawX();
                lastY = event.getRawY() - ScreenUtil.getStatusBarHeight(context);
                return true;
            }
        });

    }

    /**
     * 适配Build api 8.0 以上系统悬浮窗权限适配
     */
    private void pession() {
        //权限判断
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(context)) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
                return;
            } else {
                //执行6.0以上绘制代码
                if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
                    mParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                }
            }
        } else {
            //执行6.0以下绘制代码、
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
    }


    /**横竖屏变化监听*/
    public void onConfigurationChanged(boolean isPortrait){
        this.isPortrait = isPortrait;
        initLimit();
    }

    /**初始化移动限制位置*/
    private void initLimit(){
        if(isPortrait){
            SDLog.create().i(AssistentHelper.UU_TAG, "AssistentHelper", " 竖屏 ");
        }else{
            SDLog.create().i(AssistentHelper.UU_TAG, "AssistentHelper", " 横屏 ");
        }
        limitMaxX = isPortrait?ScreenUtil.getScreenWidth(context) - mParams.width:ScreenUtil.getScreenHeight(context) - mParams.height;
        limitMaxY = !isPortrait?ScreenUtil.getScreenWidth(context) - mParams.width:ScreenUtil.getScreenHeight(context) - mParams.height;
    }
    /**
     * 关闭
     */
    public void dismissWindow() {
        if (myView != null && myView.getParent() != null) {
            wManager.removeView(myView);//移除窗口
            myView = null;
        }
    }

    /**
     * 展示
     */
    public void showWindow() {
        if (myView == null || myView.getParent() != null) {
            return;
        }
        try {
            wManager.addView(myView, mParams);//添加窗口
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新位置
     */
    private void updateLoaction() {
        if (wManager != null && mParams != null && myView != null) {
            mParams.x = (int) (lastX - touchX);
            mParams.y = (int) (lastY - touchY);
            SDLog.create().i(AssistentHelper.UU_TAG, "AssistentHelper", " mParams.x:" +  mParams.x+" mParams.y:"+ mParams.y);
            SDLog.create().i(AssistentHelper.UU_TAG, "AssistentHelper", " mParams.width:" +  mParams.width+" mParams.height:"+ mParams.height);
            if(mParams.x < 0){
                mParams.x = 0;
            }
            if(mParams.x > limitMaxX){
                mParams.x = limitMaxX;
            }
            if(mParams.y < 0){
                mParams.y = 0;
            }
            if(mParams.y > limitMaxY){
                mParams.y = limitMaxY;
            }
            wManager.updateViewLayout(myView, mParams);
        }

    }
}
