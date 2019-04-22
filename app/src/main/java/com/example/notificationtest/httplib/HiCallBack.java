package com.example.notificationtest.httplib;


public abstract class HiCallBack<T> implements Converter<T> {

    Class<T> clazz;

    public HiCallBack(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 类型转化函数，子线程
     */
    @Override
    public T convertResponse(Response response) {
        return null;
    }

    /**
     * 请求网络开始前，UI线程
     */
    void onStart(Request request) {
    }

    /**
     * 对返回数据进行操作的回调， UI线程
     */
    void onSuccess(Response<T> response) {
    }

    /**
     * 缓存成功的回调,UI线程
     */
    void onCacheSuccess(Response<T> response) {
    }

    /**
     * 请求失败，响应错误，数据解析错误等，都会回调该方法， UI线程
     */
    void onError(Response<T> response) {
    }

    /**
     * 请求网络结束后，UI线程
     */
    void onFinish() {
    }

    /**
     * 上传过程中的进度回调，get请求不回调，UI线程
     */
    void uploadProgress(long progress, long totalLength) {
    }

    /**
     * 下载过程中的进度回调，UI线程
     */
    void downloadProgress(long progress) {
    }
}
