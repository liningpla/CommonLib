package com.example.notificationtest.upload;

import android.util.Log;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lining on 2017/3/15.
 * 文件上传管理类
 */
public class SvUploadUtils {
    /**分片上传任务类*/
    public static class HttpUploadTask implements Runnable{
        private FilePartInfo filePartInfo;
        private PartFileUploadListener listener;
        public HttpUploadTask(FilePartInfo filePartInfo, PartFileUploadListener listener) {
            this.filePartInfo = filePartInfo;
            this.listener = listener;
        }
        @Override
        public void run() {
            upLoadFile(filePartInfo, listener);
        }
    }
    /**上传文件
     *
     * */
    private static void upLoadFile(FilePartInfo filePartInfo, PartFileUploadListener listener){
        try {
            String result = "";
            File parentFile = new File(filePartInfo.parentFilePath);
//            String urlStr = SvUploadManager.UPLOAD_URI+ filePartInfo.parentMd5;
            String urlStr = SvUploadManager.UPLOAD_URI;
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "application/octet-stream");
            connection.setRequestProperty("x-fission-range", filePartInfo.getRangeSize()+"-"+ filePartInfo.endPos);
            connection.setRequestProperty("x-fission-length", String.valueOf(parentFile.length()));
            BufferedOutputStream out = new BufferedOutputStream(connection.getOutputStream());

            RandomAccessFile rFile = new RandomAccessFile(parentFile,"r");
            rFile.seek(filePartInfo.getRangeSize());
            byte[] bytes = new byte[1024];
            int numReadByte = 0;
            //如果已经上传的size小于分片的总大小，继续读取上传
            while ( filePartInfo.loadSize < filePartInfo.totalSize && (numReadByte = rFile.read(bytes, 0, 1024)) > 0 && !SvUploadManager.isPause) {
                out.write(bytes, 0, numReadByte);
                filePartInfo.loadSize = filePartInfo.loadSize + numReadByte;
                filePartInfo.num = numReadByte;
                listener.callBackPartFile(filePartInfo);
            }
            out.flush();
            rFile.close();

            filePartInfo.isLoadSuccess = true;
            if(connection.getResponseCode() == 200){
                result += SvFileUtils.convertStreamToString(connection.getInputStream()) + "\n";
                Log.e("lining","result = "+result);
                filePartInfo.isLoadSuccess = true;
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
            e.printStackTrace();
        }
    }
    /**分片文件监听*/
    public interface PartFileUploadListener{
        void callBackPartFile(FilePartInfo filePartInfo);
    }
}
