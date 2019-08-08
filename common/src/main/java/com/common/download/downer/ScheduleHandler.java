package com.common.download.downer;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.common.download.Downer;
import com.common.download.DownerException;
import com.common.download.DownerUtil;
import com.common.download.InstallThread;
import com.common.download.model.DownerContrat;
import com.common.download.model.DownerOptions;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**通知栏更新，对外主线程通知派发类*/
public class ScheduleHandler {

    private Handler mHandler;
    private Context mContext;
    private ScheduleRunable schedule;
    /**下载请求信息类*/
    private DownerRequest downerRequest;
    /**下载请求状态返回，用来通知外部调用者，有可能为空，需要非空判断*/
    private DownerCallBack downerCallBack;
    /**下载参数类*/
    private DownerOptions downerOptions;
    /**下载进度通知栏*/
    private Notification.Builder builder;
    /**下载进度通知栏管理*/
    private NotificationManager notificationManager;
    /**下载数据库管理类*/
    /**并发时控制对外暂停派发*/
    private volatile AtomicBoolean isPause;
    /**并发时控制通知栏更新*/
    private volatile AtomicInteger notyStatus;
    /**并发时控制失败派发*/
    private volatile AtomicBoolean isError;
    /**文件长度*/
    private long fileLengeh;
    public ScheduleHandler(ScheduleRunable scheduleRunable){
        schedule = scheduleRunable;
        mContext = schedule.mContext;
        downerRequest = schedule.downerRequest;
        downerOptions = schedule.downerOptions;
        downerCallBack = schedule.downerCallBack;
        notyStatus = new AtomicInteger();
        isError = new AtomicBoolean(false);
        isPause = new AtomicBoolean(false);
        mHandler = new Handler(Looper.getMainLooper());
        initNotify();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initNotify() {
        if(!downerOptions.isNeedNotify()){
            return;
        }
        Log.i(Downer.TAG, "ScheduleHandler:  initNotify icon: "+ downerOptions.getIcon()+"  Title:"+ downerOptions.getTitle());
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(mContext, DownerService.NOTIFY_CHANNEL_ID)
//                    .setGroup(DownerService.NOTIFY_CHANNEL_ID)
//                    .setGroupSummary(false)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setLargeIcon(downerOptions.getIcon())
                    .setContentIntent(getDefalutIntent(PendingIntent.FLAG_UPDATE_CURRENT))
                    .setContentTitle(downerOptions.getTitle())
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setDefaults(Notification.FLAG_AUTO_CANCEL);
        } else {
            builder = new Notification.Builder(mContext)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setLargeIcon(downerOptions.getIcon())
                    .setContentIntent(getDefalutIntent(PendingIntent.FLAG_UPDATE_CURRENT))
                    .setContentTitle(downerOptions.getTitle())
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setDefaults(Notification.FLAG_AUTO_CANCEL);
        }
    }
    /**通知栏意图*/
    private PendingIntent getDefalutIntent(int flags) {
        Intent intent = new Intent(mContext, DownerService.class);
        intent.setAction("android.intent.action.RESPOND_VIA_MESSAGE");
        intent.putExtra(DownerService.DOWN_REQUEST, downerRequest.options.getUrl());
        return PendingIntent.getService(mContext, 0, intent, flags);
    }
    /**
     * 设置通知栏
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setNotify(String description) {
        if(!downerOptions.isNeedNotify()){
            return;
        }
        if (notyStatus.get() == Downer.STATUS_DOWNLOAD_START) {
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
        } else if (notyStatus.get() == Downer.STATUS_DOWNLOAD_PROGRESS) {
            int offset = (this != null)?schedule.offset:0;
            builder.setProgress(100, offset, false);
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
        } else if(notyStatus.get() == Downer.STATUS_DOWNLOAD_STOP){
            clearNotify();
            builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
            Log.i(Downer.TAG, "ScheduleHandler:setNotify  pause " + downerOptions.getTitle());
        }else{
            builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        }
        builder.setContentText(description);
        notificationManager.notify(downerRequest.NOTIFY_ID, builder.build());
    }
    /**
     * 清除通知栏
     */
    private void clearNotify() {
        if(!downerOptions.isNeedNotify()){
            return;
        }
        Log.i(Downer.TAG, "ScheduleHandler: clearNotify");
        notificationManager.cancel(downerRequest.NOTIFY_ID);
    }
    /**调度类监听，用来通知栏UI更新和下载状态变化*/
    public ScheduleRunable.ScheduleListener listener = new ScheduleRunable.ScheduleListener() {
        @Override
        public void downLoadStart() {
            /*通知外部调用者，开始下载*/
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    downerRequest.status = Downer.STATUS_DOWNLOAD_START;
                    isPause.getAndSet(false);
                    isError.getAndSet(false);
                    if(downerCallBack != null){
                        downerCallBack.onStart(downerRequest.getModel());
                    }
                    Log.i(Downer.TAG, "ScheduleHandler: downLoadStart");
                    notyStatus.getAndSet(Downer.STATUS_DOWNLOAD_START);
                    setNotify(DownerContrat.DownerString.DOWN_CONNECTING);
                }
            });
        }
        @Override
        public void downLoadProgress(long max, long progress) {
            if(progress >= max){
                return;
            }
            if(downerRequest.status == Downer.STATUS_DOWNLOAD_STOP){
                return;
            }
            if(downerRequest.status == Downer.STATUS_DOWNLOAD_PAUSE){
                return;
            }
            final long uiMax = max;
            final long uiProgress = progress;
            /*通知外部调用者，实时进度*/
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    downerRequest.status = Downer.STATUS_DOWNLOAD_PROGRESS;
                    if(downerCallBack != null){
                        downerCallBack.onProgress(uiMax, uiProgress);
                    }
                    notyStatus.getAndSet(Downer.STATUS_DOWNLOAD_PROGRESS);
                    setNotify(DownerUtil.formatByte(uiProgress) + "/" + DownerUtil.formatByte(uiMax));
                }
            });

        }
        @Override
        public void downLoadStop() {
            /*通知外部调用者，下载异常*/
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                        downerRequest.status = Downer.STATUS_DOWNLOAD_STOP;
                        if (!isError.get()){
                            isError.getAndSet(true);
                            if(downerCallBack != null){
                                downerCallBack.onStop(downerRequest.getModel(), new DownerException());
                            }
                        }
                        notyStatus.getAndSet(Downer.STATUS_DOWNLOAD_STOP);
                        if(DownerUtil.isNetworkConnected(mContext)){
                            //网络正常
                            setNotify(DownerContrat.DownerString.DONW_STOP);
                            Log.i(Downer.TAG, "ScheduleHandler: downLoadStop   2");
                        }else{
                            //无网络
                            setNotify(DownerContrat.DownerString.DONW_NET_STOP);
                            Log.i(Downer.TAG, "ScheduleHandler: downLoadStop   3");
                        }
                }
            }, 500);

        }
        @Override
        public void downLoadComplete() {
            schedule.pools--;
            Log.i(Downer.TAG, "ScheduleHandler: downLoadComplete："+schedule.pools);
            if(schedule.pools == 0){//分包全部下载完成
                /*通知外部调用者，完成下载*/
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        downerRequest.status = Downer.STATUS_DOWNLOAD_COMPLETE;
                        if(downerCallBack != null){
                            downerCallBack.onProgress(fileLengeh, fileLengeh);
                            downerCallBack.onComplete(downerRequest.getModel());
                        }
                        notyStatus.getAndSet(Downer.STATUS_DOWNLOAD_COMPLETE);
                        setNotify(DownerContrat.DownerString.DOWN_COMPLETE);
                        if(downerOptions.isAutomountEnabled()){//自动安装
                            Log.i(Downer.TAG, "ScheduleHandler:  downLoadComplete is Auto Install");
                            schedule.installThread = new InstallThread(schedule);
                            schedule.installThread.start();
                        }else{
                            downerRequest.release();
                        }
                        clearNotify();
                    }
                });
            }
        }

        @Override
        public void downLoadedInstall() {
            downerRequest.status = Downer.STATUS_DOWNLOAD_COMPLETE;
            if(downerOptions.isAutomountEnabled()){//自动安装
                Log.i(Downer.TAG, "ScheduleHandler:  downLoadComplete is Auto Install");
                schedule.installThread = new InstallThread(schedule);
                schedule.installThread.start();
            }else{
                downerRequest.release();
            }
            clearNotify();
        }

        @Override
        public void downLoadPause() {
            /*通知外部调用者，暂停成功*/
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!isPause.get()){
                        Log.i(Downer.TAG, "ScheduleHandler: downLoadPause offset = "+schedule.offset);
                        isPause.getAndSet(true);
                        if(downerCallBack != null){
                            downerCallBack.onPause(downerRequest.getModel());
                        }
                        notyStatus.getAndSet(Downer.STATUS_DOWNLOAD_PAUSE);
                        setNotify(DownerContrat.DownerString.DOWN_PAUSE);
                    }
                }
            }, new Random().nextInt(20));
        }
    };
}
