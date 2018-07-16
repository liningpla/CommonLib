package com.floatingwindow.viewhelpers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.common.log.SDLog;
import com.common.utils.ScreenUtil;
import com.floatingwindow.R;

import java.util.ArrayList;
import java.util.List;

/**游戏助手悬浮视图辅助类*/
public class AssistentHelper {

    public static final String UU_TAG = "UU_TAG";
    private Context context;
    private WindowManager wManager;// 窗口管理者
    private WindowManager.LayoutParams mParams;// 窗口的属性
    private float lastX = 0f;//上次X位置
    private float lastY = 0f;//上次Y位置
    private float touchX = 0f;//点击X位置
    private float touchY = 0f;//点击Y位置
    public int limitMaxX = 0;//X最大限制位置
    public int limitMaxY = 0;//Y最大限制位置
    private boolean isPortrait;//是否是竖屏。
    private ImageView myView;
    private List<AssistentView> viewList = new ArrayList<>();

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
        myView = new ImageView(context);
        myView.setImageResource(R.drawable.ic_launcher);
        mParams.format = PixelFormat.TRANSLUCENT;// 支持透明
        //允许移除屏幕外且不捕获焦点
        mParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.height = ScreenUtil.dip2px(context, 54);
        mParams.width = ScreenUtil.dip2px(context, 54);
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
                        addChildView();
                        break;
                }
                lastX = event.getRawX();
                lastY = event.getRawY() - ScreenUtil.getStatusBarHeight(context);
                return true;
            }
        });

    }

    private void addChildView(){
        if (wManager != null && mParams != null && myView != null) {//异常判断
            AssistentModule assistentModule = new AssistentModule();
            AssistentModule.APoint aPoint = assistentModule.initPoints(context, isPortrait, mParams.width,
                    mParams.x, mParams.y, ScreenUtil.dip2px(context, 45), ScreenUtil.dip2px(context, 81));
            mParams.x = aPoint.pointX;
            mParams.y = aPoint.pointY;
            wManager.updateViewLayout(myView, mParams);
            if(viewList.size() == 5){
                for(AssistentView asssitentView: viewList){
                    asssitentView.updateLoaction(assistentModule.currChilds.get(asssitentView.position));
                }
            }else {
                viewList.clear();
                for(int i = 0; i < assistentModule.currChilds.size(); i ++){
                    AssistentView assistentView = new AssistentView(context, assistentModule.currChilds.get(i));
                    viewList.add(assistentView);
                }
            }
        }
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
