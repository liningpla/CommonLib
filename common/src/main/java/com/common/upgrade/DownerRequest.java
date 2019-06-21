package com.common.upgrade;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.common.upgrade.model.DownlaodOptions;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * 下载请求类
 */
public class DownerRequest implements Serializable {

    /**下载参数构造类*/
    private DownlaodOptions.Builder optionsBulider;
    /**下载监听返回，通知发起请求者，有可能为空，需要非空判断*/
    public DownerCallBack downerCallBack;
    /**下载上下文参数*/
    public SoftReference<Context> mContext;
    /**下载参数类*/
    public DownlaodOptions options;
    /**下载状态*/
    public volatile int status;

    public DownerRequest(Context context) {
        mContext = new SoftReference<>(context);
        optionsBulider = new DownlaodOptions.Builder();
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
     * 执行下载
     */
    public void execute(DownerCallBack callBack) {
        if(callBack != null){
            this.downerCallBack = callBack;
        }
        new CheckForUpdatesTask(this).execute(optionsBulider.build());
    }

    /**
     * 执行下载
     */
    public void execute(){
        execute(null);
    }

    /**取消下载任务*/
    public void cancle(){
        status = Downer.STATUS_DOWNLOAD_CANCEL;
    }

    /**暂停下载任务*/
    public void pause(){
        status = Downer.STATUS_DOWNLOAD_PAUSE;
    }

    /**恢复下载任务*/
    public void resume(){
        status = Downer.STATUS_DOWNLOAD_START;
        /**启动DownerService*/
        DownerService.startDownerService(mContext.get(), this);
    }

    /**
     * 检测url是否重定向，并且先获取下载文件总长度
     */
    private static class CheckForUpdatesTask extends AsyncTask<Object, Void, Message> {

        private int statusCode;
        private long fileLength = 0L;
        private String tureUrl;
        private DownerRequest downerRequest;

        public CheckForUpdatesTask(DownerRequest downerRequest) {
            this.downerRequest = downerRequest;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Message doInBackground(Object... objects) {
            Message message = new Message();
            message.setData(new Bundle());
            DownlaodOptions downlaodOptions = (DownlaodOptions) objects[0];
            /*--------------这里解析下载地址 获取文件长度-----------start*/
            connectHttp(downlaodOptions.getUrl());
            downlaodOptions.setTrueUrl(tureUrl);
            downlaodOptions.setFilelength(fileLength);
            Log.i(Downer.TAG, "DownerRequest:  tureUrl :"+tureUrl+"  fileLength: "+fileLength);
            /*--------------这里解析下载地址 获取文件长度-----------end*/
            message.getData().putParcelable(DownerService.DOWN_REQUEST, downlaodOptions);
            return message;
        }

        @Override
        protected void onCancelled(Message message) {
            super.onCancelled(message);
        }

        @Override
        protected void onPostExecute(Message message) {
            Log.i(Downer.TAG, "DownerRequest:  onPostExecute ");
            Bundle bundle = message.getData();
            downerRequest.options = bundle.getParcelable(DownerService.DOWN_REQUEST);
            /**启动DownerService*/
            DownerService.startDownerService(downerRequest.mContext.get(), downerRequest);
        }

        /**
         * 解析url，解析文件长度
         */
        private void connectHttp(String url) {
            tureUrl = url;
            HttpURLConnection readConnection = null;
            try {
                readConnection = (HttpURLConnection) new URL(url).openConnection();
                readConnection.setRequestMethod("GET");
                readConnection.setDoInput(true);
                readConnection.setDoOutput(false);
                readConnection.setConnectTimeout(Downer.CONNECT_TIMEOUT);
                readConnection.setReadTimeout(Downer.READ_TIMEOUT);
                readConnection.connect();
                statusCode = readConnection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    while ((statusCode == Downer.SC_MOVED_TEMPORARILY) || (statusCode == Downer.SC_MOVED_PERMANENTLY)) {
                        tureUrl = readConnection.getHeaderField(Downer.REDIRECT_LOCATION_KEY);
                        connectHttp(tureUrl);
                    }
                }
                fileLength = readConnection.getContentLength();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (readConnection != null) {
                    readConnection.disconnect();
                }
            }
        }

    }


}
