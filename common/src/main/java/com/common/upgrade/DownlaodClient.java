package com.common.upgrade;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.common.upgrade.model.bean.DownlaodOptions;
import com.common.upgrade.service.DownlaodService;

import java.lang.ref.WeakReference;

/**
 */
public class DownlaodClient {
    private static final String TAG = DownlaodManager.TAG;
    private Context context;
    private DownlaodOptions options;
    private Messenger client;
    private Messenger server;
    private ServiceConnection connection;
    private OnDownloadListener onDownloadListener;
    private boolean isConnected;

    /**
     * 添加客户端实例，同时绑定活动
     *
     * @param context
     * @param options
     * @return
     */
    public static DownlaodClient add(Context context, DownlaodOptions options) {
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null");
        }
        return new DownlaodClient(context, options);
    }

    private DownlaodClient(Context context, DownlaodOptions options) {
        this.context = context;
        this.options = options;
        this.client = new Messenger(new ClientHandler(this));
        this.connection = new UpgradeServiceConnection();
    }


    /**
     * 注入下载监听回调接口
     *
     * @param onDownloadListener
     */
    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.onDownloadListener = onDownloadListener;
    }


    /**
     * 销毁客户端实例，同时解除绑定活动
     */
    public void remove() {
        disconnect();
    }

    /**
     * 开始
     */
    public void start() {
        if (!isConnected) {
            bind();
        }
    }

    /**
     * 暂停
     */
    public void pause() {
        if (isConnected) {
            sendMessageToServer(DownlaodConstant.MSG_KEY_DOWNLOAD_PAUSE_REQ, null);
        }
    }

    /**
     * 继续
     */
    public void resume() {
        if (isConnected) {
            sendMessageToServer(DownlaodConstant.MSG_KEY_DOWNLOAD_RESUME_REQ, null);
        }
    }

    /**
     * 取消
     */
    public void cancel() {
        if (isConnected) {
            sendMessageToServer(DownlaodConstant.MSG_KEY_DOWNLOAD_RESUME_REQ, null);
        }
    }

    /**
     * 安装
     */
    public void install() {
        if (isConnected) {
            sendMessageToServer(DownlaodConstant.MSG_KEY_INSTALL_START_REQ, null);
        }
    }

    /**
     * 绑定升级服务
     */
    private void bind() {
        if (context instanceof Activity || context instanceof Service) {
            DownlaodService.start(context, options, connection);
            return;
        }
        DownlaodService.start(context, options);
    }

    /**
     * 解绑升级服务
     */
    private void unbind() {
        if (context != null && connection != null) {
            context.unbindService(connection);
        }
    }

    /**
     * 连接升级服务
     */
    private void connect() {
        if (!isConnected) {
            sendMessageToServer(DownlaodConstant.MSG_KEY_CONNECT_REQ, null);
        }
    }

    /**
     * 断开升级服务
     */
    private void disconnect() {
        if (isConnected && client != null && server != null) {
            sendMessageToServer(DownlaodConstant.MSG_KEY_DISCONNECT_REQ, null);
        }
    }

    /**
     * 发送消息到服务端
     *
     * @param key
     * @param data
     */
    private void sendMessageToServer(int key, Bundle data) {
        try {
            Message message = Message.obtain();
            message.replyTo = client;
            message.what = key;
            message.setData(data == null ? new Bundle() : data);
            server.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Handler {
        private WeakReference<DownlaodClient> reference;

        private ClientHandler(DownlaodClient client) {
            this.reference = new WeakReference<>(client);
        }

        @Override
        public void handleMessage(Message msg) {
            DownlaodClient client = reference.get();
            if (client == null) {
                return;
            }
            Bundle data = msg.getData();
            int code = data.getInt("code");
            String message = data.getString("message");
            switch (msg.what) {
                case DownlaodConstant.MSG_KEY_CONNECT_RESP:
                    if (code == 0) {
                        client.isConnected = true;
                        if (client.onDownloadListener != null) {
                            client.onDownloadListener.onConnected();
                        }
                    }
                    Log.d(TAG, message);
                    break;
                case DownlaodConstant.MSG_KEY_DISCONNECT_RESP:
                    if (code == 0) {
                        if (client.onDownloadListener != null) {
                            client.onDownloadListener.onDisconnected();
                            client.isConnected = false;
                        }
                    }
                    Log.d(TAG, message);
                    client.unbind();
                    break;
                case DownlaodConstant.MSG_KEY_DOWNLOAD_START_RESP:
                    Log.d(TAG, "Download：onStart");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onStart();
                    }
                    break;
                case DownlaodConstant.MSG_KEY_DOWNLOAD_PROGRESS_RESP:
                    Log.d(TAG, "Download：onProgress：");
                    long max = data.getLong("max");
                    long progress = data.getLong("progress");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onProgress(max, progress);
                    }
                    break;
                case DownlaodConstant.MSG_KEY_DOWNLOAD_PAUSE_RESP:
                    Log.d(TAG, "Download：onPause");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onPause();
                    }
                    break;
                case DownlaodConstant.MSG_KEY_DOWNLOAD_CANCEL_RESP:
                    Log.d(TAG, "Download：onCancel");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onCancel();
                    }
                    break;
                case DownlaodConstant.MSG_KEY_DOWNLOAD_ERROR_RESP:
                    Log.d(TAG, "Download：onError");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onError(new DownlaodException());
                    }
                    break;
                case DownlaodConstant.MSG_KEY_DOWNLOAD_COMPLETE_RESP:
                    Log.d(TAG, "Download：onComplete");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onComplete();
                    }
                    break;
                case DownlaodConstant.MSG_KEY_INSTALL_CHECK_RESP:
                    Log.d(TAG, "Install：onCheck");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onCheckInstall();
                    }
                    break;
                case DownlaodConstant.MSG_KEY_INSTALL_START_RESP:
                    Log.d(TAG, "Install：onStart");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onStartInstall();
                    }
                    break;
                case DownlaodConstant.MSG_KEY_INSTALL_CANCEL_RESP:
                    Log.d(TAG, "Install：onCancel");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onCancelInstall();
                    }
                    break;
                case DownlaodConstant.MSG_KEY_INSTALL_ERROR_RESP:
                    Log.d(TAG, "Install：onError");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onErrorInstall(new DownlaodException(code));
                    }
                    break;
                case DownlaodConstant.MSG_KEY_INSTALL_COMPLETE_RESP:
                    Log.d(TAG, "Install：onComplete");
                    if (client.onDownloadListener != null) {
                        client.onDownloadListener.onCompleteInstall();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private class UpgradeServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            server = new Messenger(service);
            connect();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            server = null;
            client = null;
            isConnected = false;
        }
    }

}
