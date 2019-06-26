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
import com.common.upgrade.thread.Priority;
import com.common.upgrade.thread.ThreadManger;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**调度线程类*/
public class ScheduleRunable implements Runnable {

    public Context mContext;
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
    /**通知id，分配生成的三位数*/
    private int NOTIFY_ID;
    public Handler mHandler;
    /**该分包下载缓存*/
    private volatile DownlaodBuffer downlaodBuffer;
    /**并发时控制对外暂停派发*/
    private volatile AtomicBoolean isPause;
    /**并发时控制通知栏跟新*/
    private volatile AtomicInteger notyStatus;

    public ScheduleRunable(Context context, DownerRequest downerRequest){
        mContext = context;
        this.downerRequest = downerRequest;
        this.downlaodOptions = downerRequest.options;
        this.fileLength = downlaodOptions.getFilelength();
        this.downerCallBack = downerRequest.downerCallBack;
        this.mHandler = new Handler(context.getMainLooper());
        notyStatus = new AtomicInteger();
        NOTIFY_ID = (int) (Math.random()*900 + 100);
        if (repository == null) {
            repository = DownlaodRepository.getInstance(context);
        }
        initNotify();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initNotify() {
        Log.i(Downer.TAG, "ScheduleRunable:  initNotify icon: "+downlaodOptions.getIcon()+"  Title:"+downlaodOptions.getTitle());
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
    /**
     * 设置通知栏
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setNotify(String description) {
        if (notyStatus.get() == Downer.STATUS_DOWNLOAD_START) {
            clearNotify();
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
        } else if (notyStatus.get() == Downer.STATUS_DOWNLOAD_PROGRESS) {
            int offset = (this != null)?this.offset:0;
            builder.setProgress(100, offset, false);
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
            Log.i(Downer.TAG, "ScheduleRunable:setNotify  rogress " + downlaodOptions.getTitle());
        } else if(notyStatus.get() == Downer.STATUS_DOWNLOAD_PAUSE){
            clearNotify();
            builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
            Log.i(Downer.TAG, "ScheduleRunable:setNotify  pause " + downlaodOptions.getTitle());
        }else{
            clearNotify();
            builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        }
        builder.setContentText(description);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }
    /**通知栏意图*/
    private PendingIntent getDefalutIntent(int flags) {
        Log.i(Downer.TAG, "ScheduleRunable:  PendingIntent ");
        Intent intent = new Intent(mContext, DownerService.class);
        return PendingIntent.getService(mContext, 0, intent, flags);
    }

    /**
     * 清除通知栏
     */
    private void clearNotify() {
        notificationManager.cancel(NOTIFY_ID);
    }

    @Override
    public void run() {
        try {
            downerRequest.status = Downer.STATUS_DOWNLOAD_START;
            listener.downLoadStart();
            connectHttp(downlaodOptions.getUrl());
            long startLength = 0;
            long endLength = -1;
            File targetFile = downlaodOptions.getStorage();
            if(!downlaodOptions.isSupportRange() && targetFile.exists()){//不支持断点续传
                targetFile.delete();
            }
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
                                    downerRequest.status = Downer.STATUS_DOWNLOAD_COMPLETE;
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
                downerRequest.status = Downer.STATUS_DOWNLOAD_ERROR;
                listener.downLoadError();
                return;
            }
            if ((endLength = fileLength) == -1) {
                downerRequest.status = Downer.STATUS_DOWNLOAD_ERROR;
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
            Log.i(Downer.TAG, "ScheduleRunable:  run pools = "+pools+"  part = "+part+" getMultithreadPools = "+downlaodOptions.getMultithreadPools());
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
        if (!downlaodOptions.isMultithreadEnabled()) {
            ThreadManger.getInstance().execute(Priority.NORMAL,  new DownloadTask(scheduleThread, id));
        } else {
            ThreadManger.getInstance().execute(Priority.NORMAL,  new DownloadTask(scheduleThread, id, startLength, entLength));
        }
    }

    /**
     * 解析url，解析文件长度
     */
    private void connectHttp(String url) {
        String tureUrl = url;
        HttpURLConnection readConnection = null;
        try {
            readConnection = (HttpURLConnection) new URL(url).openConnection();
            readConnection.setRequestMethod("GET");
            readConnection.setDoInput(true);
            readConnection.setDoOutput(false);
            readConnection.setConnectTimeout(Downer.CONNECT_TIMEOUT);
            readConnection.setReadTimeout(Downer.READ_TIMEOUT);
            readConnection.connect();
            int statusCode = readConnection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                while ((statusCode == Downer.SC_MOVED_TEMPORARILY) || (statusCode == Downer.SC_MOVED_PERMANENTLY)) {
                    tureUrl = readConnection.getHeaderField(Downer.REDIRECT_LOCATION_KEY);
                    connectHttp(tureUrl);
                }
            }
            fileLength = readConnection.getContentLength();
            downlaodOptions.setTrueUrl(tureUrl);
            Log.i(Downer.TAG, "ScheduleRunable:  connectHttp  fileLength:"+fileLength+" tureUrl:"+tureUrl);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (readConnection != null) {
                readConnection.disconnect();
            }
        }
    }

    /**
     * 标记下载位置
     */
    public void mark(long startLength, long endLength) {
        if(!downlaodOptions.isSupportRange()){//不支持断点续传
            return;
        }
        if (downlaodBuffer == null) {
            downlaodBuffer = new DownlaodBuffer();
            downlaodBuffer.setDownloadUrl(downlaodOptions.getUrl());
            downlaodBuffer.setFileMd5(downlaodOptions.getMd5());
            downlaodBuffer.setBufferLength(progress.get());
            downlaodBuffer.setFileLength(maxProgress);
            downlaodBuffer.setBufferParts(new CopyOnWriteArrayList<DownlaodBuffer.BufferPart>());
            downlaodBuffer.setLastModified(System.currentTimeMillis());
        }
        downlaodBuffer.setBufferLength(progress.get());
        downlaodBuffer.setLastModified(System.currentTimeMillis());
        int index = -1;
        for (int i = 0; i < downlaodBuffer.getBufferParts().size(); i++) {
            if (downlaodBuffer.getBufferParts().get(i).getEndLength() == endLength) {
                index = i;
                break;
            }
        }
        DownlaodBuffer.BufferPart bufferPart = new DownlaodBuffer.BufferPart(startLength, endLength);
        if (index == -1) {
            downlaodBuffer.getBufferParts().add(bufferPart);
        } else {
            downlaodBuffer.getBufferParts().set(index, bufferPart);
        }
        repository.setUpgradeBuffer(downlaodBuffer);
    }

    /**调度类监听，用来通知栏UI更新和下载状态变化*/
    public ScheduleListener listener = new ScheduleListener() {
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
                    Log.i(Downer.TAG, "ScheduleRunable: downLoadStart");
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
            Log.i(Downer.TAG, "ScheduleRunable: downLoadError");
            /*通知外部调用者，下载异常*/
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(downerCallBack != null){
                        downerCallBack.onError(new DownlaodException());
                    }
                    notyStatus.getAndSet(Downer.STATUS_DOWNLOAD_ERROR);
                    setNotify(mContext.getString(R.string.message_download_error));
                    clearNotify();
                }
            });

        }
        @Override
        public void downLoadComplete() {
            Log.i(Downer.TAG, "ScheduleRunable: downLoadComplete");
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
                        Log.i(Downer.TAG, "ScheduleRunable:  downLoadComplete is Auto Install");
                        new InstallThread(ScheduleRunable.this).start();
                    }
                    clearNotify();
                }
            });
        }
        @Override
        public void downLoadCancel() {
            Log.i(Downer.TAG, "ScheduleRunable: downLoadCancel");
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
                        Log.i(Downer.TAG, "ScheduleRunable: downLoadPause offset = "+offset);
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