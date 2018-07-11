package com.common.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Method;

public class ScreenUtil {

    private static final int NO_VALUE = -1;

    private static int screen_widht = NO_VALUE;
    private static int screen_height = NO_VALUE;
    private static DisplayMetrics metric;

    public static int getScreenWidth(Application application) {
        if (screen_widht == NO_VALUE) {
            init(application);
        }
        return screen_widht;
    }

    public static int getScreenHeight(Application application) {
        if (screen_height == NO_VALUE) {
            init(application);
        }
        return screen_height;
    }

    private static void init(Application application) {
        WindowManager wm = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
        Display ds = wm.getDefaultDisplay();
        screen_widht = ds.getWidth();
        screen_height = ds.getHeight();
    }

    private static void getDisplayMetrics(Application application) {
        WindowManager wm = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
        metric = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metric);
    }

    public static int dip2px(Application application, float dpValue) {
        final float scale = application.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dp2px(Application application, float dp) {
        Resources r = application.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int) px;
    }

    public static int sp2px(Application application, float spValue) {
        Resources r = application.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, r.getDisplayMetrics());
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * hide Nexus5
     *
     * @param activity
     */
    public static void hideSoftKeys(Activity activity) {
        if (Build.VERSION.SDK_INT < 14) {
            return;
        }
        int uiOptions = activity.getWindow().getDecorView().getSystemUiVisibility();
        int newUiOptions = uiOptions;
        if (Build.VERSION.SDK_INT >= 14) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        if (Build.VERSION.SDK_INT >= 16) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
        if (Build.VERSION.SDK_INT >= 18) {
            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
    }

    public static void showSoftKeys(Activity activity) {
        if (Build.VERSION.SDK_INT < 14) {
            return;
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**状态栏是否显示*/
    public static boolean isStatusBarVisible(Activity activity) {
        if ((activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**获取状态栏高度*/
    public static int getStatusBarHeight(Application application) {
        int statusBarHeight = -1;
        //获取status_bar_height资源的ID
        int resourceId = application.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight = application.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**Activity 全屏设置*/
    public static void fullScreen(Activity activity, boolean enable) {
        if (enable) {
            int uiFlags = View.SYSTEM_UI_FLAG_FULLSCREEN; //hide statusBar
            activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            activity.getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    /**获取底部导航栏高度*/
    public static int getNavigationBarHeight(Application application) {
        int height = 0;
        boolean hasNavigationBar = false;
        Resources rs = application.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (hasNavigationBar) {
                Resources resources = application.getResources();
                int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
                height = resources.getDimensionPixelSize(resourceId);
            }
        }
        return height;
    }

    /**
     * 获取底部导航栏高度：如果不为 0 则正在显示阶段
     *
     * @return
     */
    public static int getNavigationHei(Activity context) {
        int dpi = 0;
        Display display = context.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi - display.getHeight();
    }
}
