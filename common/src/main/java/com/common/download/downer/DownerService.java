package com.common.download.downer;

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
import android.util.Log;

import com.common.download.Downer;
import com.common.download.thread.Priority;
import com.common.download.thread.ThreadManger;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DownerService extends Service {

    public static final String DOWN_REQUEST = "down_request";
    /**调度任务状态-断开-下载服务*/
    private static final int SCHEDULE_STATUS_DISCONNECTED = 0x5121;
    /**调度任务状态-中断-下载服务*/
    private static final int SCHEDULE_STATUS_STOP = 0x5122;
    /**调度任务状态-恢复-下载服务*/
    private static final int SCHEDULE_STATUS_RESUME = 0x5123;
    /**网络状态监听*/
    private NetWorkStateReceiver netWorkStateReceiver;
    /**安装包状态监听*/
    private PackagesReceiver packagesReceiver;
    /**下载监听管理*/
    public static Map<String, SoftReference<DownerRequest>> downerRequests = new LinkedHashMap<>();
    /**下载进度通知栏管理*/
    private NotificationManager notificationManager;
    /**通知栏ID*/
    public static final String NOTIFY_CHANNEL_ID = "下载管理";
    /**传给service，添加下载*/
    public static void startDownerService(Context context, DownerRequest downerRequest){
        SoftReference<DownerRequest> softRequest = new SoftReference<>(downerRequest);
        //是否调度
        boolean isSchedule = true;
        if(downerRequests.containsKey(downerRequest.options.getUrl())){//已经添加到任务中
            isSchedule = false;
            //连接状态下，和加载状态下载，不再重复执行任务
            if(downerRequest.status == Downer.STATUS_DOWNLOAD_STOP|| downerRequest.status == Downer.STATUS_DOWNLOAD_PAUSE){
                isSchedule = true;
            }
        }
        if(!isSchedule){
            Log.i(Downer.TAG, "DownerService:startDownerService "+downerRequest.options.getTitle()+" is scheduleing");
            return;
        }
        downerRequests.put(downerRequest.options.getUrl(), softRequest);
        if(downerRequest.downerCallBack != null){//链接服务下载
            downerRequest.downerCallBack.onConnected(downerRequest);
        }
        Intent intent = new Intent(context, DownerService.class);
        intent.setAction("android.intent.action.RESPOND_VIA_MESSAGE");
        intent.putExtra(DOWN_REQUEST, downerRequest.options.getUrl());
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
        String url = intent.getStringExtra(DOWN_REQUEST);
        if(!downerRequests.containsKey(url)){
            return START_STICKY;
        }
        if(downerRequests.get(url).get() == null){
            return START_STICKY;
        }
        Log.i(Downer.TAG, "DownerService:  onStartCommand url :"+url);
        DownerRequest downerRequest = downerRequests.get(url).get();
        ThreadManger.getInstance().execute(Priority.NORMAL, downerRequest.scheduleRunable);
        Log.i(Downer.TAG, "DownerService:  onStartCommand  execute");
        return START_STICKY;
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
    private synchronized void iteratorSchedule(int scheduleStatus, String apkpagename){
        Iterator<Map.Entry<String, SoftReference<DownerRequest>>> entries = downerRequests.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, SoftReference<DownerRequest>> entry = entries.next();
            DownerRequest downerRequest = entry.getValue().get();
            ScheduleRunable scheduleRunable = downerRequest.scheduleRunable;
            switch (scheduleStatus){
                case SCHEDULE_STATUS_DISCONNECTED:
                    if(scheduleRunable.downerCallBack != null){
                        scheduleRunable.downerCallBack.onDisconnected(downerRequest.getModel());
                    }
                    Log.i(Downer.TAG, "DownerService:iteratorSchedule:Schedule is disconnected");
                    break;
                case SCHEDULE_STATUS_RESUME:
                    if(scheduleRunable.downerRequest != null){
                        if(downerRequest.status == Downer.STATUS_DOWNLOAD_STOP || downerRequest.status == Downer.STATUS_DOWNLOAD_PAUSE){
                            scheduleRunable.downerRequest.reStart(this);
                            Log.i(Downer.TAG, "DownerService:iteratorSchedule:Schedule is resume");
                        }
                    }
                    break;
                case SCHEDULE_STATUS_STOP:
                    if(scheduleRunable.downerRequest != null){
                        if(downerRequest.status == Downer.STATUS_DOWNLOAD_PROGRESS || downerRequest.status == Downer.STATUS_DOWNLOAD_PAUSE){
                            scheduleRunable.listener.downLoadStop();
                            Log.i(Downer.TAG, "DownerService:iteratorSchedule:Schedule is stop");
                        }
                    }
                    break;
                case Downer.STATUS_INSTALL_COMPLETE:
                    scheduleRunable.completeInstall(apkpagename);
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
            // WIFI已断开，移动数据已断开，执行中断操作
            iteratorSchedule(SCHEDULE_STATUS_STOP,"");
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
            Log.i(Downer.TAG, "onReceive：packageName " + packageName);
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
