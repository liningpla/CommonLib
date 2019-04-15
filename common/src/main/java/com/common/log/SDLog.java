package com.common.log;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SDLog {

    public static final boolean SDCARD_LOG = true;//true;
    private static final String PATH = "/sdcard/capture/logs/";
    private static final int MAX_FILE_COUNT = 10;
    private static String PACKAGE = "com.example.notificationtest";

    static SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    static SimpleDateFormat fmt1 = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
    static String fileName = fmt1.format(System.currentTimeMillis());
    static String TAG = "SDLog";

    public static void initLog(String packageName){
        PACKAGE = packageName;
    }

    public static void clearLog() {
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

    /**
     * @param tag 日志类型
     * @param message 日志类型
     * */
    public static void e(String tag, String message) {
        if(SDCARD_LOG){
            message = getMessage(tag, message);
            Log.e(tag, message);
            writeToFile(message);
        }

    }

    /**@param tag 日志tag
     * @param message 日志类型
     * */
    public static void i(String tag, String message) {
        if(SDCARD_LOG){
            message = getMessage(tag, message);
            Log.i(tag, message);
            writeToFile(message);
        }
    }

    /**@param tag 日志tag
     * @param message 日志类型
     * */
    public static void d(String tag, String message) {
        if(SDCARD_LOG){
            message = getMessage(tag, message);
            Log.d(tag, message);
            writeToFile(message);
        }
    }


    /**
     * @param message 日志类型
     * */
    public static void e(String message) {
        if(SDCARD_LOG){
            message = getMessage(TAG, message);
            Log.e(TAG, message);
            writeToFile(message);
        }

    }

    /**
     * @param message 日志类型
     * */
    public static void i(String message) {
        if(SDCARD_LOG){
            message = getMessage(TAG, message);
            Log.i(TAG, message);
            writeToFile(message);
        }
    }

    /**
     * @param message 日志类型
     * */
    public static void d(String message) {
        if(SDCARD_LOG){
            message = getMessage(TAG, message);
            Log.d(TAG, message);
            writeToFile(message);
        }
    }

    /**
     * */
    public static void e() {
        if(SDCARD_LOG){
            String message = getMessage(TAG, "");
            Log.e(TAG, message);
            writeToFile(message);
        }

    }

    /**
     * */
    public static void i() {
        if(SDCARD_LOG){
            String message = getMessage(TAG, "");
            Log.i(TAG, message);
            writeToFile(message);
        }
    }

    /**
     * */
    public static void d() {
        if(SDCARD_LOG){
            String message = getMessage(TAG, "");
            Log.d(TAG, message);
            writeToFile(message);
        }
    }

    private static String getMessage(String tag, String message){
        StackTraceElement se = new Throwable().getStackTrace()[1];
        StackTraceElement[] element = Thread.currentThread().getStackTrace();
        for (int i = 0 ;i < element.length; i++){
            if(element[i].getClassName().startsWith(PACKAGE)){
                 se = element[i];
                 break;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(tag);
        sb.append(":");
        sb.append(se.getClassName().substring(se.getClassName().lastIndexOf(".") + 1, se.getClassName().length()));
        sb.append(".");
        sb.append(se.getMethodName());
        sb.append(":");
        sb.append(se.getLineNumber());
        sb.append("::");
        sb.append(message);
        return sb.toString();
    }

    private static void writeToFile(String message){
        try {
            message = ("[" + fmt.format(System.currentTimeMillis()) + "]:")+message;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File dir = new File(PATH);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(PATH + fileName, true);
                OutputStreamWriter sw = new OutputStreamWriter(fos);
                sw.append("\n");
                sw.append(message);
                sw.flush();
                fos.close();
            }
        } catch (Exception e) {

        }
    }

}
