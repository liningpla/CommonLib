package com.example.notificationtest.httplib;


public abstract class HiCallBack<T> implements HiConverter<T> {

    Class<T> clazz;

    public HiCallBack(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 类型转化函数，子线程
     */
    @Override
    public T convertResponse(Response response) {
        if(clazz == String.class){
            return null;
        }
        response.setBody(HiJson.jsonObject(clazz, (String) response.body()));
        return (T) response.body();
    }

    /**
     * 请求网络开始前，UI线程
     */
    public void onStart(Request request) {
    }

    /**
     * 对返回数据进行操作的回调， UI线程
     */
    public void onSuccess(Response response) {
    }

    /**
     * 缓存成功的回调,UI线程
     */
    public void onCacheSuccess(Response response) {
    }

    /**
     * 请求失败，响应错误，数据解析错误等，都会回调该方法， UI线程
     */
    public void onError(Response response) {
    }

    /**
     * 请求网络结束后，UI线程
     */
    public void onFinish() {
    }

    /**
     * 上传过程中的进度回调，get请求不回调，UI线程
     */
    public void uploadProgress(long progress, long totalLength) {
    }

    /**
     * 下载过程中的进度回调，UI线程
     */
    public void downloadProgress(long progress) {
    }
}
