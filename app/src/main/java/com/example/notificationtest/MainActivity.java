package com.example.notificationtest;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.common.BaseAcivity;
import com.common.log.SDLog;
import com.example.notificationtest.httplib.HiHttp;
import com.example.notificationtest.httplib.HiLog;
import com.example.notificationtest.httplib.TestManager;
import com.example.notificationtest.manager.StudyLifecycle;
import com.lenove.httplibrary.OkGoManager;


public class MainActivity extends BaseAcivity {
    private TextView tv_countdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getLifecycle().addObserver(new StudyLifecycle());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "chat";
            String channelName = "聊天消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);

            channelId = "subscribe";
            channelName = "订阅消息";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);
        }
        LayoutInflater.from(this).inflate(R.layout.activity_down_main, null, false);

        tv_countdown = findViewById(R.id.tv_countdown);
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String msg = ai.metaData.getString("ID_CHANNEL");
            tv_countdown.setText(msg);
        } catch (PackageManager.NameNotFoundException e) {
        }
        SDLog.i("lining","启动MainActivity 线程ID："+Thread.currentThread().getId());
//        PauseTimer countDownTimer = new PauseTimer(1000*1000, 10*1000, true) {
//            @Override
//            public void onTick(final long millisUntilFinished) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tv_countdown.setText("倒计时剩余时间："+millisUntilFinished);
//                    }
//                });
//                HiLog.create().i("lining","test","启动MainActivity" +millisUntilFinished+" 当前线程ID："+Thread.currentThread().getId());
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        };
//
//        countDownTimer.start();

        OkGoManager.getInstance().initOkGo(getApplication(), null, null);
    }


    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setShowBadge(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public void sendChatMsg(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel("chat");
            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                startActivity(intent);
                Toast.makeText(this, "请手动将通知打开", Toast.LENGTH_SHORT).show();
            }
        }
        Notification notification = new NotificationCompat.Builder(this, "chat")
                .setContentTitle("收到一条聊天消息")
                .setContentText("今天中午吃什么？")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .setAutoCancel(true)
                .build();
        manager.notify(1, notification);
    }

    public void sendSubscribeMsg(View view) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(MainActivity.this, "subscribe")
                .setContentTitle("收到一条订阅消息")
                .setContentText("地铁沿线30万商铺抢购中！")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
                .setAutoCancel(true)
                .setNumber(2)
                .build();
        manager.notify(2, notification);
    }

    public void intentCommon(View view){
        Uri uri= Uri.parse("common://common.com/commonactivity");
        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);

//        HiLog.i("--intentCommo ---  FloatingWindowActivityn--");
//        ContextManager.intentUri(this, FloatingWindowActivity.URI);
//        finish();


//        ContextManager.intentUri(this, KTActivity.Companion.getURI());
//        startActivity(new Intent(this, SetInfoActivity.class));

//        startActivity(new Intent(this, PullRefreshActivity.class));

//        RxjavaBiz.INSTANCE.testRxjava();
//        RxjavaBiz.INSTANCE.doRxJava();

//        JobManager.INSTANCE.initJobService(this);

//        JobManager.INSTANCE.testScreen(this, view);

        HiHttp.init(getApplication());
        HiLog.i(" test : "+ TestManager.instance.testPostHttp());
//        HiViewModel.init(getApplication()).observe(this, new Observer() {
//            @Override
//            public void onChanged(Object o) {
//                UserInfo userInfo = (UserInfo) o;
//                HiLog.i(" observe : "+userInfo.body.appName);
//            }
//        });

//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        ComponentName componentName = new ComponentName("com.lenovo.blockchain", "com.lenovo.blockchain.ui.splash.SplashActivity");
//        intent.setComponent(componentName);
//        startActivity(intent);
    }

}
