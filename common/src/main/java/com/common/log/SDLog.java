package com.common.log;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SDLog {

    public static final boolean SDCARD_LOG = true;//true;
    private static final String PATH = "/sdcard/capture/logs/";
    private static final int MAX_FILE_COUNT = 10;
    private ExecutorService threadPool;

    private static SDLog instance;

    public void init() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                clearLog();
            }
        });

    }

    public static SDLog create() {
        if (instance == null) {
            instance = new SDLog();
        }

        return instance;
    }

    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    SimpleDateFormat fmt1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
    String fileName;

    private SDLog() {
        threadPool = Executors.newSingleThreadExecutor();
        fileName = fmt1.format(System.currentTimeMillis());
    }

    private void clearLog() {
        File dir = new File(PATH);
        String[] files = dir.list();
        if (files == null) {
            return;
        }

        int size = files.length;
        while (size >= MAX_FILE_COUNT) {
            for (int i = 0; i < size - 1; i++) {
                try {
                    Date d1 = fmt1.parse(files[i]);
                    Date d2 = fmt1.parse(files[i + 1]);
                    if (d1.before(d2)) {
                        String temp = files[i];
                        files[i] = files[i + 1];
                        files[i + 1] = temp;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            (new File(PATH + files[size - 1])).delete();
            size--;
        }
    }

    /**@param object 类名
     * @param keyword 日志类型
     * @param message 日志类型
     * */
    public void e(Object object, String keyword, String message) {
        String tag = object.getClass().getSimpleName();
        if(SDCARD_LOG){
            Log.e(tag, keyword + "::" + message);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[" + fmt.format(System.currentTimeMillis()) + "]:");
        sb.append(tag);
        sb.append("::");
        sb.append(keyword);
        sb.append("::");
        sb.append(message);
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = new File(PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(PATH + fileName, true);
                OutputStreamWriter sw = new OutputStreamWriter(fos);
                sw.append("\n");
                sw.append(sb.toString());
                sw.flush();
                fos.close();
            }
        } catch (Exception e) {

        }
    }

    /**@param object 类名
     * @param keyword 日志类型
     * @param message 日志类型
     * */
    public void i(Object object, String keyword, String message) {
        String tag = object.getClass().getSimpleName();
        if(SDCARD_LOG){
            Log.i(tag, keyword + "::" + message);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[" + fmt.format(System.currentTimeMillis()) + "]:");
        sb.append(tag);
        sb.append("::");
        sb.append(keyword);
        sb.append("::");
        sb.append(message);
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = new File(PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(PATH + fileName, true);
                OutputStreamWriter sw = new OutputStreamWriter(fos);
                sw.append("\n");
                sw.append(sb.toString());
                sw.flush();
                fos.close();
            }
        } catch (Exception e) {

        }
    }
}
