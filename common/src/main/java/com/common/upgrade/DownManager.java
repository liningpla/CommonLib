package com.common.upgrade;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Preconditions;

import com.common.upgrade.model.bean.DownOptions;

import java.lang.ref.WeakReference;

/**
 * 下载管理
 */

public class DownManager {
    public static final String TAG = "DownLoad";
    private Context context;
    private CheckParseUrl task;

    public DownManager(Context context) {
        this.context = context;
    }

    /**
     * 下载
     *
     * @param options          下载选项
     * @param downLoadListener 下载监听回调接口
     */
    @SuppressLint("RestrictedApi")
    public void doDownLoad(@NonNull DownOptions options, @Nullable OnDownloadListener downLoadListener) {
        execute(Preconditions.checkNotNull(options), downLoadListener);
    }

    /**
     * 执行下载
     *
     * @param parames
     */
    private void execute(Object... parames) {
        if (task == null || task.getStatus() == AsyncTask.Status.FINISHED) {
            task = new CheckParseUrl(context);
        }
        if (task.getStatus() == AsyncTask.Status.RUNNING) {
            return;
        }
        task.execute(parames);
    }

    /**
     * 取消下载
     */
    public void cancel() {
        if (task == null) {
            return;
        }
        if (!task.isCancelled()) {
            task.cancel(false);
        }
        Log.d(TAG, "cancel down laod");
    }

    /**
     * 检测解析url
     */
    private static class CheckParseUrl extends AsyncTask<Object, Void, Message> {
        private static final int RESULT_CODE_TRUE = 0x1024;
        private WeakReference<Context> reference;

        private CheckParseUrl(Context context) {
            this.reference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Message doInBackground(Object... objects) {
            Message message = new Message();
            message.what = RESULT_CODE_TRUE;
            message.obj = objects[1];
            message.setData(new Bundle());
            DownOptions downOptions = (DownOptions) objects[0];
            /*------------------------------------------start*/
            //这里考虑解析url，解决重定向是，得到真正下载的url
            /*------------------------------------------end*/
            message.getData().putParcelable("upgrade_options", downOptions);
            return message;
        }

        @Override
        protected void onCancelled(Message message) {
            super.onCancelled(message);
        }

        @Override
        protected void onPostExecute(Message message) {
            Context context = reference.get();
            if (context == null) {
                return;
            }
            Bundle bundle = message.getData();
            DownOptions downOptions = bundle.getParcelable("upgrade_options");
            DownOptions.Builder builder = new DownOptions.Builder()
                    .setIcon(downOptions.getIcon())
                    .setTitle(downOptions.getTitle())
                    .setDescription(downOptions.getDescription())
                    .setStorage(downOptions.getStorage())
                    .setUrl(downOptions.getUrl())
                    .setMultithreadEnabled(downOptions.isMultithreadEnabled())
                    .setMultithreadPools(downOptions.getMultithreadPools())
                    .setAutomountEnabled(downOptions.isAutomountEnabled())
                    .setAutocleanEnabled(downOptions.isAutocleanEnabled())
                    .setMd5(downOptions.getMd5());
            OnDownloadListener downLoadListener = (OnDownloadListener) message.obj;
            if (downLoadListener != null) {
                downLoadListener.onBefore();
            }
            Log.d(TAG, "DownManager:CheckParseUrl:onPostExecute:  -- start");
            DownLoadClient.add(context, builder.build()).setOnDownloadListener(downLoadListener).start();
        }

    }

}
