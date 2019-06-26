package com.common.upgrade;

import android.content.Context;
import android.util.Log;

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
    public DownlaodRepository repository;
    /**通知栏以及UI的对外调度器*/
    public ScheduleHandler mHandler;
    /**该分包下载缓存*/
    private volatile DownlaodBuffer downlaodBuffer;
    /**调度类监听，用来通知栏UI更新和下载状态变化*/
    public ScheduleListener listener;

    public ScheduleRunable(Context context, DownerRequest downerRequest){
        mContext = context;
        this.downerRequest = downerRequest;
        this.downlaodOptions = downerRequest.options;
        this.fileLength = downlaodOptions.getFilelength();
        this.downerCallBack = downerRequest.downerCallBack;
        if (repository == null) {
            repository = DownlaodRepository.getInstance(context);
        }
        this.mHandler = new ScheduleHandler(this);
        this.listener = mHandler.listener;
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
            ThreadManger.getInstance().execute(Priority.NORMAL,  new ScheduleTask(scheduleThread, id));
        } else {
            ThreadManger.getInstance().execute(Priority.NORMAL,  new ScheduleTask(scheduleThread, id, startLength, entLength));
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
    public interface ScheduleListener{
        void downLoadStart();
        void downLoadProgress(long max, long progress);
        void downLoadError();
        void downLoadComplete();
        void downLoadCancel();
        void downLoadPause();
    }

}