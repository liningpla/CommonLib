package com.common.upgrade;

/**
 */
public abstract class OnDownloadListener {


    //下载之前
    public void onBefore(){}
    //开始下载
    public void onStart() {}
    //链接下载地址
    public void onConnected(){}
    //取消下载链接
    public void onDisconnected(){}
    //下载进度显示
    public abstract void onProgress(long max, long progress);
    //下载暂停
    public void onPause() {}
    //下载取消下载
    public void onCancel() {}
    //下载失败
    public abstract void onError(UpgradeException e);
    //下载完成
    public abstract void onComplete();
    //检查安装
    public void onCheckInstall(){}
    //开始安装
    public void onStartInstall(){}
    //取消安装
    public void onCancelInstall(){}
    //安装失败
    public void onErrorInstall(UpgradeException e){}
    //安装完成
    public void onCompleteInstall(){}
}
