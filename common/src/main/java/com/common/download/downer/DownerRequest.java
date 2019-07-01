package com.common.download.downer;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.common.download.Downer;
import com.common.download.model.DownerOptions;

import java.io.File;

/**
 * 下载请求类
 */
public class DownerRequest<T> {

    /**下载参数构造类*/
    private DownerOptions.Builder optionsBulider;
    /**下载监听返回，通知发起请求者，有可能为空，需要非空判断*/
    public DownerCallBack downerCallBack;
    /**下载参数类*/
    public DownerOptions options;
    /**下载状态*/
    public volatile int status;
    /**如果下载的是apk，记录apk包明*/
    public String apkPageName;
    /**通知id，分配生成的三位数*/
    public int NOTIFY_ID;
    public ScheduleRunable scheduleRunable;
    private T model;
    public T getModel() {
        return model;
    }
    public DownerRequest(T model) {
        this.model = model;
        optionsBulider = new DownerOptions.Builder();
        optionsBulider.setSupportRange(Downer.init().isSupportRange);
        optionsBulider.setAutocleanEnabled(Downer.init().isClean);
        optionsBulider.setAutomountEnabled(Downer.init().isInstall);
        optionsBulider.setMultithreadEnabled(Downer.init().isMuliti);
        optionsBulider.setMultithreadPools(Downer.init().pools);
        optionsBulider.setOverride(Downer.init().isOverride);
        NOTIFY_ID = (int) (Math.random()*900 + 100);
    }

    /**
     * 设置下载图标
     */
    public DownerRequest setIcon(Bitmap icon) {
        optionsBulider.setIcon(icon);
        return this;
    }

    /**
     * 设置下载标题
     */
    public DownerRequest setTitle(String title) {
        optionsBulider.setTitle(title);
        return this;
    }

    /**
     * 设置下载描述
     */
    public DownerRequest setDescription(String des) {
        optionsBulider.setDescription(des);
        return this;
    }

    /**
     * 设置下载url
     */
    public DownerRequest setUrl(String url) {
        optionsBulider.setUrl(url);
        return this;
    }

    /**
     * 设置文件存储路径
     */
    public DownerRequest setStorage(File file) {
        optionsBulider.setStorage(file);
        return this;
    }

    /**
     * 是否支持多线程下载
     */
    public DownerRequest setMultithreadEnabled(boolean isMuliti) {
        optionsBulider.setMultithreadEnabled(isMuliti);
        return this;
    }

    /**
     * 设置线程池大小
     */
    public DownerRequest setMultithreadPools(int pools) {
        optionsBulider.setMultithreadPools(pools);
        return this;
    }

    /**
     * 设置md5校验（可选）
     */
    public DownerRequest setMd5(String md5) {
        if (!TextUtils.isEmpty(md5)) {
            optionsBulider.setMd5(md5);
        }
        return this;
    }

    /**
     * 是否自动删除安装（可选）
     */
    public DownerRequest setAutoInstallEnabled(boolean isInstall) {
        optionsBulider.setAutomountEnabled(isInstall);
        return this;
    }

    /**
     * 是否自动删除安装包（可选）
     */
    public DownerRequest setAutocleanEnabled(boolean isClean) {
        optionsBulider.setAutocleanEnabled(isClean);
        return this;
    }

    /**
     * 是否使用断点续传
     */
    public DownerRequest setSupportRange(boolean isSupportRange) {
        optionsBulider.setSupportRange(isSupportRange);
        return this;
    }

    /**
     * 执行下载
     */
    public void execute(Context context, DownerCallBack callBack) {
        this.downerCallBack = callBack;
        Log.i(Downer.TAG, "DownerRequest:  execute ");
        options = optionsBulider.build();
        scheduleRunable = new ScheduleRunable(context, this);
        DownerService.startDownerService(context, this);
    }

    /**
     * 执行下载
     */
    public void execute(Context context){
        execute(context,null);
    }

    /**取消下载任务*/
    public void cancle(){
        Log.i(Downer.TAG, "DownerRequest:  cancle ");
        status = Downer.STATUS_DOWNLOAD_CANCEL;
        release();
    }

    /**暂停下载任务*/
    public void pause(){
        Log.i(Downer.TAG, "DownerRequest:  pause ");
        if(status == Downer.STATUS_DOWNLOAD_COMPLETE){//完成了
            return;
        }
        scheduleRunable.listener.downLoadPause();
        status = Downer.STATUS_DOWNLOAD_PAUSE;
    }

    /**重新下载*/
    public void reStart(Context context){
        DownerService.startDownerService(context, this);
    }

    /**释放自己*/
    public void release(){
        if(DownerService.downerRequests != null){
            DownerService.downerRequests.remove(options.getUrl());
            Log.i(Downer.TAG, "DownerRequest:release "+options.getTitle()+" is released");
        }
    }
}
