package com.example.notificationtest.upload;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lining on 2017/3/15.
 */

public class SvHttpUpload {
    public static void upLoadFile(String str, File file){
        try {
            String result = "";
            URL url = new URL(str);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "application/octet-stream");
            connection.setRequestProperty("x-fission-length", String.valueOf(file.length()));
            BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());

            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int numReadByte = 0;
            int upSize = 0;
            while ((numReadByte = fileInputStream.read(bytes, 0, 1024)) > 0) {
                out.write(bytes, 0, numReadByte);
                upSize = upSize + numReadByte;
                Log.e("lining","upSize = "+upSize);
            }

            out.flush();
            fileInputStream.close();
            if(connection.getResponseCode() == 200){
                result += convertStreamToString(connection.getInputStream()) + "\n";
                Log.e("lining","result = "+result);
            }
            // 读取URLConnection的响应
            DataInputStream indataStream = new DataInputStream(connection.getInputStream());
            int bytesRead = 0;
            byte data[] = new byte[1024];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bytesRead = indataStream.read(data);
            while (-1 != bytesRead) {
                baos.write(data, 0, bytesRead);
                bytesRead = indataStream.read(data);
            }
            byte[] resultBytes = baos.toByteArray();
            baos.close();
            connection.disconnect();
            String inResult = new String(resultBytes,"UTF-8");
            Log.e("lining","inResult = "+inResult);
        }catch (Exception e){

        }
    }
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
