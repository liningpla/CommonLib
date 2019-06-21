package com.common.upgrade;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.captureinfo.R;
import com.common.upgrade.model.DownlaodBuffer;
import com.common.upgrade.model.DownlaodOptions;
import com.common.upgrade.model.DownlaodRepository;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.common.upgrade.DownerService.scheduleRunables;

/**调度线程类*/
public class ScheduleRunable implements Runnable {

    private Context mContext;
    /**下载请求信息类*/
    public DownerRequest downerRequest;
    /**下载参数类*/
    public DownlaodOptions downlaodOptions;
    /**下载请求状态返回，用来通知外部调用者，有可能为空，需要非空判断*/
    public DownerCallBack downerCallBack;
    /**下载文件总长度*/
    public volatile long fileLength;
    /**下载最大进度*/
    public volatile long maxProgress;
    /**下载进度*/
    public volatile AtomicLong progress;
    /**下载偏移量*/
    public volatile int offset;
    /**下载进度通知栏*/
    private Notification.Builder builder;
    /**下载进度通知栏管理*/
    private NotificationManager notificationManager;
    /**下载数据库管理类*/
    public DownlaodRepository repository;

    public Handler mHandler;

    /**调度类监听，用来通知栏UI更新和下载状态变化*/
    public ScheduleListener listener = new ScheduleListener() {
        @Override
        public void downLoadStart() {
            /*通知外部调用者，开始下载*/
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(downerCallBack != null){
                        downerCallBack.onStart();
                    }
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
                    if(downerCallBack != null){
                        downerCallBack.onError(new DownlaodException());
                    }
                    setNotify(mContext.getString(R.string.message_download_error));
                }
            });

        }
        @Override
        public void downLoadComplete() {
            /*通知外部调用者，完成下载*/
            scheduleRunables.remove(downerRequest.options.getTrueUrl());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(downerCallBack != null){
                        downerCallBack.onComplete();
                    }
                    setNotify(mContext.getString(R.string.message_download_complete));
                    if(downlaodOptions.isAutomountEnabled()){//自动安装

                    }
                }
            });
        }
        @Override
        public void downLoadCancel() {
            /*通知外部调用者，取消成功*/
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(downerCallBack != null){
                        downerCallBack.onCancel();
                    }

                }
            });
        }
        @Override
        public void downLoadPause() {
            /*通知外部调用者，暂停成功*/
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(downerCallBack != null){
                        downerCallBack.onPause();
                    }
                    setNotify(mContext.getString(R.string.message_download_pause));
                }
            });

        }
    };

    public ScheduleRunable(Context context, DownerRequest downerRequest){
        mContext = context;
        this.downerRequest = downerRequest;
        this.downlaodOptions = downerRequest.options;
        this.fileLength = downlaodOptions.getFilelength();
        this.downerCallBack = downerRequest.downerCallBack;
        this.mHandler = new Handler(context.getMainLooper());
        if (repository == null) {
            repository = DownlaodRepository.getInstance(context);
        }
        initNotify();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initNotify() {
        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(mContext, String.valueOf(DownerService.NOTIFY_ID))
                    .setGroup(String.valueOf(DownerService.NOTIFY_ID))
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
    /**
     * 设置通知栏
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setNotify(String description) {
        if (downerRequest.status == Downer.STATUS_DOWNLOAD_START) {
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
        } else if (downerRequest.status == Downer.STATUS_DOWNLOAD_PROGRESS) {
            int offset = (this != null)?this.offset:0;
            builder.setProgress(100, offset, false);
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
        } else {
            builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        }
        builder.setContentText(description);
        notificationManager.notify(DownerService.NOTIFY_ID, builder.build());
    }
    /**通知栏意图*/
    private PendingIntent getDefalutIntent(int flags) {
        Intent intent = new Intent(mContext, DownerService.class);
        return PendingIntent.getService(mContext, 0, intent, flags);
    }

    @Override
    public void run() {
        try {
            Log.i(Downer.TAG, "ScheduleRunable:  run ");
            Thread.sleep(DownlaodService.DELAY);
            listener.downLoadStart();
            long startLength = 0;
            long endLength = -1;
            File targetFile = downlaodOptions.getStorage();
            if (targetFile.exists()) {
                DownlaodBuffer upgradeBuffer = repository.getUpgradeBuffer(downlaodOptions.getUrl());
                if (upgradeBuffer != null) {
                    if (upgradeBuffer.getBufferLength() <= targetFile.length()) {
                        if ((endLength = fileLength) != -1 && endLength == upgradeBuffer.getFileLength()) {
                            progress = new AtomicLong(upgradeBuffer.getBufferLength());
                            maxProgress = upgradeBuffer.getFileLength();
                            long expiryDate = Math.abs(System.currentTimeMillis() - upgradeBuffer.getLastModified());
                            if (expiryDate <= DownlaodBuffer.EXPIRY_DATE) {
                                if (upgradeBuffer.getBufferLength() == upgradeBuffer.getFileLength()) {
                                    listener.downLoadProgress(maxProgress, progress.get());
                                    listener.downLoadComplete();
                                    return;
                                }
                                List<DownlaodBuffer.BufferPart> bufferParts = upgradeBuffer.getBufferParts();
                                for (int id = 0; id < bufferParts.size(); id++) {
                                    startLength = bufferParts.get(id).getStartLength();
                                    endLength = bufferParts.get(id).getEndLength();
                                    submit(this, id, startLength, endLength);
                                }
                                return;
                            }
                        }
                    }
                }
                targetFile.delete();
            }
            boolean parentFileExists = true;
            File parentFile = targetFile.getParentFile();
            if (!parentFile.exists()) {
                parentFileExists = parentFile.mkdirs();
            }
            if (!parentFileExists) {
                listener.downLoadError();
                return;
            }
            if ((endLength = fileLength) == -1) {
                listener.downLoadError();
                return;
            }
            progress = new AtomicLong(startLength);
            maxProgress = endLength;
            if (!downlaodOptions.isMultithreadEnabled()) {
                submit(this, 0, startLength, endLength);
                return;
            }
            int part = 5 * 1024 * 1024;
            int pools = 1;
            if (endLength >= part) {
                pools = (int) (endLength / part);
            }
            if (pools > downlaodOptions.getMultithreadPools()) {
                pools = downlaodOptions.getMultithreadPools();
                part = (int) (endLength / pools);
            }
            long tempStartLength = 0;
            long tempEndLength = 0;
            for (int id = 1; id <= pools; id++) {
                tempStartLength = (id - 1) * part;
                tempEndLength = tempStartLength + part - 1;
                if (id == pools) {
                    tempEndLength = endLength;
                }
                submit(this, id - 1, tempStartLength, tempEndLength);
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.downLoadError();
        }
    }
    /**
     * 提交下载任务
     *
     * @param id          线程ID
     * @param startLength 开始下载位置
     * @param entLength   结束下载位置
     */
    private void submit(ScheduleRunable scheduleThread, int id, long startLength, long entLength) {
        Thread thread = null;
        if (!downlaodOptions.isMultithreadEnabled()) {
            thread = new DownloadTask(scheduleThread, id);
        } else {
            thread = new DownloadTask(scheduleThread, id, startLength, entLength);
        }
        thread.start();

    }

    /**调度类监听，用来通知栏UI更新和下载状态变化*/
    public interface ScheduleListener{
        void downLoadStart();
        void downLoadProgress(long max, long progress);
        void downLoadError();
        void downLoadComplete();
        void downLoadCancel();
        void downLoadPause();
    }
}