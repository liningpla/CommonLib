package com.example.notificationtest.httplib;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Request<T> implements Serializable {
    // 键值对参数
    public static final String MEDIA_TYPE_NORAML_FORM = "application/x-www-form-urlencoded;charset=utf-8";
    // 既可以提交普通键值对，也可以提交(多个)文件键值对。
    public static final String MEDIA_TYPE_MULTIPART_FORM = "multipart/form-data;charset=utf-8";
    // 只能提交二进制，而且只能提交一个二进制，如果提交文件的话，只能提交一个文件,后台接收参数只能有一个，而且只能是流（或者字节数组）
    public static final String MEDIA_TYPE_STREAM = "application/octet-stream";
    // JSON参数
    public static final String MEDIA_TYPE_JSON = "application/json;charset=utf-8";
    private static final int DEFAULT_RETRY_COUNT = 3;
    private static final int CONNECT_OUT_TIME = 30000;//连接超时时间30秒
    private static final int READ_OUT_TIME = 600000;//60秒
    private String url;
    private String contentType = "";
    private HttpMethod httpMethod;
    private HttpParams requestHttpParams;
    private HttpHeaders requestHttpHeaders;
    private HttpURLConnection httpConn;
    private Priority priority = Priority.NORMAL;
    private String paramsJson;
    private int defaultRetryCount = DEFAULT_RETRY_COUNT;
    private int connectTimeout = CONNECT_OUT_TIME;
    private int readTimeout = READ_OUT_TIME;
    private boolean isToJson;
    private transient HiCallBack<T> mCallBack;
    private Response<T> mResponse;
    private Handler mHandler;
    private String mParent;//json参数转化的父类结点名称
    private LinkedHashMap<String, File> fileParams;
    private String fileCache, assetCache;

    public String getUrl() {
        return url;
    }

    public Request(HttpMethod httpMethod, String url) {
        this.httpMethod = httpMethod;
        this.url = url;
        mHandler = HiHttp.instance.getDelivery();
        if(mHandler == null){
            mHandler = new Handler(Looper.getMainLooper());
        }
        if (HiHttp.instance.getRetryCount() > 0) {
            defaultRetryCount = HiHttp.instance.getRetryCount();
        }
        if (HiHttp.instance.getConnectTimeout() > 0) {
            connectTimeout = HiHttp.instance.getConnectTimeout();
        }
        if (HiHttp.instance.getReadTimeout() > 0) {
            readTimeout = HiHttp.instance.getReadTimeout();
        }
        if(!TextUtils.isEmpty(HiHttp.instance.getContentType())){
            contentType = HiHttp.instance.getContentType();
        }
        if (requestHttpHeaders == null) {
            requestHttpHeaders = new HttpHeaders();
        }
        if (requestHttpParams == null) {
            requestHttpParams = new HttpParams();
        }
    }

    /**
     * 键值对参数
     */
    public Request params(String key, String value) {
        requestHttpParams.put(key, value);
        return this;
    }

    /**
     * Json字符串参数，使用后自动将键值参数合并到Json字符中
     */
    public Request paramsJson(String paramsString) {
        contentType = MEDIA_TYPE_JSON;
        this.paramsJson = paramsString;
        return this;
    }

    /**
     * 键值对参数
     */
    public Request params(HttpParams params) {
        contentType = MEDIA_TYPE_NORAML_FORM;
        requestHttpParams.put(params);
        return this;
    }

    /**请求头添加-兼职对*/
    public Request headers(String key, String value) {
        requestHttpHeaders.put(key, value);
        return this;
    }

    /**请求头添加-集合*/
    public Request headers(HttpHeaders headers) {
        requestHttpHeaders.put(headers);
        return this;
    }

    /**设置Http请求contentType*/
    public Request contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * 设置网络任务优先级
     * HIGH：最高级别的优先级
     * NORMAL：正常优先级
     * LOW：最低级别优先级
     */
    public Request priority(Priority contentType) {
        this.priority = priority;
        return this;
    }

    /**设置Http连接失败尝试*/
    public Request retryCount(int retryCount) {
        defaultRetryCount = retryCount;
        return this;
    }

    /**设置Http连接超时时间，默认 30秒*/
    public Request connectTimeout(int timeout) {
        connectTimeout = timeout;
        return this;
    }

    /**设置http读取超时时间，默认 60秒*/
    public Request readTimeout(int timeout) {
        readTimeout = timeout;
        return this;
    }

    /**键值参数转化字符，有父节点，全局键值参数下使用，paramsJson同时使用时，只执行paramsJson逻辑*/
    public Request toJson(String parent) {
        contentType = MEDIA_TYPE_JSON;
        isToJson = true;
        mParent = parent;
        return this;
    }

    /**键值参数转化字符，无父节点，全局键值参数下使用，paramsJson同时使用时，只执行paramsJson逻辑*/
    public Request toJson() {
        contentType = MEDIA_TYPE_JSON;
        isToJson = true;
        return this;
    }
    /**上传表单文件
     * @param parameterName 文件的上传字段名
     * * */
    public Request multipart(String parameterName, File file){
        contentType = MEDIA_TYPE_MULTIPART_FORM;
        if(fileParams == null){
            fileParams = new LinkedHashMap<>();
        }
        fileParams.put(parameterName, file);
        return this;
    }
    /**
     * 构建请求
     */
    private HttpURLConnection bulidHttp() {
        try {
            URL url = new URL(getUrl());
            httpConn = (HttpURLConnection) url.openConnection();
            //设置参数
            if(httpMethod != HttpMethod.GET){
                httpConn.setRequestMethod(httpMethod.toString());      //设置连接方式
                //需要输入
                httpConn.setDoInput(true);
                httpConn.setUseCaches(false);   //不允许缓存
                httpConn.setDoOutput(true);     //需要输出
                httpConn.setRequestProperty("Content-Type", contentType);
            }
            httpConn.setConnectTimeout(connectTimeout);
            httpConn.setReadTimeout(readTimeout);
            bulidHead();
            boolean useHttps = getUrl().startsWith("https");
            if (useHttps) {
                HttpsURLConnection https = (HttpsURLConnection) httpConn;
                trustAllHosts(https);
                https.setHostnameVerifier(DO_NOT_VERIFY);
            }

        } catch (Exception e) {
            e.printStackTrace();
            HiLog.e(HiHttp.TAG, e.getMessage());
        }
        return httpConn;
    }

    /**
     * 构建请求头
     */
    private void bulidHead() {
        httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
        httpConn.setRequestProperty("Charset", "UTF-8");
        if (HiHttp.instance.getCommonHeaders() != null &&
                HiHttp.instance.getCommonHeaders().getSize() > 0 && requestHttpHeaders != null) {
            requestHttpHeaders.put(HiHttp.instance.getCommonHeaders());
        }
        if(requestHttpHeaders.httpHeaders.size() > 0){
            Set<Map.Entry<String, String>> entrySet = requestHttpHeaders.httpHeaders.entrySet();
            Iterator<Map.Entry<String, String>> iter = entrySet.iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                httpConn.setRequestProperty(entry.getKey(), entry.getValue());// 维持长连接
            }
        }
    }

    /**
     * 构建请求头请求参数
     */
    private String bulidParams() {
        String strParams = "";
        try {
            //添加公共参数
            if (HiHttp.instance.getCommonParams() != null &&
                    HiHttp.instance.getCommonParams().getSize() > 0 && requestHttpParams != null) {
                requestHttpParams.put(HiHttp.instance.getCommonParams());
            }
            if(httpMethod == HttpMethod.GET){
                bulidGetParams();
            }else{
                //如果传入的参数是json字符，有父节点
                if (!TextUtils.isEmpty(paramsJson)) {
                    strParams = bulidParamsJson();
                } else {
                    strParams = bulidKeyValue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            HiLog.e(HiHttp.TAG, e.getMessage());
        }
        HiLog.i(HiHttp.TAG, strParams);
        return strParams;
    }

    private void bulidGetParams(){
        try {
            String strParams = "";
            //封装键值对参数
            Set<Map.Entry<String, String>> entrySet = requestHttpParams.httpParams.entrySet();
            Iterator<Map.Entry<String, String>> iter = entrySet.iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                strParams = strParams + entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
            }
            if(!TextUtils.isEmpty(strParams)){
                url = url + "?"+strParams;
            }
            HiLog.e(HiHttp.TAG, url);
        }catch (Exception e){
            e.printStackTrace();
            HiLog.e(HiHttp.TAG, e.getMessage());
        }
    }

    private String bulidKeyValue (){
        String strParams = "";
        try {
            if(isToJson){
                //键值对转化为Json
                JSONObject jsonParent = new JSONObject();
                JSONObject jsonObject = new JSONObject();
                Set<Map.Entry<String, String>> entrySet = requestHttpParams.httpParams.entrySet();
                Iterator<Map.Entry<String, String>> iter = entrySet.iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    jsonObject.put(entry.getKey(), entry.getValue());
                }
                if(!TextUtils.isEmpty(mParent)){
                    jsonParent.put(mParent,jsonObject);
                    strParams = jsonParent.toString();
                }else{
                    strParams = jsonObject.toString();
                }
            }else{
                //封装键值对参数
                Set<Map.Entry<String, String>> entrySet = requestHttpParams.httpParams.entrySet();
                Iterator<Map.Entry<String, String>> iter = entrySet.iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    strParams = strParams + entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), "UTF-8") + "&";
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            HiLog.e(HiHttp.TAG, e.getMessage());
        }
        return strParams;
    }

    private String bulidParamsJson(){
        String strParams = "";
        try {
            JSONObject jsonObject = new JSONObject(paramsJson);
            Set<Map.Entry<String, String>> entrySet = requestHttpParams.httpParams.entrySet();
            Iterator<Map.Entry<String, String>> iter = entrySet.iterator();
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                jsonObject.put(entry.getKey(), entry.getValue());
            }
            strParams = jsonObject.toString();
        }catch (Exception e){
            e.printStackTrace();
            HiLog.e(HiHttp.TAG, e.getMessage());
        }
        return strParams;
    }

    /**执行http任务*/
    private void bulidHttpConntect() {
        try {
            defaultRetryCount--;
            httpConn = bulidHttp();
            httpConn.connect();
        } catch (ConnectException e) {//连接失败，那可以允许用户再次提交
            HiLog.e(HiHttp.TAG, e.getMessage());
            retryConnect(e);
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            mResponse.setThrowable(e);
            HiLog.e(HiHttp.TAG, e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            retryConnect(e);
            HiLog.e(HiHttp.TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void retryConnect(Exception e){
        if (defaultRetryCount > 0) {
            HiLog.e(HiHttp.TAG," Connect error, Retry connect :"+defaultRetryCount);
            bulidHttpConntect();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } else {
            mResponse.setThrowable(e);
        }
    }

    /***
     *传递参数，请求结果
     * */
    private String connectStreamResult(String post) {
        String result = "";
        try {
            if(httpMethod != HttpMethod.GET){
                // 获取URLConnection对象对应的输出流
                PrintWriter printWriter = new PrintWriter(httpConn.getOutputStream());
                // 发送请求参数
                printWriter.write(post);//post的参数 xx=xx&yy=yy
                // flush输出流的缓冲
                printWriter.flush();
            }
            //开始获取数据
            InputStream is;
            int status = httpConn.getResponseCode();
            if(status> 200){  //此处一定要根据返回的状态码state来初始化输入流。如果为错误
                is = httpConn.getErrorStream();
            }else{
                is = httpConn.getInputStream();
            }
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            byte[] arr = new byte[1024];
            while ((len = bis.read(arr)) != -1) {
                bos.write(arr, 0, len);
                bos.flush();
            }
            result = bos.toString("utf-8");
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
            mResponse.setThrowable(e);
            HiLog.e(HiHttp.TAG, e.getMessage());
        }
        return result;
    }

    /**返回错误到UI线程*/
    private void backErrorToUI(){
        mHandler.post(new Runnable() {
            public void run() {
                if(mResponse.getThrowable() != null){
                    mCallBack.onError(mResponse);
                    mCallBack.onFinish();
                }
            }
        });

    }
    /**返回错误到UI线程*/
    private void backSuccessToUI(String result){
        mResponse.setBody((T) result);
        mResponse.setResult(result);
        mCallBack.convertResponse(mResponse);
        mHandler.post(new Runnable() {
            public void run() {
                mCallBack.onSuccess(mResponse);
                mCallBack.onFinish();
            }
        });
    }

    public void execute(HiCallBack cllBack) {
        mCallBack = cllBack;
        mCallBack.onStart(this);
        mResponse = new Response<>();
        HiThreadManger.getInstance().execute(priority, new Runnable() {
            @Override
            public void run() {
                String params = bulidParams();//构建参数
                HiLog.i("------------params:" + params);
                bulidHttpConntect();//构建Http请求并连接
                HiLog.i("------------url:" + url);
                String result;
                if(fileParams != null){//上传表单文件参数
                    result = new Multipart(httpConn, fileParams, params, mCallBack).multipart();
                }else{
                    result = connectStreamResult(params);//普通文本参数
                }
                if(!TextUtils.isEmpty(result)){
                    HiLog.i("------------result:"+result);
                    backSuccessToUI(result);
                }else{
                    backErrorToUI();
                }
            }
        });
    }

    /**
     * 覆盖java默认的证书验证
     */
    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }};

    /**
     * 设置不验证主机
     */
    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * 信任所有
     * @param connection
     * @return
     */
    private static SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldFactory;
    }

}
