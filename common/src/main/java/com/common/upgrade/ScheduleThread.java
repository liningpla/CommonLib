package com.common.upgrade;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.common.upgrade.model.DownlaodBuffer;
import com.common.upgrade.model.DownlaodOptions;
import com.common.upgrade.model.DownlaodRepository;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**调度线程类*/
public class ScheduleThread extends Thread {

    public ScheduleListener listener;
    /**
     * 下载最大进度
     */
    public volatile long maxProgress;
    /**
     * 下载进度
     */
    public volatile AtomicLong progress;
    /**
     * 升级选项
     */
    public DownlaodOptions downlaodOptions;

    /**
     * 下载偏移量
     */
    public volatile int offset;
    /**
     * 状态
     */
    public static volatile int status;
    /**
     * 升级进度通知栏
     */
    private Notification.Builder builder;

    /**
     * 升级进度通知栏管理
     */
    private NotificationManager notificationManager;

    private Context mContext;

    public DownlaodRepository repository;
    public ScheduleThread(Context context, DownlaodOptions downlaodOptions, ScheduleListener listener){
        mContext = context;
        this.listener = listener;
        this.downlaodOptions = downlaodOptions;
        if (repository == null) {
            repository = DownlaodRepository.getInstance(context);
        }
    }
    @Override
    public void run() {
        super.run();
        try {
            Thread.sleep(DownlaodService.DELAY);
            listener.downLoadStart();
            long startLength = 0;
            long endLength = -1;
            File targetFile = downlaodOptions.getStorage();
            if (targetFile.exists()) {
                DownlaodBuffer upgradeBuffer = repository.getUpgradeBuffer(downlaodOptions.getUrl());
                if (upgradeBuffer != null) {
                    if (upgradeBuffer.getBufferLength() <= targetFile.length()) {
                        if ((endLength = length(downlaodOptions.getUrl())) != -1 &&
                                endLength == upgradeBuffer.getFileLength()) {
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
            if ((endLength = length(downlaodOptions.getUrl())) == -1) {
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
    private void submit(ScheduleThread scheduleThread, int id, long startLength, long entLength) {
        Thread thread = null;
        if (!downlaodOptions.isMultithreadEnabled()) {
            thread = new DownloadThread(scheduleThread, id);
        } else {
            thread = new DownloadThread(scheduleThread, id, startLength, entLength);
        }
        thread.start();

    }
    /**
     * 下载文件长度
     *
     * @param url 下载文件地址
     * @return
     */
    private long length(String url) throws IOException {
        HttpURLConnection readConnection = null;
        try {
            readConnection = (HttpURLConnection) new URL(url).openConnection();
            readConnection.setRequestMethod("GET");
            readConnection.setDoInput(true);
            readConnection.setDoOutput(false);
            readConnection.setConnectTimeout(DownlaodService.CONNECT_TIMEOUT);
            readConnection.setReadTimeout(DownlaodService.READ_TIMEOUT);
            readConnection.connect();
            if (readConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                return readConnection.getContentLength();
            }
        } finally {
            if (readConnection != null) {
                readConnection.disconnect();
            }
        }
        return -1;
    }
    public interface ScheduleListener{
        void downLoadStart();
        void downLoadProgress(long max, long progress);
        void downLoadError();
        void downLoadComplete();
        void downLoadCancel();
        void downLoadPause();
    }
}