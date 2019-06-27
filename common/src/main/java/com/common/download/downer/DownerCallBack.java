package com.common.download.downer;

import android.os.Parcel;
import android.os.Parcelable;

import com.common.download.DownerException;

/**
 * 下载监听返回，通知发起请求者，有可能为空，需要非空判断
 */
public abstract class DownerCallBack implements Parcelable {

    /*连接下载服务*/
    public abstract void onConnected(DownerRequest request);
    /*断开下载服务*/
    public void onDisconnected(){}
    /*下载开始*/
    public void onStart() {}
    /*下载进度*/
    public abstract void onProgress(long max, long progress);
    /*下载暂停*/
    public void onPause() {}
    /*下载中断*/
    public abstract void onStop(DownerException e);
    /*下载完成*/
    public abstract void onComplete();
    /*检查安装*/
    public void onCheckInstall(){}
    /*开始安装*/
    public void onStartInstall(){}
    /*安装失败*/
    public  void onErrorInstall(DownerException e){}
    /*安装完成*/
    public  void onCompleteInstall(){}





    public static final Creator<DownerCallBack> CREATOR = new Creator<DownerCallBack>() {
        @Override
        public DownerCallBack createFromParcel(Parcel in) {
            return new DownerCallBack(in) {
                public void onConnected(DownerRequest request) {}
                public void onProgress(long max, long progress) {}
                 public void onStop(DownerException e) {}
                 public void onComplete() {}
            };
        }
        @Override
        public DownerCallBack[] newArray(int size) {
            return new DownerCallBack[size];
        }
    };
    @Override
    public int describeContents() {return 0;}
    @Override
    public void writeToParcel(Parcel dest, int flags) {}
    protected DownerCallBack(Parcel in) {}
    protected DownerCallBack() {}


}
