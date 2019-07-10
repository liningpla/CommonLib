package com.common.download.downer;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.common.download.Downer;
import com.common.download.DownerUtil;
import com.common.download.InstallThread;
import com.common.download.model.DownerBuffer;
import com.common.download.model.DownerOptions;
import com.common.download.model.DownerRepository;
import com.common.download.thread.Priority;
import com.common.download.thread.ThreadManger;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 调度线程类
 */
public class ScheduleRunable implements Runnable {

    public Context mContext;
    /**
     * 下载请求信息类
     */
    public DownerRequest downerRequest;
    /**
     * 下载参数类
     */
    public DownerOptions downerOptions;
    /**
     * 下载请求状态返回，用来通知外部调用者，有可能为空，需要非空判断
     */
    public DownerCallBack downerCallBack;
    /**
     * 下载文件总长度
     */
    public volatile long fileLength;
    /**
     * 下载最大进度
     */
    public volatile long maxProgress;
    /**
     * 下载进度
     */
    public volatile AtomicLong progress;
    /**
     * 下载偏移量
     */
    public volatile int offset;
    public DownerRepository repository;
    /**
     * 通知栏以及UI的对外调度器
     */
    public ScheduleHandler mHandler;
    /**
     * 该分包下载缓存
     */
    public volatile DownerBuffer downerBuffer;
    /**
     * 调度类监听，用来通知栏UI更新和下载状态变化
     */
    public ScheduleListener listener;
    /**
     * 分包下载数
     */
    public volatile int pools = 1;
    /**
     * 安装线程
     */
    public InstallThread installThread;
    /**
     * 并发时完整完成派发
     */
    private volatile AtomicBoolean isInstalled;

    public ScheduleRunable(Context context, DownerRequest downerRequest) {
        mContext = context;
        this.downerRequest = downerRequest;
        this.downerOptions = downerRequest.options;
        this.fileLength = downerOptions.getFilelength();
        this.downerCallBack = downerRequest.downerCallBack;
        if (repository == null) {
            repository = DownerRepository.getInstance(context);
        }
        this.mHandler = new ScheduleHandler(this);
        this.listener = mHandler.listener;
        isInstalled = new AtomicBoolean();
    }

    @Override
    public void run() {
        try {
            listener.downLoadStart();
            connectHttp(downerOptions.getUrl());
            long startLength = 0;
            long endLength = -1;
            File targetFile = downerOptions.getStorage();
            if (!downerOptions.isSupportRange() && targetFile.exists()) {//不支持断点续传
                targetFile.delete();
            }
            DownerBuffer upgradeBuffer = repository.getUpgradeBuffer(downerOptions.getUrl());
            //覆盖下载，删除文件，重新下载
            if (downerOptions.isOverride() && targetFile.exists()) {
                Log.i(Downer.TAG, "覆盖下载，删除文件，重新下载");
                targetFile.delete();
            }
            //下载实际长度和上次记录长度不同，删除文件，重新下载
            if(upgradeBuffer != null && fileLength != upgradeBuffer.getFileLength() && targetFile.exists()){
                Log.i(Downer.TAG, "下载实际长度和上次记录长度不同，删除文件，重新下载");
                targetFile.delete();
            }
            //下载完成，直接安装
            if(upgradeBuffer != null && fileLength == upgradeBuffer.getFileLength() && targetFile.exists()){
                Log.i(Downer.TAG, "下载完成，直接安装");
                listener.downLoadedInstall();
                return;
            }
            if (targetFile.exists() && upgradeBuffer != null) {
                if (upgradeBuffer.getBufferLength() <= targetFile.length()) {
                    if ((endLength = fileLength) != -1 && endLength == upgradeBuffer.getFileLength()) {
                        progress = new AtomicLong(upgradeBuffer.getBufferLength());
                        listener.downLoadProgress(maxProgress, progress.get());
                        Log.i(Downer.TAG, "progress :"+progress+"  maxProgress:"+maxProgress);
                        long expiryDate = Math.abs(System.currentTimeMillis() - upgradeBuffer.getLastModified());
                        if (expiryDate <= DownerBuffer.EXPIRY_DATE) {

                            List<DownerBuffer.BufferPart> bufferParts = upgradeBuffer.getBufferParts();
                            for (int id = 0; id < bufferParts.size(); id++) {
                                startLength = bufferParts.get(id).getStartLength();
                                endLength = bufferParts.get(id).getEndLength();
                                submit(this, id, startLength, endLength);
                            }
                            return;
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
                Log.i(Downer.TAG, "ScheduleRunable:parentFileExists:Schedule is stop");
                listener.downLoadStop();
                return;
            }
            if ((endLength = fileLength) == -1) {
                Log.i(Downer.TAG, "ScheduleRunable:(endLength = fileLength) == -1:Schedule is stop");
                listener.downLoadStop();
                return;
            }
            progress = new AtomicLong(startLength);
            maxProgress = endLength;
            if (!downerOptions.isMultithreadEnabled()) {
                submit(this, 0, startLength, endLength);
                return;
            }
            int part = 5 * 1024 * 1024;
            pools = 1;
            if (endLength >= part) {
                pools = (int) (endLength / part);
            }

            if (pools > downerOptions.getMultithreadPools()) {
                pools = downerOptions.getMultithreadPools();
                part = (int) (endLength / pools);
            }
            if (!downerOptions.isSupportRange()) {//不支持断点续传
                pools = 1;
                part = (int) (endLength / pools);
            }
            Log.i(Downer.TAG, "ScheduleRunable:  run pools = " + pools + "  part = " + part + " getMultithreadPools = " + downerOptions.getMultithreadPools()+" progress :"+progress);
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
            Log.i(Downer.TAG, "ScheduleRunable:run = " + e.getMessage());
            Log.i(Downer.TAG, "ScheduleRunable:Exception:Schedule is stop");
            listener.downLoadStop();
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
        if (!downerOptions.isMultithreadEnabled()) {
            ThreadManger.getInstance().execute(Priority.NORMAL, new ScheduleTask(scheduleThread, id));
        } else {
            ThreadManger.getInstance().execute(Priority.NORMAL, new ScheduleTask(scheduleThread, id, startLength, entLength));
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
            fileLength = getContentSize(readConnection);
            maxProgress = fileLength;
            downerOptions.setTrueUrl(tureUrl);
            Log.i(Downer.TAG, "ScheduleRunable:  connectHttp  fileLength:" + fileLength + " tureUrl:" + tureUrl);
        } catch (ProtocolException e) {
            e.printStackTrace();
            Log.i(Downer.TAG, "ScheduleRunable:  connectHttp  error1:" + downerOptions.getTitle());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i(Downer.TAG, "ScheduleRunable:  connectHttp  error2:" + downerOptions.getTitle());
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(Downer.TAG, "ScheduleRunable:  connectHttp  error3:" + downerOptions.getTitle());
        } finally {
            if (readConnection != null) {
                readConnection.disconnect();
            }
        }
    }

    private long getContentSize(HttpURLConnection conn) {
        long contentSize = 0;
        for (int i = 0; ; i++) {
            String mine = conn.getHeaderFieldKey(i);
            if (mine == null) {
                break;
            } else if (mine.equals("Content-Length")) {
                contentSize = Long.parseLong(conn.getHeaderField(i));
                break;
            }
        }
        return contentSize;
    }

    /**
     * 安装完成处理
     */
    public void completeInstall(String apkpagename) {
        if(downerRequest != null){
            downerRequest.release();
            String requesPageName = downerRequest.apkPageName;
            if (TextUtils.equals(requesPageName, apkpagename)) {
                if (downerOptions.isAutocleanEnabled() && downerOptions.getStorage().exists()) {
                    downerOptions.getStorage().delete();
                }
            }
            if (downerCallBack != null && DownerUtil.isAppInstalled(mContext, downerRequest.apkPageName) && !isInstalled.get()) {
                isInstalled.getAndSet(true);
                if (!isInstalled.get()) {
                    isInstalled.getAndSet(true);
                }
            }
        }
    }

    /**
     * 调度类监听，用来通知栏UI更新和下载状态变化
     */
    public interface ScheduleListener {
        void downLoadStart();

        void downLoadProgress(long max, long progress);

        void downLoadStop();

        void downLoadComplete();

        void downLoadedInstall();

        void downLoadPause();
    }

}