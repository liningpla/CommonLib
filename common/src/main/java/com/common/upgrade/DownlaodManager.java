package com.common.upgrade;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Preconditions;

import com.common.upgrade.model.bean.DownlaodOptions;

import java.lang.ref.WeakReference;

/**
 */

public class DownlaodManager {
    public static final String TAG = "download";
    private Activity activity;
    private CheckForUpdatesTask task;

    public DownlaodManager(Activity activity) {
        this.activity = activity;
    }

    /**
     * 检测更新
     *
     * @param options     更新选项
     * @param isAutoCheck 是否自动检测更新
     */
    @SuppressLint("RestrictedApi")
    public void checkForUpdates(@NonNull DownlaodOptions options, boolean isAutoCheck) {
        execute(Preconditions.checkNotNull(options), isAutoCheck);
    }

    /**
     * 检测更新
     *
     * @param options           更新选项
     * @param onUpgradeListener 更新监听回调接口
     */
    @SuppressLint("RestrictedApi")
    public void checkForUpdates(@NonNull DownlaodOptions options, @Nullable OnDownloadListener onUpgradeListener) {
        execute(Preconditions.checkNotNull(options), onUpgradeListener);
    }

    /**
     * 执行检测更新
     *
     * @param parames
     */
    private void execute(Object... parames) {
        if (task == null || task.getStatus() == AsyncTask.Status.FINISHED) {
            task = new CheckForUpdatesTask(activity);
        }
        if (task.getStatus() == AsyncTask.Status.RUNNING) {
            return;
        }
        task.execute(parames);
    }

    /**
     * 取消检测更新
     */
    public void cancel() {
        if (task == null) {
            return;
        }
        if (!task.isCancelled()) {
            task.cancel(false);
        }
        Log.d(TAG, "cancel checked updates");
    }

    /**
     * 检测更新任务
     */
    private static class CheckForUpdatesTask extends AsyncTask<Object, Void, Message> {
        private static final int RESULT_CODE_TRUE = 0x1024;
        private static final int RESULT_CODE_FALSE = 0x1025;
        private WeakReference<Activity> reference;

        private CheckForUpdatesTask(Activity activity) {
            this.reference = new WeakReference<>(activity);
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
            DownlaodOptions upgradeOptions = (DownlaodOptions) objects[0];
            message.getData().putParcelable("upgrade_options", upgradeOptions);
            /*--------------这里解析下载地址 获取文件长度-----------start*/

            /*--------------这里解析下载地址 获取文件长度-----------end*/
            return message;
        }

        @Override
        protected void onCancelled(Message message) {
            super.onCancelled(message);
        }

        @Override
        protected void onPostExecute(Message message) {
            Activity activity = reference.get();
            if (activity == null) {
                return;
            }
            Bundle bundle = message.getData();
            DownlaodOptions upgradeOptions = bundle.getParcelable("upgrade_options");
            DownlaodOptions.Builder builder = new DownlaodOptions.Builder()
                    .setIcon(upgradeOptions.getIcon())
                    .setTitle(upgradeOptions.getTitle())
                    .setDescription(upgradeOptions.getDescription())
                    .setStorage(upgradeOptions.getStorage())
                    .setUrl(upgradeOptions.getUrl())
                    .setMultithreadEnabled(upgradeOptions.isMultithreadEnabled())
                    .setMultithreadPools(upgradeOptions.getMultithreadPools())
                    .setAutomountEnabled(upgradeOptions.isAutomountEnabled())
                    .setAutocleanEnabled(upgradeOptions.isAutocleanEnabled())
                    .setMd5(upgradeOptions.getMd5());
            DownlaodClient downlaodClient = DownlaodClient.add(activity, builder.build());
            downlaodClient.start();
            OnDownloadListener downloadListener = (OnDownloadListener) message.obj;
            if(downloadListener != null){
                downloadListener.onDownBefore(downlaodClient);
            }

        }

    }

}
