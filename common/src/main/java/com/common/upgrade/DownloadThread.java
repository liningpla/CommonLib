package com.common.upgrade;

import android.util.Log;

import com.common.upgrade.model.DownlaodBuffer;
import com.common.upgrade.model.DownlaodOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CopyOnWriteArrayList;

public class DownloadThread extends Thread {
    private int id;
    private long startLength;
    private long endLength;
    private DownlaodOptions downlaodOptions;
    ScheduleThread.ScheduleListener listener;
    private ScheduleThread scheduleThread;
    /**
     * 升级缓存
     */
    private DownlaodBuffer downlaodBuffer;

    public DownloadThread(ScheduleThread scheduleThread, int id) {
        this(scheduleThread, id, 0, 0);
    }

    public DownloadThread(ScheduleThread scheduleThread, int id, long startLength, long endLength) {
        this.scheduleThread = scheduleThread;
        this.id = id;
        this.startLength = startLength;
        this.endLength = endLength;
        this.downlaodOptions = scheduleThread.downlaodOptions;
        this.listener = scheduleThread.listener;
        setName("DownloadThread-" + id);
        setPriority(Thread.NORM_PRIORITY);
        setDaemon(false);
        Log.d(DownlaodManager.TAG, "DownloadThread initialized");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void run() {
        super.run();
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            URL url = new URL(downlaodOptions.getUrl());
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
                if (DownlaodService.status == DownlaodService.STATUS_DOWNLOAD_CANCEL) {
                    listener.downLoadCancel();
                    break;
                }

                if (DownlaodService.status == DownlaodService.STATUS_DOWNLOAD_PAUSE) {
                    listener.downLoadPause();
                    break;
                }

                if ((len = inputStream.read(buffer)) == -1) {
                    if (scheduleThread.progress.get() < scheduleThread.maxProgress) {
                        break;
                    }

                    if (DownlaodService.status == DownlaodService.STATUS_DOWNLOAD_COMPLETE) {
                        break;
                    }
                    listener.downLoadComplete();
                    break;
                }

                if (DownlaodService.status == DownlaodService.STATUS_DOWNLOAD_START) {
                    DownlaodService.status = DownlaodService.STATUS_DOWNLOAD_PROGRESS;
                }

                randomAccessFile.write(buffer, 0, len);
                startLength += len;
                scheduleThread.progress.addAndGet(len);
                tempOffset = (int) (((float) scheduleThread.progress.get() / scheduleThread.maxProgress) * 100);
                if (tempOffset > scheduleThread.offset) {
                    scheduleThread.offset = tempOffset;
                    listener.downLoadProgress(scheduleThread.maxProgress, scheduleThread.progress.get());
                    mark();
                    Log.d(DownlaodManager.TAG, "Thread：" + getName()
                            + " Position：" + startLength + "-" + endLength
                            + " Download：" + scheduleThread.offset + "% " + scheduleThread.progress + "Byte/" + scheduleThread.maxProgress + "Byte");
                }
            } while (true);
        } catch (Exception e) {
            e.printStackTrace();
            DownlaodService.status = DownlaodService.STATUS_DOWNLOAD_ERROR;
            listener.downLoadError();
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

    /**
     * 标记下载位置
     */
    private void mark() {
        if (downlaodBuffer == null) {
            downlaodBuffer = new DownlaodBuffer();
            downlaodBuffer.setDownloadUrl(downlaodOptions.getUrl());
            downlaodBuffer.setFileMd5(downlaodOptions.getMd5());
            downlaodBuffer.setBufferLength(scheduleThread.progress.get());
            downlaodBuffer.setFileLength(scheduleThread.maxProgress);
            downlaodBuffer.setBufferParts(new CopyOnWriteArrayList<DownlaodBuffer.BufferPart>());
            downlaodBuffer.setLastModified(System.currentTimeMillis());
        }
        downlaodBuffer.setBufferLength(scheduleThread.progress.get());
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
        scheduleThread.repository.setUpgradeBuffer(downlaodBuffer);
    }
}