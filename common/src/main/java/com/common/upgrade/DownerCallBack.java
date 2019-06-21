package com.common.upgrade;

/**
 * 下载监听返回，通知发起请求者，有可能为空，需要非空判断
 */
public abstract class DownerCallBack {
    /*下载之前*/
    public void  onDownBefore(){}
    /*连接下载服务*/
    public void onConnected(){}
    /*断开下载服务*/
    public void onDisconnected(){}
    /*下载开始*/
    public void onStart() {}
    /*下载进度*/
    public abstract void onProgress(long max, long progress);
    /*下载暂停*/
    public void onPause() {}
    /*下载取消*/
    public void onCancel() {}
    /*下载开始*/
    public abstract void onError(DownlaodException e);
    /*下载完成*/
    public abstract void onComplete();
    /*检查安装*/
    public void onCheckInstall(){}
    /*开始安装*/
    public void onStartInstall(){}
    /*取消安装*/
    public void onCancelInstall(){}
    /*安装失败*/
    public abstract void onErrorInstall(DownlaodException e);
    /*安装完成*/
    public abstract void onCompleteInstall();



}
