package com.example.notificationtest.httplib;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Multipart {

    private static final String multipart_form_data = "multipart/form-data";
    private static final String twoHyphens = "--";
    private static final String boundary = "----------------314159265358979323846";    // 数据分隔符
    private static final String lineEnd =  "\r\n";

    private HttpURLConnection conn;
    private String jsonStr;
    private HiCallBack mCallBack;
    private List<HiFormFile> formFiles = new LinkedList<>();
    private long totalLength;

    public Multipart(HttpURLConnection conn, LinkedHashMap<String, File> files, String jsonStr, HiCallBack mCallBack) {
        this.conn = conn;
        this.jsonStr = jsonStr;
        this.mCallBack = mCallBack;
        Set<Map.Entry<String, File>> entrySet = files.entrySet();
        Iterator<Map.Entry<String, File>> iter = entrySet.iterator();
        while (iter.hasNext()) {
            Map.Entry<String, File> entry = iter.next();
            HiFormFile formfile = new HiFormFile(entry.getValue(), entry.getKey(), null);
            totalLength += entry.getKey().length();
            formFiles.add(formfile);
        }
    }

    private void addImageContent(List<HiFormFile> files, DataOutputStream output, HiCallBack mCallBack) {
        for(HiFormFile file : files) {
            StringBuilder split = new StringBuilder();   
            split.append(twoHyphens + boundary + lineEnd);   
            split.append("Content-Disposition: form-data; name=\"" + file.getParameterName() + "\"; filename=\"" + "noname" + "\"" + lineEnd);   
            split.append("Content-Type: " + file.getContentType() + lineEnd);   
            split.append(lineEnd);
            try {
                // 发送图片数据
                output.writeBytes(split.toString());
                FileInputStream inStream = new FileInputStream(file.getFile());
                int currLength = 0;
                byte[] b = new byte[1024];
                int readLength;
                while ((readLength = inStream.read(b)) != -1) {
                    output.write(b, 0, readLength);
                    currLength += readLength;
                    mCallBack.uploadProgress(currLength, totalLength);
                }
                mCallBack.uploadProgress(totalLength, totalLength);
                inStream.close();
                output.writeBytes(lineEnd);
            } catch (IOException e) {   
                throw new RuntimeException(e);   
            }   
        }   
    }   
    
    private void addFormField(String jsonstr, DataOutputStream output) {
        StringBuilder sb = new StringBuilder();   
            sb.append(twoHyphens + boundary + lineEnd);   
            sb.append("Content-Disposition: form-data; name=\"" + "jsonstr" + "\"" + lineEnd);   
            sb.append(lineEnd);   
            sb.append(jsonstr + lineEnd);   
        try {   
            output.writeBytes(sb.toString());// 发送表单字段数据    
        } catch (IOException e) {   
            throw new RuntimeException(e);   
        }   
    }   
    public String multipart() {
        DataOutputStream output = null;
        BufferedReader input = null;   
        String jsonResult ="timeout";
        try {
            conn.setRequestProperty("Content-Type", multipart_form_data + "; boundary=" + boundary);
            output = new DataOutputStream(conn.getOutputStream());
            addImageContent(formFiles, output, mCallBack);    // 添加图片内容
            addFormField(jsonStr, output);    // 添加表单字段内容
            output.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd+lineEnd);// 数据结束标志    
            output.flush();   
            int code = conn.getResponseCode();   
            if(code != 200) {   
                throw new RuntimeException(" Request" + conn.getURL() +" errcode :"+ code);
            }   
            if (conn.getResponseCode() == 200) {
                byte data[] = new byte[1024];
                InputStream indataStream = conn
                        .getInputStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int bytesRead = indataStream.read(data);
                while (-1 != bytesRead) {
                    baos.write(data, 0, bytesRead);
                    bytesRead = indataStream.read(data);
                }
                byte[] resultBytes = baos.toByteArray();
                baos.close();
                conn.disconnect();
                jsonResult = new String(resultBytes, "UTF-8");
                return jsonResult;
            }
 
            return jsonResult;   
        } catch (IOException e) {   
            throw new RuntimeException(e);  
        } finally {   
            // 统一释放资源
            try {   
                if(output != null) {   
                    output.close();   
                }   
                if(input != null) {   
                    input.close();   
                }   
            } catch (IOException e) {   
                throw new RuntimeException(e);   
            }   
               
            if(conn != null) {   
                conn.disconnect();   
            } 
        }   
    }

}
