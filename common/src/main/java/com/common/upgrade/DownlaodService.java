package com.common.upgrade;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.captureinfo.R;
import com.common.upgrade.model.DownlaodOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 */

@SuppressWarnings("deprecation")
public class DownlaodService extends Service {
    private static final String TAG = DownlaodManager.TAG;

    /**
     * 连接超时时长
     */
    public static final int CONNECT_TIMEOUT = 60 * 1000;

    /**
     * 读取超时时长
     */
    public static final int READ_TIMEOUT = 60 * 1000;

    /**
     * 下载开始
     */
    public static final int STATUS_DOWNLOAD_START = 0x1001;

    /**
     * 下载进度
     */
    public static final int STATUS_DOWNLOAD_PROGRESS = 0x1002;

    /**
     * 下载暂停
     */
    public static final int STATUS_DOWNLOAD_PAUSE = 0x1003;

    /**
     * 下载取消
     */
    public static final int STATUS_DOWNLOAD_CANCEL = 0x1004;

    /**
     * 下载错误
     */
    public static final int STATUS_DOWNLOAD_ERROR = 0x1005;

    /**
     * 下载完成
     */
    public static final int STATUS_DOWNLOAD_COMPLETE = 0x1006;

    /**
     * 安装效验
     */
    public static final int STATUS_INSTALL_CHECK = 0x2001;

    /**
     * 安装开始
     */
    public static final int STATUS_INSTALL_START = 0x2002;
    /**
     * 安装错误
     */
    public static final int STATUS_INSTALL_ERROR = 0x2004;

    /**
     * 安装完成
     */
    public static final int STATUS_INSTALL_COMPLETE = 0x2005;

    /**
     * 通知栏ID
     */
    public static final int NOTIFY_ID = 0x6710;

    /**
     * 延时
     */
    public static final int DELAY = 200;

    /**
     * 升级进度通知栏
     */
    private Notification.Builder builder;

    /**
     * 升级进度通知栏管理
     */
    private NotificationManager notificationManager;

    /**
     * 升级选项
     */
    public DownlaodOptions downlaodOptions;


    /**
     * 调度线程
     */
    private ScheduleThread scheduleThread;

    /**
     * 消息处理
     */
    public MessageHandler messageHandler;

    /**
     * 网络状态变化广播
     */
    private NetWorkStateReceiver netWorkStateReceiver;

    /**
     * 网络状态变化广播
     */
    private PackagesReceiver packagesReceiver;

    /**
     * 服务端
     */
    private Messenger server;

    /**
     * 客服端
     */
    private List<Messenger> clients;

    /**
     * 双击取消标记
     */
    private boolean isCancel;

    /**
     * 状态
     */
    public static volatile int status;
    /**
     * 启动
     *
     * @param context
     * @param options 升级选项
     */
    public static void start(Context context, DownlaodOptions options) {
        start(context, options, null);
    }

    /**
     * 启动
     *
     * @param context    Context
     * @param options    升级选项
     * @param connection 升级服务连接
     */
    public static void start(Context context, DownlaodOptions options, ServiceConnection connection) {
        if (context == null) {
            throw new IllegalArgumentException("Context can not be null");
        }

        if (options == null) {
            throw new IllegalArgumentException("UpgradeOption can not be null");
        }

        Intent intent = new Intent(context, DownlaodService.class);
        intent.putExtra("upgrade_option", options);
        if (!DownlaodUtil.isServiceRunning(context, DownlaodService.class.getName())) {
            context.startService(intent);
        }
        if (connection != null) {
            context.bindService(intent, connection, Context.BIND_ABOVE_CLIENT);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int command = super.onStartCommand(intent, flags, startId);
        if (status == STATUS_DOWNLOAD_START || status == STATUS_DOWNLOAD_PROGRESS) {
            pause();
            return command;
        }
        if (status == STATUS_DOWNLOAD_PAUSE) {
            if (!isCancel) {
                isCancel = true;
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        isCancel = false;
                        resume();
                    }
                }, 2000L);
            } else {
                cancel();
            }
            return command;
        }
        if (status == STATUS_DOWNLOAD_ERROR) {
            resume();
            return command;
        }
        if (status == STATUS_DOWNLOAD_COMPLETE) {
            complete();
            return command;
        }
        initDownlaod(intent);
        return command;
    }

    /***初始化下载*/
    private void initDownlaod(Intent intent){
        DownlaodOptions upgradeOptions = intent.getParcelableExtra("upgrade_option");
        if (upgradeOptions != null) {
            this.downlaodOptions = new DownlaodOptions.Builder()
                    .setIcon(upgradeOptions.getIcon() == null ?
                            DownlaodUtil.getAppIcon(this) : upgradeOptions.getIcon())
                    .setTitle(upgradeOptions.getTitle() == null ?
                            DownlaodUtil.getAppName(this) : upgradeOptions.getTitle())
                    .setDescription(upgradeOptions.getDescription())
                    .setStorage(upgradeOptions.getStorage() == null ?
                            new File(Environment.getExternalStorageDirectory(),
                                    getPackageName() + ".apk") : upgradeOptions.getStorage())
                    .setUrl(upgradeOptions.getUrl())
                    .setMd5(upgradeOptions.getMd5())
                    .setMultithreadEnabled(upgradeOptions.isMultithreadEnabled())
                    .setMultithreadPools(upgradeOptions.isMultithreadEnabled() ?
                            upgradeOptions.getMultithreadPools() == 0 ? 100 :
                                    upgradeOptions.getMultithreadPools() : 0)
                    .setAutocleanEnabled(upgradeOptions.isAutocleanEnabled())
                    .build();
            initNotify();
            start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messageHandler != null) {
            messageHandler.removeCallbacksAndMessages(null);
        }
        if (netWorkStateReceiver != null) {
            netWorkStateReceiver.unregisterReceiver(this);
        }
        if (packagesReceiver != null) {
            packagesReceiver.unregisterReceiver(this);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return server.getBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.d(TAG, "onRebind");
        messageHandler.sendEmptyMessageDelayed(STATUS_DOWNLOAD_PROGRESS, DELAY);
        Message msg = Message.obtain();
        msg.what = status;
        msg.arg1 = -1;
        messageHandler.sendMessageDelayed(msg, DELAY);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    /**
     * 初始化
     */
    private void init() {
        if (server == null) {
            server = new Messenger(ServeHanlder.create(this));
        }

        if (clients == null) {
            clients = new CopyOnWriteArrayList<>();
        }

        if (messageHandler == null) {
            messageHandler = new MessageHandler(this);
        }

        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
            netWorkStateReceiver.registerReceiver(this);
        }

        if (packagesReceiver == null) {
            packagesReceiver = new PackagesReceiver();
            packagesReceiver.registerReceiver(this);
        }
    }

    /**
     * 初始化通知栏
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initNotify() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(String.valueOf(NOTIFY_ID),
                    downlaodOptions.getTitle(), NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.setShowBadge(false);
            channel.setSound(null, null);
            channel.setVibrationPattern(null);
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(this, String.valueOf(NOTIFY_ID))
                    .setGroup(String.valueOf(NOTIFY_ID))
                    .setGroupSummary(false)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setLargeIcon(downlaodOptions.getIcon())
                    .setContentIntent(getDefalutIntent(PendingIntent.FLAG_UPDATE_CURRENT))
                    .setContentTitle(downlaodOptions.getTitle())
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setDefaults(Notification.FLAG_AUTO_CANCEL);
        } else {
            builder = new Notification.Builder(this)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setLargeIcon(downlaodOptions.getIcon())
                    .setContentIntent(getDefalutIntent(PendingIntent.FLAG_UPDATE_CURRENT))
                    .setContentTitle(downlaodOptions.getTitle())
                    .setWhen(System.currentTimeMillis())
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setDefaults(Notification.FLAG_AUTO_CANCEL);
        }
    }

    /**
     * 设置通知栏
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setNotify(String description) {
        if (status == STATUS_DOWNLOAD_START) {
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
        } else if (status == STATUS_DOWNLOAD_PROGRESS) {
            int offset = (scheduleThread != null)?scheduleThread.offset:0;
            builder.setProgress(100, offset, false);
            builder.setSmallIcon(android.R.drawable.stat_sys_download);
        } else {
            builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        }
        builder.setContentText(description);
        notificationManager.notify(NOTIFY_ID, builder.build());
    }

    /**
     * 清除通知栏
     */
    private void clearNotify() {
        notificationManager.cancel(NOTIFY_ID);
    }

    /**
     * 通知栏意图
     *
     * @param flags
     * @return
     */
    private PendingIntent getDefalutIntent(int flags) {
        Intent intent = new Intent(this, DownlaodService.class);
        return PendingIntent.getService(this, 0, intent, flags);
    }

    /**
     * 删除安装包
     *
     * @return
     */
    private boolean deletePackage() {
        File packageFile = downlaodOptions.getStorage();
        if (packageFile.exists()) {
            return packageFile.delete();
        }
        return false;
    }

    /**
     * 安装
     */
    private void install() {
        Thread thread = new InstallThread();
        thread.start();
    }

    /**
     * 开始
     */
    private void start() {
        if (scheduleThread != null) {
            if (scheduleThread.isAlive() || !scheduleThread.isInterrupted()) {
                status = STATUS_DOWNLOAD_CANCEL;
            }
            scheduleThread = null;
        }
        scheduleThread = new ScheduleThread(this, downlaodOptions, new ScheduleThread.ScheduleListener() {
            @Override
            public void downLoadStart() {
                //下载开始，通知DownLoadService更新notify
                status = STATUS_DOWNLOAD_START;
                messageHandler.sendEmptyMessage(status);
            }

            @Override
            public void downLoadProgress(long max, long progress) {
                //下载中，通知DownLoadService更新notify
                status = STATUS_DOWNLOAD_PROGRESS;
                Bundle response = new Bundle();
                response.putLong("max", max);
                response.putLong("progress", progress);
                Message message = Message.obtain();
                message.what = status;
                message.setData(response);
                messageHandler.sendMessage(message);
            }

            @Override
            public void downLoadError() {
                //下载失败，通知DownLoadService更新notify
                status = STATUS_DOWNLOAD_ERROR;
                messageHandler.sendEmptyMessage(status);
            }

            @Override
            public void downLoadComplete() {
                //下载完成，通知DownLoadService更新notify
                status = STATUS_DOWNLOAD_COMPLETE;
                messageHandler.sendEmptyMessage(status);
            }

            @Override
            public void downLoadCancel() {
                //下载取消，通知DownLoadService更新notify
                messageHandler.sendEmptyMessage(status);
            }

            @Override
            public void downLoadPause() {
                messageHandler.sendEmptyMessage(status);
            }
        });
        scheduleThread.start();
    }

    /**
     * 暂停
     */
    private void pause() {
        status = STATUS_DOWNLOAD_PAUSE;
    }

    /**
     * 继续
     */
    private void resume() {
        status = STATUS_DOWNLOAD_START;
        start();
    }

    /**
     * 取消
     */
    private void cancel() {
        status = STATUS_DOWNLOAD_CANCEL;
    }

    /**
     * 下载完成
     */
    private void complete() {
        status = STATUS_DOWNLOAD_COMPLETE;
        clearNotify();
        install();
    }

    /**
     * 发送消息到客户端
     *
     * @param key
     * @param data
     */
    private void sendMessageToClient(int key, Bundle data) {
        Iterator<Messenger> iterator = clients.iterator();
        Messenger client = null;
        while (iterator.hasNext()) {
            client = iterator.next();
            if (client == null) {
                iterator.remove();
                continue;
            }
            sendMessageToClient(client, key, data);
        }
    }

    /**
     * 发送消息到客户端
     *
     * @param client
     * @param key
     * @param data
     */
    private void sendMessageToClient(Messenger client, int key, Bundle data) {
        try {
            Message message = Message.obtain();
            message.replyTo = server;
            message.what = key;
            message.setData(data == null ? new Bundle() : data);
            client.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务端消息
     */
    private static class ServeHanlder extends Handler {
        private SoftReference<DownlaodService> reference;

        private static Handler create(DownlaodService service) {
            HandlerThread thread = new HandlerThread("Messenger");
            thread.start();
            return new ServeHanlder(thread.getLooper(), service);
        }

        private ServeHanlder(Looper looper, DownlaodService service) {
            super(looper);
            this.reference = new SoftReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            DownlaodService service = reference.get();
            if (service == null) {
                return;
            }
            Messenger clint = msg.replyTo;
            Bundle response = new Bundle();
            switch (msg.what) {
                case DownlaodConstant.MSG_KEY_CONNECT_REQ:
                    if (!service.clients.contains(clint)) {
                        service.clients.add(clint);
                        response.putInt("code", 0);
                        response.putString("message",
                                service.getString(R.string.message_connect_success));
                    } else {
                        response.putInt("code", DownlaodException.ERROR_CODE_UNKNOWN);
                        response.putString("message",
                                service.getString(R.string.message_connect_failure));
                    }
                    service.sendMessageToClient(clint, DownlaodConstant.MSG_KEY_CONNECT_RESP, response);
                    break;
                case DownlaodConstant.MSG_KEY_DISCONNECT_REQ:
                    boolean success = service.clients.remove(clint);
                    if (success) {
                        response.putInt("code", 0);
                        response.putString("message",
                                service.getString(R.string.message_disconnect_success));
                    } else {
                        response.putInt("key", DownlaodException.ERROR_CODE_UNKNOWN);
                        response.putString("message",
                                service.getString(R.string.message_disconnect_failure));
                    }
                    service.sendMessageToClient(clint, DownlaodConstant.MSG_KEY_DISCONNECT_RESP, response);
                    break;
                case DownlaodConstant.MSG_KEY_DOWNLOAD_PAUSE_REQ:
                    service.pause();
                    break;
                case DownlaodConstant.MSG_KEY_DOWNLOAD_RESUME_REQ:
                    service.resume();
                    break;
                case DownlaodConstant.MSG_KEY_INSTALL_START_REQ:
                    service.install();
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * 下载消息处理
     */
    private static class MessageHandler extends Handler {
        private WeakReference<DownlaodService> reference;

        private MessageHandler(DownlaodService service) {
            reference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            DownlaodService service = reference.get();
            if (service == null) {
                return;
            }
            Bundle response = new Bundle();
            switch (msg.what) {
                case STATUS_DOWNLOAD_START:
                    service.setNotify(service.getString(R.string.message_download_start));
                    service.sendMessageToClient(DownlaodConstant.MSG_KEY_DOWNLOAD_START_RESP, response);
                    break;
                case STATUS_DOWNLOAD_PROGRESS:
                    long max = msg.getData().getLong("max");
                    long progress = msg.getData().getLong("progress");
                    service.setNotify(DownlaodUtil.formatByte(progress) + "/" +DownlaodUtil.formatByte(max));
                    response.putLong("max", max);
                    response.putLong("progress", progress);
                    service.sendMessageToClient(DownlaodConstant.MSG_KEY_DOWNLOAD_PROGRESS_RESP, response);
                    break;
                case STATUS_DOWNLOAD_PAUSE:
                    service.setNotify(service.getString(R.string.message_download_pause));
                    service.sendMessageToClient(DownlaodConstant.MSG_KEY_DOWNLOAD_PAUSE_RESP, response);
                    break;
                case STATUS_DOWNLOAD_CANCEL:
                    service.setNotify(service.getString(R.string.message_download_cancel));
                    service.sendMessageToClient(DownlaodConstant.MSG_KEY_DOWNLOAD_CANCEL_RESP, response);
                    break;
                case STATUS_DOWNLOAD_ERROR:
                    service.setNotify(service.getString(R.string.message_download_error));
                    response.putInt("code", DownlaodException.ERROR_CODE_UNKNOWN);
                    service.sendMessageToClient(DownlaodConstant.MSG_KEY_DOWNLOAD_ERROR_RESP, response);
                    break;
                case STATUS_DOWNLOAD_COMPLETE:
                    service.setNotify(service.getString(R.string.message_download_complete));
                    service.sendMessageToClient(DownlaodConstant.MSG_KEY_DOWNLOAD_COMPLETE_RESP, response);
                    if (msg.arg1 != -1) {
                        service.install();
                    }
                    break;
                case STATUS_INSTALL_CHECK:
                    service.setNotify(service.getString(R.string.message_install_check));
                    service.sendMessageToClient(DownlaodConstant.MSG_KEY_INSTALL_CHECK_RESP, response);
                    break;
                case STATUS_INSTALL_START:
                    service.setNotify(service.getString(R.string.message_install_start));
                    service.sendMessageToClient(DownlaodConstant.MSG_KEY_INSTALL_START_RESP, response);
                    break;
                case STATUS_INSTALL_ERROR:
                    service.setNotify(service.getString(R.string.message_install_error));
                    response.putInt("code", DownlaodException.ERROR_CODE_UNKNOWN);
                    service.sendMessageToClient(DownlaodConstant.MSG_KEY_INSTALL_ERROR_RESP, response);
                    break;
                case STATUS_INSTALL_COMPLETE:
                    service.setNotify(service.getString(R.string.message_install_complete));
                    if (msg.arg1 == -1 && service.deletePackage()) {
                        Toast.makeText(service, service.getString(
                                R.string.message_install_package_delete), Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 安装线程
     */
    private class InstallThread extends Thread {
        @Override
        public void run() {
            super.run();

            try {
                if (downlaodOptions.getMd5() != null) {
                    status = STATUS_INSTALL_CHECK;
                    messageHandler.sendEmptyMessage(STATUS_INSTALL_CHECK);
                    if (!check()) {
                        status = STATUS_INSTALL_ERROR;
                        Message message = new Message();
                        message.what = status;
                        message.arg1 = DownlaodException.ERROR_CODE_PACKAGE_INVALID;
                        messageHandler.sendMessage(message);
                        return;
                    }
                }
                DownlaodUtil.installApk(DownlaodService.this, downlaodOptions.getStorage().getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 检测文件完整性
         *
         * @return
         */
        @SuppressWarnings("TryFinallyCanBeTryWithResources")
        private boolean check() throws IOException {
            MessageDigest messageDigest = null;
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(downlaodOptions.getStorage());
                messageDigest = MessageDigest.getInstance("MD5");
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = fileInputStream.read(buffer)) != -1) {
                    messageDigest.update(buffer, 0, len);
                }
                BigInteger bigInteger = new BigInteger(1, messageDigest.digest());
                return TextUtils.equals(bigInteger.toString(), downlaodOptions.getMd5());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            }
            return false;
        }
    }

    /**
     * 网络状态变化广播
     */
    private class NetWorkStateReceiver extends BroadcastReceiver {

        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobileNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            // WIFI已连接，移动数据已连接
            if (wifiNetworkInfo.isConnected() && mobileNetworkInfo.isConnected()) {
                if (status == STATUS_DOWNLOAD_PAUSE) {
                    start();
                }
                return;
            }

            // WIFI已连接，移动数据已断开
            if (wifiNetworkInfo.isConnected() && !mobileNetworkInfo.isConnected()) {
                if (status == STATUS_DOWNLOAD_PAUSE) {
                    start();
                }
                return;
            }

            // WIFI已断开，移动数据已连接
            if (!wifiNetworkInfo.isConnected() && mobileNetworkInfo.isConnected()) {
                if (status == STATUS_DOWNLOAD_PAUSE) {
                    start();
                }
                return;
            }

            // WIFI已断开，移动数据已断开
            pause();
        }

        public void registerReceiver(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(this, intentFilter);
        }

        public void unregisterReceiver(Context context) {
            context.unregisterReceiver(this);
        }
    }

    /**
     * 程序状态变化广播
     */
    private class PackagesReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName = null;
            if (intent.getData() != null) {
                packageName = intent.getData().getSchemeSpecificPart();
            }

            String action = intent.getAction();
            if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                Log.i(TAG, "onReceive：Added " + packageName);
            } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
                Log.i(TAG, "onReceive：Replaced " + packageName);

                status = STATUS_INSTALL_COMPLETE;
                Message message = Message.obtain();
                message.what = status;
                if (downlaodOptions.isAutocleanEnabled()) {
                    message.arg1 = -1;
                }
                messageHandler.sendMessage(message);
            } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                Log.i(TAG, "onReceive：Removed " + packageName);
            }
        }

        public void registerReceiver(Context context) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
            intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            intentFilter.addDataScheme("package");
            context.registerReceiver(this, intentFilter);
        }

        public void unregisterReceiver(Context context) {
            context.unregisterReceiver(this);
        }
    }

}
