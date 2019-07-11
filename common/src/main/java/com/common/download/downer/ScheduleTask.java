package com.common.download.downer;

import android.util.Log;

import com.common.download.Downer;
import com.common.download.model.DownerBuffer;
import com.common.download.model.DownerOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;

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
            connection.setConnectTimeout(Downer.CONNECT_TIMEOUT);
            connection.setReadTimeout(Downer.READ_TIMEOUT);
            connection.setDoInput(true);
            connection.setDoOutput(false);
            File file = downerOptions.getStorage();

            if (endLength == 0) {
                connection.connect();
            } else {
                if(downerOptions.isSupportRange()){//不支持断点续传
                    connection.setRequestProperty("Range", "bytes=" + startLength + "-" + endLength);
                }
                connection.connect();
            }
            inputStream = connection.getInputStream();
            randomAccessFile = new RandomAccessFile(file, "rwd");
            randomAccessFile.seek(startLength);
            byte[] buffer = new byte[1024 * 1024];
            int len = -1;
            int tempOffset = 0;
            do {
                if (downerRequest.status == Downer.STATUS_DOWNLOAD_STOP) {
                    Log.d(Downer.TAG, "ScheduleTask run STATUS_DOWNLOAD_STOP");
                    break;
                }
                if (downerRequest.status == Downer.STATUS_DOWNLOAD_CANCEL) {
                    Log.d(Downer.TAG, "ScheduleTask run STATUS_DOWNLOAD_CANCEL");
                    break;
                }
                if (downerRequest.status == Downer.STATUS_DOWNLOAD_PAUSE) {
                    Log.d(Downer.TAG, "ScheduleTask run STATUS_DOWNLOAD_PAUSE");
                    break;
                }
               if( (len=inputStream.read(buffer)) != -1){
                   randomAccessFile.write(buffer, 0, len);
                   startLength += len;
                   scheduleRunable.progress.addAndGet(len);
                   tempOffset = (int) (((float) scheduleRunable.progress.get() / scheduleRunable.maxProgress) * 100);
                   Log.d(Downer.TAG, "tempOffset = "+tempOffset+"  scheduleRunable.offset = "+scheduleRunable.offset);
                   if (tempOffset > scheduleRunable.offset) {
                       scheduleRunable.offset = tempOffset;
                       listener.downLoadProgress(scheduleRunable.maxProgress, scheduleRunable.progress.get());
                        mark(startLength, endLength);
                   }
               }else{
                   /*如果 b 的长度为 0，则不读取任何字节并返回 0；否则，尝试读取至少一个字节。如果因为流位于文件末尾而没有可用的字节，则返回值 -1
                   * 因此分包下，读取中间大小时，读取完成总是startLength 比 endLength 大一，只有读取末尾时才正常startLength = endLength
                   * */
                   listener.downLoadComplete();
                   break;
               }
            } while (true);
        } catch (Exception e) {
            Log.i(Downer.TAG, "ScheduleTask:run = "+e.getMessage());
            Log.i(Downer.TAG, "ScheduleTask:Exception:Schedule is stop");
            listener.downLoadStop();
        } finally {
            Log.d(Downer.TAG, "ScheduleTask finally startLength = "+startLength+"  endLength = "+endLength);
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(Downer.TAG, "ScheduleTask:Exception:randomAccessFile = "+e.getMessage());
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(Downer.TAG, "ScheduleTask:Exception:inputStream = "+e.getMessage());
                }
            }
            if (connection != null) {
                connection.disconnect();
            }

        }
    }

    /**
     * 标记下载位置
     */
    public void mark(long startLength, long endLength) {
        try {
            if(!downerOptions.isSupportRange()){//不支持断点续传
                return;
            }
            if (scheduleRunable.downerBuffer == null) {
                scheduleRunable.downerBuffer = new DownerBuffer();
                scheduleRunable.downerBuffer.setDownloadUrl(downerOptions.getUrl());
                scheduleRunable.downerBuffer.setFileMd5(downerOptions.getMd5());
                scheduleRunable.downerBuffer.setBufferLength(scheduleRunable.progress.get());
                scheduleRunable.downerBuffer.setFileLength(scheduleRunable.maxProgress);
                scheduleRunable.downerBuffer.setBufferParts(new CopyOnWriteArrayList<DownerBuffer.BufferPart>());
                scheduleRunable.downerBuffer.setLastModified(System.currentTimeMillis());
            }
            scheduleRunable.downerBuffer.setBufferLength(scheduleRunable.progress.get());
            scheduleRunable.downerBuffer.setLastModified(System.currentTimeMillis());
            int index = -1;
            for (int i = 0; i < scheduleRunable.downerBuffer.getBufferParts().size(); i++) {
                if (scheduleRunable.downerBuffer.getBufferParts().get(i).getEndLength() == endLength) {
                    index = i;
                    break;
                }
            }
            DownerBuffer.BufferPart bufferPart = new DownerBuffer.BufferPart(startLength, endLength);
            if (index == -1) {
                scheduleRunable.downerBuffer.getBufferParts().add(bufferPart);
            } else {
                scheduleRunable.downerBuffer.getBufferParts().set(index, bufferPart);
            }
            scheduleRunable.repository.setUpgradeBuffer(scheduleRunable.downerBuffer);
        }catch (Exception e){
            e.printStackTrace();
            Log.i(Downer.TAG, "ScheduleRunable:mark = "+e.getMessage());
        }

    }


}