package com.common;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.captureinfo.R;
import com.common.admob.AdMobActivity;
import com.common.download.thread.PauseTimer;
import com.common.utils.SDLog;

public class CommonActivity extends AppCompatActivity {
    private Button btn_sendTest, btn_close, btn_down, btn_ad;
    private int count;
    NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_main);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        initNofification();
        btn_sendTest = findViewById(R.id.btn_sendTest);
        btn_sendTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer.start();
            }
        });

        btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeNotification();
            }
        });

        btn_down = findViewById(R.id.btn_down);
        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CommonActivity.this, MainActivity.class));
            }
        });
        SDLog.i("CommonActivity","--onCreate--");
        btn_ad = findViewById(R.id.btn_ad);
        btn_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CommonActivity.this, AdMobActivity.class));
            }
        });

    }
    /**10*1000毫秒倒计时，  每隔1000毫秒执行onTick*/
    PauseTimer timer = new PauseTimer(60*10*1000, 5*1000, false) {
        @Override
        public void onTick(long millisUntilFinished) {//阻塞当前线程，如果是主线程，避免处理复杂业务逻辑
            //millisUntilFinished 还剩多少毫秒
            sendMessage();
        }
        @Override
        public void onFinish() {
        }
    };

    private void sendMessage(){
        count ++;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(CommonActivity.this, "subscribe")
                .setContentTitle("收到一条订阅消息："+count)
                .setContentText(count+"：地铁沿线30万商铺抢购中！")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                .setAutoCancel(true)
                .build();
        manager.notify(2, notification);
    }

    private void initNofification(){
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
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setShowBadge(true);
        notificationManager.createNotificationChannel(channel);
    }


    /**关闭通知*/
    public void closeNotification(){
        if(notificationManager != null){
            if (Build.VERSION.SDK_INT>=26){
//                notificationManager.deleteNotificationChannel("subscribe");//channel的id
            }
            notificationManager.cancel(2);// notify 的code

//            notificationManager.cancelAll();
        }

    }
}
