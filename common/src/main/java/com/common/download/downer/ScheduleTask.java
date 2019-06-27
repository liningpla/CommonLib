package com.common.download.downer;

import android.util.Log;

import com.common.download.Downer;
import com.common.download.model.DownerOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**分包线程类*/
public class ScheduleTask implements Runnable {
    /**分包任务id*/
    private int id;
    /**分包任务开始下载长度位置*/
    private long startLength;
    /**分包任务介绍下载长度位置*/
    private long endLength;
    /**现在参数类*/
    private DownerOptions downerOptions;
    /**外部监听类*/
    ScheduleRunable.ScheduleListener listener;
    /**此分包的调度类*/
    private ScheduleRunable scheduleRunable;
    /**下载请求信息类*/
    public DownerRequest downerRequest;

    public ScheduleTask(ScheduleRunable scheduleRunable, int id) {
        this(scheduleRunable, id, 0, 0);
    }

    public ScheduleTask(ScheduleRunable scheduleRunable, int id, long startLength, long endLength) {
        this.scheduleRunable = scheduleRunable;
        this.id = id;
        this.startLength = startLength;
        this.endLength = endLength;
        this.downerOptions = scheduleRunable.downerOptions;
        this.listener = scheduleRunable.listener;
        this.downerRequest = scheduleRunable.downerRequest;
        Log.d(Downer.TAG, "ScheduleTask initialized startLength = "+startLength+"  endLength = "+endLength);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run() {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            URL url = new URL(downerOptions.getTrueUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            File file = downerOptions.getStorage();

            if (endLength == 0) {
                connection.connect();
            } else {
                connection.setRequestProperty("Range", "bytes=" + startLength + "-" + endLength);
                connection.connect();
                if (connection.getResponseCode() != HttpURLConnection.HTTP_PARTIAL) {
                    listener.downLoadStop();
                    return;
                }
            }

            inputStream = connection.getInputStream();
            randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(startLength);
            byte[] buffer = new byte[1 * 1024 * 1024];
            int len = -1;
            int tempOffset = 0;
            do {
                if (downerRequest.status == Downer.STATUS_DOWNLOAD_STOP) {
                    break;
                }
                if (downerRequest.status == Downer.STATUS_DOWNLOAD_CANCEL) {
                    break;
                }
                if (downerRequest.status == Downer.STATUS_DOWNLOAD_COMPLETE) {
                    break;
                }
                if (downerRequest.status == Downer.STATUS_DOWNLOAD_PAUSE) {
                    break;
                }
               if( (len=inputStream.read(buffer)) != -1){
                   randomAccessFile.write(buffer, 0, len);
                   startLength += len;
                   scheduleRunable.progress.addAndGet(len);
                   tempOffset = (int) (((float) scheduleRunable.progress.get() / scheduleRunable.maxProgress) * 100);
                   if (tempOffset > scheduleRunable.offset) {
                       listener.downLoadProgress(scheduleRunable.maxProgress, scheduleRunable.progress.get());
                       scheduleRunable.mark(startLength, endLength);
                   }
               }else{
                   /*如果 b 的长度为 0，则不读取任何字节并返回 0；否则，尝试读取至少一个字节。如果因为流位于文件末尾而没有可用的字节，则返回值 -1
                   * 因此分包下，读取中间大小时，读取完成总是startLength 比 endLength 大一，只有读取末尾时才正常startLength = endLength
                   * */
                   Log.d(Downer.TAG, "ScheduleTask startLength = "+startLength+"  endLength = "+endLength);
                   if(startLength > endLength){
                       break;
                   }
                   listener.downLoadComplete();
                   break;
               }
            } while (true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(Downer.TAG, "ScheduleTask:run = "+e.getMessage());
            listener.downLoadStop();
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