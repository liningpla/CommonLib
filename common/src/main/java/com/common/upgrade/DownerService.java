package com.common.upgrade;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.common.threadPool.Priority;
import com.common.threadPool.ThreadManger;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DownerService extends Service {

    public static final String DOWN_REQUEST = "down_request";
    /**调度任务状态-断开-下载服务*/
    private static final int SCHEDULE_STATUS_DISCONNECTED = 0x5121;
    /**调度任务状态-暂停-下载服务*/
    private static final int SCHEDULE_STATUS_PAUSE = 0x5122;
    /**调度任务状态-恢复-下载服务*/
    private static final int SCHEDULE_STATUS_RESUME = 0x5123;
    /**网络状态监听*/
    private NetWorkStateReceiver netWorkStateReceiver;
    /**安装包状态监听*/
    private PackagesReceiver packagesReceiver;
    /**下载任务管理*/
    public Map<String, SoftReference<ScheduleRunable>> scheduleRunables = new LinkedHashMap<>();
    /**下载进度通知栏管理*/
    private NotificationManager notificationManager;
    /**通知栏ID*/
    public static final String NOTIFY_CHANNEL_ID = "下载管理";
    /**传给service，添加下载*/
    public static void startDownerService(Context context, DownerRequest downerRequest){
        if(downerRequest.downerCallBack != null){//链接服务下载
            downerRequest.downerCallBack.onConnected(downerRequest);
        }
        Intent intent = new Intent(context, DownerService.class);
        intent.setAction("android.intent.action.RESPOND_VIA_MESSAGE");
        intent.putExtra(DOWN_REQUEST, downerRequest);
        context.startService(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(Downer.TAG, "DownerService:  onCreate ");
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DownerRequest downerRequest = intent.getParcelableExtra(DOWN_REQUEST);
        String url = downerRequest.options.getUrl();
        Log.i(Downer.TAG, "DownerService:  onStartCommand url :"+url+" status:"+downerRequest.status);
        if(isNeedSchedule(downerRequest)){
            ScheduleRunable scheduleRunable = new ScheduleRunable(this, downerRequest);
            SoftReference<ScheduleRunable> softSchedule = new SoftReference<>(scheduleRunable);
            scheduleRunables.put(url, softSchedule);
            ThreadManger.getInstance().execute(Priority.NORMAL, scheduleRunable);
            Log.i(Downer.TAG, "DownerService:  onStartCommand  execute");
        }
        return START_STICKY;
    }

    /**下载请求是否需要调度*/
    private boolean isNeedSchedule(DownerRequest downerRequest){
        String url = downerRequest.options.getUrl();
        //程序启动状态下下载没有调度过
        if(!scheduleRunables.containsKey(url)){
            return true;
        }
        //程序启动状态下，下载调度过
        ScheduleRunable scheduleRunable = scheduleRunables.get(url).get();
        File dowed = scheduleRunable.downerRequest.options.getStorage();
        //虽然调度过，但是下载的文件不存在，需要下载的文件没有下载完成，都需要继续调度下载
        if(dowed == null || !dowed.exists() || dowed.length() < downerRequest.options.getFilelength()){
            return true;
        }
        return false;
    }

    /**初始化网络监听，安装管理监听，通知栏通知*/
    private void init(){
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
            netWorkStateReceiver.registerReceiver(this);
        }
        if (packagesReceiver == null) {
            packagesReceiver = new PackagesReceiver();
            packagesReceiver.registerReceiver(this);
        }
        initNotify();
    }

    /**初始化通知栏通知*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initNotify() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFY_CHANNEL_ID, NOTIFY_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(false);
            channel.setSound(null, null);
            channel.setVibrationPattern(null);
            notificationManager.createNotificationChannel(channel);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(Downer.TAG, "DownerService:  onDestroy ");
        if (netWorkStateReceiver != null) {
            netWorkStateReceiver.unregisterReceiver(this);
        }
        if (packagesReceiver != null) {
            packagesReceiver.unregisterReceiver(this);
        }
        iteratorSchedule(SCHEDULE_STATUS_DISCONNECTED, "");
    }

    /**遍历下载调度任务
     * @param scheduleStatus 任务状态
     * @param apkpagename 下载apk的包明
     * */
    private void iteratorSchedule(int scheduleStatus, String apkpagename){
        Iterator<Map.Entry<String, SoftReference<ScheduleRunable>>> entries = scheduleRunables.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, SoftReference<ScheduleRunable>> entry = entries.next();
            ScheduleRunable scheduleRunable = entry.getValue().get();
            switch (scheduleStatus){
                case SCHEDULE_STATUS_DISCONNECTED:
                    if(scheduleRunable.downerCallBack != null){
                        scheduleRunable.downerCallBack.onDisconnected();
                    }
                    Log.i(Downer.TAG, "DownerService:iteratorSchedule:Schedule is disconnected");
                    break;
                case SCHEDULE_STATUS_RESUME:
                    if(scheduleRunable.downerRequest != null && scheduleRunable.downerRequest.status == Downer.STATUS_DOWNLOAD_PAUSE){
                        scheduleRunable.downerRequest.resume(DownerService.this);
                    }
                    Log.i(Downer.TAG, "DownerService:iteratorSchedule:Schedule is resume");
                    break;
                case SCHEDULE_STATUS_PAUSE:
                    if(scheduleRunable.downerRequest != null){
                        scheduleRunable.downerRequest.pause();
                    }
                    Log.i(Downer.TAG, "DownerService:iteratorSchedule:Schedule is pause");
                    break;
                case Downer.STATUS_INSTALL_COMPLETE:
                    if(scheduleRunable.downerCallBack != null){
                        String requesPageName = scheduleRunable.downerRequest.apkPageName;
                        if(TextUtils.equals(requesPageName, apkpagename)){
                            scheduleRunable.downerCallBack.onCompleteInstall();
                            Log.i(Downer.TAG, "DownerService:iteratorSchedule:Schedule install completed");
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 网络状态变化广播
     */
    private class NetWorkStateReceiver extends BroadcastReceiver {
        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            // WIFI已连接，移动数据已连接
            if (wifiNetworkInfo.isConnected() && mobileNetworkInfo.isConnected()) {
                //如果时暂停状态，恢复网络重新下载
                iteratorSchedule(SCHEDULE_STATUS_RESUME,"");
                return;
            }
            // WIFI已连接，移动数据已断开
            if (wifiNetworkInfo.isConnected() && !mobileNetworkInfo.isConnected()) {
                //如果时暂停状态，恢复网络重新下载
                iteratorSchedule(SCHEDULE_STATUS_RESUME,"");
                return;
            }
            // WIFI已断开，移动数据已连接
            if (!wifiNetworkInfo.isConnected() && mobileNetworkInfo.isConnected()) {
                //如果时暂停状态，恢复网络重新下载
                iteratorSchedule(SCHEDULE_STATUS_RESUME,"");
                return;
            }
            // WIFI已断开，移动数据已断开，执行暂停操作
            iteratorSchedule(SCHEDULE_STATUS_PAUSE,"");
        }

        public void registerReceiver(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(this, intentFilter);
        }

        public void unregisterReceiver(Context context) {
            context.unregisterReceiver(this);
        }
    }

    /**
     * 程序状态变化广播
     */
    private class PackagesReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = null;
            if (intent.getData() != null) {
                packageName = intent.getData().getSchemeSpecificPart();
            }
            String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {//安装完成
                iteratorSchedule(Downer.STATUS_INSTALL_COMPLETE, packageName);
                Log.i(Downer.TAG, "onReceive：Added " + packageName);
            } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {//更新版本完成
                Log.i(Downer.TAG, "onReceive：Replaced " + packageName);
                iteratorSchedule(Downer.STATUS_INSTALL_COMPLETE, packageName);
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {//卸载
                Log.i(Downer.TAG, "onReceive：Removed " + packageName);
            }
        }
        public void registerReceiver(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addDataScheme("package");
            context.registerReceiver(this, intentFilter);
        }
        public void unregisterReceiver(Context context) {
            context.unregisterReceiver(this);
        }
    }

}
