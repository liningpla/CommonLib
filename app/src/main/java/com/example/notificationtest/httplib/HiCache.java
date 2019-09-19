package com.example.notificationtest.httplib;

import android.content.Context;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**缓存读取工具类*/
public class HiCache {

    /**保存缓存*/
    public static void saveCache(String result, String filePath){
        if(!TextUtils.isEmpty(filePath)){
            File file = new File(filePath);
            if(file.exists()){
                file.delete();
            }
            FileOutputStream outputStream;
            try {
                outputStream = new FileOutputStream(file);
                outputStream.write(result.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**读取Cache缓存*/
    public static String readCache(String filePath){
        String result = "";
        try {
            if(!TextUtils.isEmpty(filePath)){
                File file = new File(filePath);
                FileInputStream in = new FileInputStream(file);
                result = getString(in);
            }
        }catch (Exception e){
        }
        return result;
    }

    /**读取Asset缓存*/
    public static String readAsset(Context context, String fileName){
        String result = "";
        try {
            if(context != null && !TextUtils.isEmpty(fileName)){
                InputStream inputStream = context.getAssets().open(fileName);
                result = getString(inputStream);
                return result;
            }
        }catch (Exception e){
        }
        return result;
    }

    private static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
