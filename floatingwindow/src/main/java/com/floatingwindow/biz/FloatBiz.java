package com.floatingwindow.biz;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.floatingwindow.R;
import com.floatingwindow.ui.DragFloatView;

import java.util.List;

public enum  FloatBiz {
    INIT;
    private DragFloatView dragFloatView;

    public static final String TAG = "FloatBiz";
    public void initFloat(Application application){
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                String simpleName = activity.getClass().getSimpleName();
                Log.i(TAG, "----onActivityResumed-----"+simpleName);
                if(dragFloatView != null){
                    dragFloatView.remove();
                }
                if(!TextUtils.equals(simpleName, "ProxyActivity")){
                    ViewGroup mRootView = activity.findViewById(android.R.id.content);
                    Rect rootViewRect = new Rect();
                    mRootView.getGlobalVisibleRect(rootViewRect);
                    dragFloatView = new DragFloatView(activity, mRootView);
                    dragFloatView.addParent();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }


    /**
     * 处理当应用处于后台时，栈中Activity的判断
     * @param intent
     */
    private void foregroundActivity(Context context, Intent intent) {
        if (intent.getData() == null) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            try {
                List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(10);
                if (list != null && list.size() > 0) {
                    for (ActivityManager.RunningTaskInfo info :
                            list) {
                        if (info != null) {
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
