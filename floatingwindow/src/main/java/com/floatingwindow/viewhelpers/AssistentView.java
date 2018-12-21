package com.floatingwindow.viewhelpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;

import com.common.log.SDLog;
import com.common.utils.ScreenUtil;

/**游戏助手悬浮视图辅助类*/
public class AssistentView {

    public static final String UU_TAG = "UU_TAG";
    private Context context;
    private WindowManager wManager;// 窗口管理者
    private WindowManager.LayoutParams mParams;// 窗口的属性


    private AssistentModule.APoint aPoint;
    public ImageView myView;
    public int position;
    public AssistentView(Context context, AssistentModule.APoint aPoint) {
        this.context = context;
        this.aPoint = aPoint;
        position = aPoint.positon;
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
        myView.setImageResource(aPoint.resId);
        mParams.format = PixelFormat.TRANSLUCENT;// 支持透明
        //允许移除屏幕外且不捕获焦点
        mParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        mParams.gravity = Gravity.LEFT | Gravity.TOP;
        mParams.height = ScreenUtil.dip2px(context, 45);
        mParams.width = ScreenUtil.dip2px(context, 45);
        showWindow();
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
        if (myView != null && aPoint != null) {
            try {
                mParams.x = aPoint.pointX;//窗口位置的偏移量
                mParams.y = aPoint.pointY;
                SDLog.i(AssistentHelper.UU_TAG, "position:"+position+"  mParams.x:"+mParams.x+"  mParams.y:"+mParams.y);
                wManager.addView(myView, mParams);//添加窗口
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新位置
     */
    public void updateLoaction(AssistentModule.APoint aPoint) {
        if (wManager != null && mParams != null && myView != null) {
            this.aPoint = aPoint;
            mParams.x = aPoint.pointX;//窗口位置的偏移量
            mParams.y = aPoint.pointY;
            wManager.updateViewLayout(myView, mParams);
        }
    }
}
