package com.common.download.downer;

import com.common.download.DownerException;

/**
 * 下载监听返回，通知发起请求者，有可能为空，需要非空判断
 */
public abstract class DownerCallBack<T>{

    /*连接下载服务*/
    public abstract void onConnected(DownerRequest request);
    /*断开下载服务*/
    public void onDisconnected(T model){}
    /*下载开始*/
    public void onStart(T model) {}
    /*下载进度*/
    public abstract void onProgress(long max, long progress);
    /*下载暂停*/
    public void onPause(T model) {}
    /*下载中断*/
    public abstract void onStop(T model, DownerException e);
    /*下载完成*/
    public abstract void onComplete(T model);
    /*检查安装*/
    public void onCheckInstall(T model){}
    /*开始安装*/
    public void onStartInstall(T model){}
    /*安装失败*/
    public  void onErrorInstall(T model, DownerException e){}
    /*安装完成*/
    public  void onCompleteInstall(T model){}


}
