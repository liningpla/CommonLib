package com.common.upgrade;

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

import com.captureinfo.R;
import com.common.upgrade.model.DownlaodOptions;

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
    private DownlaodOptions downlaodOptions;
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
    private volatile int reTray = 3;
    /**通知id，分配生成的三位数*/
    public int NOTIFY_ID;
    public ScheduleHandler(ScheduleRunable scheduleRunable){
        schedule = scheduleRunable;
        mContext = schedule.mContext;
        downerRequest = schedule.downerRequest;
        downlaodOptions = schedule.downlaodOptions;
        downerCallBack = schedule.downerCallBack;
        notyStatus = new AtomicInteger();
        isError = new AtomicBoolean(false);
        isPause = new AtomicBoolean(false);
        NOTIFY_ID = (int) (Math.random()*900 + 100);
        mHandler = new Handler(Looper.getMainLooper());
        initNotify();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initNotify() {
        Log.i(Downer.TAG, "ScheduleHandler:  initNotify icon: "+downlaodOptions.getIcon()+"  Title:"+downlaodOptions.getTitle());
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(mContext, DownerService.NOTIFY_CHANNEL_ID)
                    .setGroup(DownerService.NOTIFY_CHANNEL_ID)
                    .setGroupSummary(false)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setLargeIcon(downlaodOptions.getIcon())
                    .setContentIntent(getDefalutIntent(PendingIntent.FLAG_UPDATE_CURRENT))
                    .setContentTitle(downlaodOptions.getTitle())
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setDefaults(Notification.FLAG_AUTO_CANCEL);
        } else {
            builder = new Notification.Builder(mContext)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setLargeIcon(downlaodOptions.getIcon())
                    .setContentIntent(getDefalutIntent(PendingIntent.FLAG_UPDATE_CURRENT))
                    .setContentTitle(downlaodOptions.getTitle())
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setDefaults(Notification.FLAG_AUTO_CANCEL);
        }
    }
    /**通知栏意图*/
    private PendingIntent getDefalutIntent(int flags) {
        Log.i(Downer.TAG, "ScheduleHandler:  PendingIntent ");
        Intent intent = new Intent(mContext, DownerService.class);
        return PendingIntent.getService(mContext, 0, intent, flags);
    }
    /**
     * 设置通知栏
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setNotify(String description) {
        if (notyStatus.get() == Downer.STATUS_DOWNLOAD_START) {
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
        } else if (notyStatus.get() == Downer.STATUS_DOWNLOAD_PROGRESS) {
            int offset = (this != null)?schedule.offset:0;
            builder.setProgress(100, offset, false);
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
        } else if(notyStatus.get() == Downer.STATUS_DOWNLOAD_PAUSE){
            clearNotify();
            builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
            Log.i(Downer.TAG, "ScheduleHandler:setNotify  pause " + downlaodOptions.getTitle());
        }else{
            clearNotify();
            builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        }
        builder.setContentText(description);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }
    /**
     * 清除通知栏
     */
    private void clearNotify() {
        Log.i(Downer.TAG, "ScheduleHandler: clearNotify");
        notificationManager.cancel(NOTIFY_ID);
    }
    /**调度类监听，用来通知栏UI更新和下载状态变化*/
    public ScheduleRunable.ScheduleListener listener = new ScheduleRunable.ScheduleListener() {
        @Override
        public void downLoadStart() {
            /*通知外部调用者，开始下载*/
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    isPause = new AtomicBoolean(false);
                    if(downerCallBack != null){
                        downerCallBack.onStart();
                    }
                    Log.i(Downer.TAG, "ScheduleHandler: downLoadStart");
                    notyStatus.getAndSet(Downer.STATUS_DOWNLOAD_START);
                    setNotify(mContext.getString(R.string.message_download_start));
                }
            });
        }
        @Override
        public void downLoadProgress(final long max, final long progress) {
            /*通知外部调用者，实时进度*/
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(downerCallBack != null){
                        downerCallBack.onProgress(max, progress);
                    }
                    if(max == progress){//下载完成，不再显示通知。
                        notyStatus.getAndSet(Downer.STATUS_DOWNLOAD_COMPLETE);
                        clearNotify();
                        return;
                    }
                    notyStatus.getAndSet(Downer.STATUS_DOWNLOAD_PROGRESS);
                    setNotify(DownlaodUtil.formatByte(progress) + "/" +DownlaodUtil.formatByte(max));
                }
            });

        }
        @Override
        public void downLoadError() {
            /*通知外部调用者，下载异常*/
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(reTray < 0){
                        if (!isError.get()){
                            Log.i(Downer.TAG, "ScheduleHandler: downLoadError");
                            isError.getAndSet(true);
                            downerRequest.release();
                            if(downerCallBack != null){
                                downerCallBack.onError(new DownlaodException());
                            }
                            notyStatus.getAndSet(Downer.STATUS_DOWNLOAD_ERROR);
                            setNotify(mContext.getString(R.string.message_download_error));
                            clearNotify();
                        }
                    }else{
                        reTray --;
//                        downerRequest.reStart(mContext);
                    }

                }
            });

        }
        @Override
        public void downLoadComplete() {
            Log.i(Downer.TAG, "ScheduleHandler: downLoadComplete");
            downerRequest.release();
            /*通知外部调用者，完成下载*/
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(downerCallBack != null){
                        downerCallBack.onComplete();
                    }
                    notyStatus.getAndSet(Downer.STATUS_DOWNLOAD_COMPLETE);
                    setNotify(mContext.getString(R.string.message_download_complete));
                    if(downlaodOptions.isAutomountEnabled()){//自动安装
                        Log.i(Downer.TAG, "ScheduleHandler:  downLoadComplete is Auto Install");
                        new InstallThread(schedule).start();
                    }
                    clearNotify();
                }
            });
        }
        @Override
        public void downLoadCancel() {
            Log.i(Downer.TAG, "ScheduleHandler: downLoadCancel");
            downerRequest.release();
            /*通知外部调用者，取消成功*/
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(downerCallBack != null){
                        downerCallBack.onCancel();
                    }
                    clearNotify();
                }
            });
        }
        @Override
        public void downLoadPause() {
            /*通知外部调用者，暂停成功*/
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(!isPause.get()){
                        Log.i(Downer.TAG, "ScheduleHandler: downLoadPause offset = "+schedule.offset);
                        isPause.getAndSet(true);
                        if(downerCallBack != null){
                            downerCallBack.onPause();
                        }
                        notyStatus.getAndSet(Downer.STATUS_DOWNLOAD_PAUSE);
                        setNotify(mContext.getString(R.string.message_download_pause));
                    }
                }
            });
        }
    };
}
