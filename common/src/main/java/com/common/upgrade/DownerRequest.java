package com.common.upgrade;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.common.upgrade.model.DownlaodOptions;

import java.io.File;

/**
 * 下载请求类
 */
public class DownerRequest implements Parcelable {

    /**下载参数构造类*/
    private DownlaodOptions.Builder optionsBulider;
    /**下载监听返回，通知发起请求者，有可能为空，需要非空判断*/
    public DownerCallBack downerCallBack;
    /**下载参数类*/
    public DownlaodOptions options;
    /**下载状态*/
    public volatile int status;

    public DownerRequest() {
        optionsBulider = new DownlaodOptions.Builder();
    }

    protected DownerRequest(Parcel in) {
        options = in.readParcelable(DownlaodOptions.class.getClassLoader());
        status = in.readInt();
    }

    public static final Creator<DownerRequest> CREATOR = new Creator<DownerRequest>() {
        @Override
        public DownerRequest createFromParcel(Parcel in) {
            return new DownerRequest(in);
        }

        @Override
        public DownerRequest[] newArray(int size) {
            return new DownerRequest[size];
        }
    };

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
     * 执行下载
     */
    public void execute(Context context, DownerCallBack callBack) {
        if(callBack != null){
            this.downerCallBack = callBack;
        }
        Log.i(Downer.TAG, "DownerRequest:  execute ");
        options = optionsBulider.build();
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
    }

    /**暂停下载任务*/
    public void pause(){
        Log.i(Downer.TAG, "DownerRequest:  pause ");
        status = Downer.STATUS_DOWNLOAD_PAUSE;
    }

    /**恢复下载任务*/
    public void resume(Context context){
        Log.i(Downer.TAG, "DownerRequest:  resume ");
        status = Downer.STATUS_DOWNLOAD_START;
        /**启动DownerService*/
        DownerService.startDownerService(context, this);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(options, flags);
        dest.writeInt(status);
    }
}
