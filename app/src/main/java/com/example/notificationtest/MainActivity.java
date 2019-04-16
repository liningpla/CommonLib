package com.example.notificationtest;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.common.BaseAcivity;
import com.common.log.SDLog;
import com.example.notificationtest.httplib.TestManager;


public class MainActivity extends BaseAcivity {

    private TextView tv_countdown;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        tv_countdown = findViewById(R.id.tv_countdown);

        SDLog.i("lining","启动MainActivity 线程ID："+Thread.currentThread().getId());
//        PauseAbleCountDownTimer countDownTimer = new PauseAbleCountDownTimer(1000*1000, 10*1000, true) {
//            @Override
//            public void onTick(final long millisUntilFinished) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        tv_countdown.setText("倒计时剩余时间："+millisUntilFinished);
//                    }
//                });
//                SDLog.create().i("lining","test","启动MainActivity" +millisUntilFinished+" 当前线程ID："+Thread.currentThread().getId());
//            }
//
//            @Override
//            public void onFinish() {
//
//            }
//        };
//
//        countDownTimer.start();

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
//        SDLog.create().i("app","MainActivity","--intentCommon--");
//        Uri uri= Uri.parse("common://common.com/commonactivity");
//        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
//        startActivity(intent);

//        SDLog.i("--intentCommo ---  FloatingWindowActivityn--");
//        ContextManager.intentUri(this, FloatingWindowActivity.URI);
//        finish();


//        ContextManager.intentUri(this, KTActivity.Companion.getURI());
//        startActivity(new Intent(this, SetInfoActivity.class));

//        startActivity(new Intent(this, PullRefreshActivity.class));

//        RxjavaBiz.INSTANCE.testRxjava();
//        RxjavaBiz.INSTANCE.doRxJava();

//        JobManager.INSTANCE.initJobService(this);

//        JobManager.INSTANCE.testScreen(this, view);

        TestManager.instance.testPostHttp();

    }

}
