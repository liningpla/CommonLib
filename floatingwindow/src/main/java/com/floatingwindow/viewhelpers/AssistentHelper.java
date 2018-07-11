package com.floatingwindow.viewhelpers;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.common.utils.ScreenUtil;
import com.floatingwindow.R;

/**游戏助手悬浮视图辅助类*/
public class AssistentHelper {

    private Context context;
    private WindowManager wManager;// 窗口管理者
    private WindowManager.LayoutParams mParams;// 窗口的属性

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
    /**初始化视图*/
    private void initView(){
        if(context == null){
            return;
        }
        wManager = (WindowManager) context.getApplicationContext().getSystemService(
                Context.WINDOW_SERVICE);
        mParams = new WindowManager.LayoutParams();
        pession();
        myView = LayoutInflater.from(context).inflate(R.layout.layout_assistent_floating, null);
        iv_assistent_main = myView.findViewById(R.id.iv_assistent_main);
        iv_assistent_recording = myView.findViewById(R.id.iv_assistent_recording);
        iv_assistent_screenshot = myView.findViewById(R.id.iv_assistent_screenshot);
        iv_assistent_accelerate = myView.findViewById(R.id.iv_assistent_accelerate);
        iv_assistent_welfare = myView.findViewById(R.id.iv_assistent_welfare);
        iv_assistent_chitchat = myView.findViewById(R.id.iv_assistent_chitchat);
        mParams.format = PixelFormat.TRANSLUCENT;// 支持透明
        //允许移除屏幕外且不捕获焦点
        mParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;//窗口的宽和高
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.gravity = Gravity.CENTER_HORIZONTAL |Gravity.BOTTOM;
        mParams.height = ScreenUtil.getScreenHeight(context);
        mParams.width = ScreenUtil.getScreenWidth(context);
        mParams.x = 0;//窗口位置的偏移量
        mParams.y = 0;
        showWindow();
    }
    /**适配Build api 8.0 以上系统悬浮窗权限适配*/
    private void pession(){
        //权限判断
        if (Build.VERSION.SDK_INT >= 23) {
            if(!Settings.canDrawOverlays(context)) {
                //启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
                return;
            } else {
                //执行6.0以上绘制代码
                if (Build.VERSION.SDK_INT>=26) {//8.0新特性
                    mParams.type= WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                }else{
                    mParams.type= WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
                }
            }
        } else {
            //执行6.0以下绘制代码、
            mParams.type= WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
    }
    /**关闭*/
    public void dismissWindow() {
        if (myView!=null && myView.getParent() != null) {
            wManager.removeView(myView);//移除窗口
            myView = null;
        }
    }

    /**展示*/
    public void showWindow(){
        if (myView==null ||  myView.getParent() != null) {
            return;
        }
        try {
            wManager.addView(myView, mParams);//添加窗口
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**加速属性弹窗动画
     * @param isShow true 显示动画，false 关闭动画
     * */
    public void animShow(boolean isShow){
        wManager.updateViewLayout(myView, mParams);
    }
}
