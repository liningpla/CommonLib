package com.common.upgrade;

/**
 */
public abstract class OnDownloadListener {
    /*下载之前*/
    public abstract void  onDownBefore(DownlaodClient downlaodClient);
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
    public void onErrorInstall(DownlaodException e){}
    /*安装完成*/
    public void onCompleteInstall(){}



}
