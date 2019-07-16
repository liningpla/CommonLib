package com.example.notificationtest.biz;

import android.app.Activity;
import android.content.IntentSender;
import android.util.Log;

import com.example.notificationtest.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import java.lang.ref.SoftReference;

/**使用google play 下载更新app，自带UX*/
public enum  GooglePlayBiz {

    instance;

    public static final String TAG = "HiUpdate";
    public static final int MY_REQUEST_CODE = 0x1001;
    AppUpdateManager appUpdateManager;
    MyInstallStateUpdatedListener listener;

    /**访问更新*/
    public void updateGooglePlay(Activity activity){

        // 创建管理器的实例
        appUpdateManager = AppUpdateManagerFactory.create(activity);
        listener = new MyInstallStateUpdatedListener(activity);
        // 返回用于检查更新的intent对象。
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // 检查平台是否允许指定类型的更新。
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // 对于非强制性更新, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    // 请求更新.
                Log.i(GooglePlayBiz.TAG,"  ---检查到更新完成----");
                try {
                    //在开始更新之前，请注册更新的侦听器。
                    appUpdateManager.registerListener(listener);
                    appUpdateManager.startUpdateFlowForResult(
                            // 传递'getAppUpdateInfo()'返回的意图
                            appUpdateInfo,
                            // 也可以 使用'AppUpdateType.FLEXIBLE' 非强制性更新
                            AppUpdateType.IMMEDIATE,
                            // 发出更新请求的当前活动
                            activity,
                            // 包含一个请求代码，以便稍后监视此更新请求。
                            MY_REQUEST_CODE);


                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    Log.i(GooglePlayBiz.TAG,"  ---检查到更新完成----"+e.getMessage());
                }
            }else{
                Log.i(GooglePlayBiz.TAG,"  ---没有检查到更新----");
            }
        });

    }

    /**当不再需要状态更新时，取消注册侦听器*/
    public void unregisterListener(){
        if(appUpdateManager != null && listener != null){
            appUpdateManager.unregisterListener(listener);
        }

    }

    /**创建一个侦听器来跟踪请求状态更新*/
    public class MyInstallStateUpdatedListener implements InstallStateUpdatedListener{

        private SoftReference<Activity> softActivity;
        public MyInstallStateUpdatedListener(Activity activity){
            softActivity = new SoftReference<>(activity);
        }

        @Override
        public void onStateUpdate(InstallState state) {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                // 下载更新之后，显示一个通知
                // 并请求用户确认重新启动应用程序。
                popupSnackbarForCompleteUpdate(softActivity.get());
            }
        }
    }

    /**显示snackbar通知并调用action**/
    private void popupSnackbarForCompleteUpdate(Activity activity) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(R.id.activity_main_layout),
                        "An update has just been downloaded.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor( activity.getResources().getColor(android.R.color.darker_gray));
        snackbar.show();
    }


}
