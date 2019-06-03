package com.example.notificationtest.httplib;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

public enum HiHttp {
    instance;
    public static final String TAG = "HiHttp";
    public static Application mApplication;
    private HttpParams mCommonParams;
    private HttpHeaders mCommonHeaders;
    private int mRetryCount;                //全局超时重试次数
    private int connectTimeout;
    private int readTimeout;
    private String mContentType;            //全局Content-Type
    private static Handler mDelivery;              //用于在主线程执行的调度器
    public static HiHttp init(Application application){
        mApplication = application;
        HiLog.initLog(application);
        mDelivery = new Handler(Looper.getMainLooper());
        return instance;
    }

    /**
     * POST 请求
     */
    public static <T> Request<T> post(String urlPath) {
        return new Request<>(HttpMethod.POST, urlPath);
    }

    /**
     * GET 请求
     */
    public static <T> Request<T> get(String urlPath) {
        return new Request<>(HttpMethod.GET, urlPath);
    }

    /**
     * 获取全局公共请求头
     */
    public HttpParams getCommonParams() {
        return mCommonParams;
    }

    /**
     * 添加全局公共请求参数
     */
    public HiHttp addCommonParams(HttpParams commonParams) {
        if (mCommonParams == null) mCommonParams = new HttpParams();
        mCommonParams.put(commonParams);
        return this;
    }

    /**
     * 获取全局公共请求头
     */
    public HttpHeaders getCommonHeaders() {
        return mCommonHeaders;
    }

    /**
     * 添加全局公共请求参数
     */
    public HiHttp addCommonHeaders(HttpHeaders commonHeaders) {
        if (mCommonHeaders == null) mCommonHeaders = new HttpHeaders();
        mCommonHeaders.put(commonHeaders);
        return this;
    }

    /**获取主线程调度器*/
    public Handler getDelivery() {
        return mDelivery;
    }

    /**设置超时重试次数*/
    public HiHttp setRetryCount(int retryCount){
        mRetryCount = retryCount;
        return this;
    }

    public int getRetryCount(){
        return mRetryCount;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public HiHttp setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public HiHttp setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public HiHttp setContentType(String contentType){
        mContentType = contentType;
        return this;
    }

    public String getContentType() {
        return mContentType;
    }
}
