package com.common.upgrade;

import android.util.Log;

import com.common.upgrade.model.DownlaodOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**分包线程类*/
public class DownloadTask extends Thread {
    /**分包任务id*/
    private int id;
    /**分包任务开始下载长度位置*/
    private long startLength;
    /**分包任务介绍下载长度位置*/
    private long endLength;
    /**现在参数类*/
    private DownlaodOptions downlaodOptions;
    /**外部监听类*/
    ScheduleRunable.ScheduleListener listener;
    /**此分包的调度类*/
    private ScheduleRunable scheduleRunable;
    /**下载请求信息类*/
    public DownerRequest downerRequest;

    public DownloadTask(ScheduleRunable scheduleRunable, int id) {
        this(scheduleRunable, id, 0, 0);
    }

    public DownloadTask(ScheduleRunable scheduleRunable, int id, long startLength, long endLength) {
        this.scheduleRunable = scheduleRunable;
        this.id = id;
        this.startLength = startLength;
        this.endLength = endLength;
        this.downlaodOptions = scheduleRunable.downlaodOptions;
        this.listener = scheduleRunable.listener;
        this.downerRequest = scheduleRunable.downerRequest;
        setName("DownloadTask-" + id);
        setPriority(Thread.NORM_PRIORITY);
        setDaemon(false);
        Log.d(Downer.TAG, "DownloadTask initialized startLength = "+startLength+"  endLength = "+endLength);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run() {
        super.run();
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            URL url = new URL(downlaodOptions.getTrueUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            File file = downlaodOptions.getStorage();

            if (endLength == 0) {
                connection.connect();
            } else {
                connection.setRequestProperty("Range", "bytes=" + startLength + "-" + endLength);
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_PARTIAL) {
                    downerRequest.status = Downer.STATUS_DOWNLOAD_ERROR;
                    listener.downLoadError();
                    return;
                }
            }

            inputStream = connection.getInputStream();
            randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(startLength);
            byte[] buffer = new byte[1024];
            int len = -1;
            int tempOffset = 0;
            do {
                /*收到取消通知，执行取消操作，通知调度器*/
                if (downerRequest.status == Downer.STATUS_DOWNLOAD_CANCEL) {
                    listener.downLoadCancel();
                    break;
                }
                if ((len = inputStream.read(buffer)) == -1) {//下载完成
                    if (scheduleRunable.progress.get() < scheduleRunable.maxProgress) {
                        break;
                    }

                    if (downerRequest.status == Downer.STATUS_DOWNLOAD_COMPLETE) {
                        break;
                    }
                    Log.d(Downer.TAG, "DownloadTask-" + id+":run:Position：" + startLength + "-" + endLength+" title:"+downlaodOptions.getTitle()+"-"+scheduleRunable.progress + "Byte/" + scheduleRunable.maxProgress + "Byte");
                    downerRequest.status = Downer.STATUS_DOWNLOAD_COMPLETE;
                    listener.downLoadComplete();
                    break;
                }

                if (downerRequest.status == Downer.STATUS_DOWNLOAD_START) {
                    downerRequest.status = Downer.STATUS_DOWNLOAD_PROGRESS;
                }
                /*收到暂停通知，执行暂停操作，通知调度器*/
                if (downerRequest.status != Downer.STATUS_DOWNLOAD_PAUSE) {
                    randomAccessFile.write(buffer, 0, len);
                    startLength += len;
                    scheduleRunable.mark(startLength, endLength);
                    scheduleRunable.progress.addAndGet(len);
                    tempOffset = (int) (((float) scheduleRunable.progress.get() / scheduleRunable.maxProgress) * 100);
                    if (tempOffset > scheduleRunable.offset) {
                        scheduleRunable.offset = tempOffset;
                        listener.downLoadProgress(scheduleRunable.maxProgress, scheduleRunable.progress.get());
//                    Log.d(Downer.TAG, "Thread：" + getName()
//                            + " Position：" + startLength + "-" + endLength
//                            + " Download：" + scheduleRunable.offset + "% " + scheduleRunable.progress + "Byte/" + scheduleRunable.maxProgress + "Byte");
                    }
                }else{
                    listener.downLoadPause();
                }
            } while (true);
        } catch (Exception e) {
            e.printStackTrace();
            if(downerRequest.status != Downer.STATUS_DOWNLOAD_PAUSE){
                downerRequest.status = Downer.STATUS_DOWNLOAD_ERROR;
                listener.downLoadError();
            }
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                connection.disconnect();
            }
        }
    }


}