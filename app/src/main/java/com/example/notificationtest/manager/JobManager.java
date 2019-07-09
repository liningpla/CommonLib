package com.example.notificationtest.manager;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Rect;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;

import androidx.annotation.RequiresApi;

import com.common.utils.SDLog;
import com.example.notificationtest.services.MyJobService;

import java.util.List;

/**
 * 定时任务管理器
 */
public enum JobManager {

    INSTANCE;

    public static final int JOB_ID = 11;

    /**
     * 初始化JobService
     */
    public void initJobService(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            SDLog.d("initJobService");
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, new ComponentName(context, MyJobService.class));
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setRequiresCharging(false); //是否在充电时执行
            builder.setRequiresDeviceIdle(false); //是否在空闲时执行
            builder.setMinimumLatency(500); //设置至少延迟多久后执行，单位毫秒
            builder.setOverrideDeadline(3000); //设置最多延迟多久后执行，单位毫秒
            builder.setPeriodic(1000);
            JobInfo ji = builder.build();
            JobScheduler js = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            js.schedule(ji);
        } else {
            SDLog.d("Version < LOLLIPOP , Init JobService Failed");
        }

    }


    public void testScreen(Activity activity, final View view) {
        SDLog.i();
        if (android.os.Build.VERSION.SDK_INT >= 28) {
            WindowInsets windowInsets = activity.getWindow().getDecorView().getRootWindowInsets();
            if (windowInsets != null) {
                SDLog.i(windowInsets.toString());
                DisplayCutout displayCutout = windowInsets.getDisplayCutout();
                if (displayCutout != null) {
                    List<Rect> rects = displayCutout.getBoundingRects();
                    //通过判断是否存在rects来确定是否刘海屏手机
                    if (rects != null && rects.size() > 0) {
                        SDLog.i("刘海屏");
                    }
                }

                if(view != null){
                    view.postDelayed(new Runnable() {
                        @RequiresApi(28)
                        @Override
                        public void run() {
                            DisplayCutout displayCutout = view.getRootWindowInsets().getDisplayCutout();
                            if(displayCutout != null){
                                SDLog.i("SafeInsetBottom:" + displayCutout.getSafeInsetBottom());
                                SDLog.i("SafeInsetLeft:" + displayCutout.getSafeInsetLeft());
                                SDLog.i("SafeInsetRight:" + displayCutout.getSafeInsetRight());
                                SDLog.i("SafeInsetTop:" + displayCutout.getSafeInsetTop());
                            }else{
                                SDLog.i("displayCutout != null");
                            }

                        }
                    }, 100);
                }

            }

        }
    }


}
