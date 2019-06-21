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
import android.util.Log;

import com.common.threadPool.Priority;
import com.common.threadPool.ThreadManger;

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
    public static final int NOTIFY_ID = 0x6710;
    /**传给service，添加下载*/
    public static void startDownerService(Context context, DownerRequest downerRequest){
        if(downerRequest.downerCallBack != null){//链接服务下载
            downerRequest.downerCallBack.onConnected();
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
        Log.i(Downer.TAG, "DownerService:  onStartCommand ");
        DownerRequest downerRequest = intent.getParcelableExtra(DOWN_REQUEST);
        String url = downerRequest.options.getUrl();
        Log.i(Downer.TAG, "DownerService:  onStartCommand url :"+url);
        if(!scheduleRunables.containsKey(url)){
            ScheduleRunable scheduleRunable = new ScheduleRunable(this, downerRequest);
            SoftReference<ScheduleRunable> softSchedule = new SoftReference<>(scheduleRunable);
            scheduleRunables.put(url, softSchedule);
            ThreadManger.getInstance().execute(Priority.NORMAL, scheduleRunable);
            Log.i(Downer.TAG, "DownerService:  onStartCommand  execute");
        }
        return START_STICKY;
    }

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
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initNotify() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(String.valueOf(NOTIFY_ID),
                    getApplication().getApplicationInfo().packageName, NotificationManager.IMPORTANCE_DEFAULT);
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
        iteratorSchedule(SCHEDULE_STATUS_DISCONNECTED);
    }

    /**遍历下载调度任务*/
    private void iteratorSchedule(int scheduleStatus){
        Iterator<Map.Entry<String, SoftReference<ScheduleRunable>>> entries = scheduleRunables.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, SoftReference<ScheduleRunable>> entry = entries.next();
            ScheduleRunable scheduleRunable = entry.getValue().get();
            switch (scheduleStatus){
                case SCHEDULE_STATUS_DISCONNECTED:
                    if(scheduleRunable.downerCallBack != null){
                        scheduleRunable.downerCallBack.onDisconnected();
                    }
                    Log.i(Downer.TAG, "Schedule is disconnected");
                    break;
                case SCHEDULE_STATUS_RESUME:
                    if(scheduleRunable.downerRequest != null){
                        scheduleRunable.downerRequest.resume(DownerService.this);
                    }
                    Log.i(Downer.TAG, "Schedule is resume");
                    break;
                case SCHEDULE_STATUS_PAUSE:
                    if(scheduleRunable.downerRequest != null){
                        scheduleRunable.downerRequest.pause();
                    }
                    Log.i(Downer.TAG, "Schedule is pause");
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
                iteratorSchedule(SCHEDULE_STATUS_RESUME);
                return;
            }
            // WIFI已连接，移动数据已断开
            if (wifiNetworkInfo.isConnected() && !mobileNetworkInfo.isConnected()) {
                //如果时暂停状态，恢复网络重新下载
                iteratorSchedule(SCHEDULE_STATUS_RESUME);
                return;
            }
            // WIFI已断开，移动数据已连接
            if (!wifiNetworkInfo.isConnected() && mobileNetworkInfo.isConnected()) {
                //如果时暂停状态，恢复网络重新下载
                iteratorSchedule(SCHEDULE_STATUS_RESUME);
                return;
            }
            // WIFI已断开，移动数据已断开，执行暂停操作
            iteratorSchedule(SCHEDULE_STATUS_PAUSE);
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
            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {//开始安装
                Log.i(DownlaodManager.TAG, "onReceive：Added " + packageName);
            } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {//安装完成
                Log.i(DownlaodManager.TAG, "onReceive：Replaced " + packageName);
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {//卸载
                Log.i(DownlaodManager.TAG, "onReceive：Removed " + packageName);
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
