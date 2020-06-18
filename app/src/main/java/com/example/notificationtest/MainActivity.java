package com.example.notificationtest;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.common.BaseAcivity;
import com.common.utils.SDLog;
import com.example.notificationtest.activity.SocketActivity;
import com.example.notificationtest.biz.GooglePlayBiz;
import com.example.notificationtest.manager.ContextManager;
import com.example.notificationtest.manager.StudyLifecycle;
import com.lenove.httplibrary.OkGoManager;

import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;


public class MainActivity extends BaseAcivity {
    private TextView tv_countdown;
    private FrameLayout activity_main_layout;
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

    int i = 0;
    public void sendChatMsg(View view) {
//        i++;
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = manager.getNotificationChannel("chat");
//            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
//                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
//                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId()+i);
//                startActivity(intent);
//                Toast.makeText(this, "请手动将通知打开", Toast.LENGTH_SHORT).show();
//            }
//        }
//        Notification notification = new NotificationCompat.Builder(this, "chat")
//                .setContentTitle("收到一条聊天消息")
//                .setContentText("今天中午吃什么？")
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.drawable.icon)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
//                .setAutoCancel(true)
//                .build();
//        manager.notify(1, notification);
//                ContextManager.intentUri(this, "push://push.com/news_details?p_url=https://www.qq.com/");
        ContextManager.intentUri(this, "push://push.com/news_detail?p_url=http://m.uczzd.cn/webapp/webview/article/news.html?app=greentea-iflow&aid=16488175894108642468&cid=100&zzd_from=uc-iflow&uc_param_str=dndseiwifrvesvntgipf&recoid=&readId=&rd_type=reco&previewdl=1/");

    }

    public void sendSubscribeMsg(View view) {
//        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        Notification notification = new NotificationCompat.Builder(MainActivity.this, "subscribe")
//                .setContentTitle("收到一条订阅消息")
//                .setContentText("地铁沿线30万商铺抢购中！")
//                .setWhen(System.currentTimeMillis())
//                .setSmallIcon(R.drawable.icon)
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))
//                .setAutoCancel(true)
//                .setNumber(2)
//                .build();
//        manager.notify(2, notification);

        ContextManager.intentUri(this, "push://push.com/news_net?p_url=https://www.jd.com/");
    }

    public void intentCommon(View view){
//        Uri uri= Uri.parse("common://common.com/commonactivity");
//        Intent intent=new Intent(Intent.ACTION_VIEW,uri);
//        startActivity(intent);

//        HiLog.i("--intentCommo ---  FloatingWindowActivityn--");
//        ContextManager.intentUri(this, FloatingWindowActivity.URI);
//        finish();


//        ContextManager.intentUri(this, KTActivity.Companion.getURI());
//        startActivity(new Intent(this, SetInfoActivity.class));

        startActivity(new Intent(this, SocketActivity.class));

//        RxjavaBiz.INSTANCE.testRxjava();
//        RxjavaBiz.INSTANCE.doRxJava();

//        JobManager.INSTANCE.initJobService(this);

//        JobManager.INSTANCE.testScreen(this, view);

//        HiHttp.init(getApplication());
//        HiLog.i(" test : "+ TestManager.instance.testPostHttp());
//        HiViewModel.init(getApplication()).observe(this, new Observer() {
//            @Override
//            public void onChanged(Object o) {
//                UserInfo userInfo = (UserInfo) o;
//                HiLog.i(" observe : "+userInfo.body.appName);
//            }
//        });

//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        ComponentName componentName = new ComponentName("com.lenovo.blockchain", "com.lenovo.blockchain.ui.rewarded.SplashActivity");
//        intent.setComponent(componentName);
//        startActivity(intent);

//        GooglePlayBiz.instance.updateGooglePlay(this);

//        testFragment();

//        new LeAboradHomePanelView(this, activity_main_layout).laodView();

//        ContextManager.intentUri(this, "push://push.com/news_net?p_url=https://www.qq.com/");
//        ContextManager.intentUri(this, "push://push.com/small_video?small_id=dm5AZA9xgybW");
    }

    public void intentShort(View view){
        ContextManager.intentUri(this, "push://push.com/short_video?channel=音乐&short_url=https://sh5.yladm.com/html/001/M8v/RJj0g4NnkM8v.html?id=RJj0g4NnkM8v&access_key=yl8zcrb9th5m&udid=6275aeeb9c5d780dc33f1d626f5a35b8&logid=3644750884&imei=6275aeeb9c5d780dc33f1d626f5a35b8&imeimd5=6275aeeb9c5d780dc33f1d626f5a35b8&pkg_name=com.zui.browser&referpage=openv2%2Fvideo%2Ffeed%3F13033&prid=9");
    }
    public void intentUser(View view){
        ContextManager.intentUri(this, "push://push.com/user_center");
    }
    public void intentVideo(View view){
        ContextManager.intentUri(this, "push://push.com/video_home");

    }
    public void intentHome(View view){
        ContextManager.intentUri(this, "push://push.com/news_home");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GooglePlayBiz.MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.i(GooglePlayBiz.TAG, "Update flow failed! Result code: " + resultCode);
                // 如果更新被取消或失败，
                // 您可以请求重新启动更新。
                return;
            }
            if (resultCode != RESULT_CANCELED) {
                //用户已拒绝或取消更新。
                Log.i(GooglePlayBiz.TAG, "The user has denied or cancelled the update! Result code: " + resultCode);
                return;
            }
            if (resultCode != RESULT_IN_APP_UPDATE_FAILED) {
                //其他一些错误阻止用户提供同意或继续更新。
                Log.i(GooglePlayBiz.TAG, " Some other error prevented either the user from providing consent or the update to proceed! Result code: " + resultCode);
                return;
            }
            Log.i(GooglePlayBiz.TAG, "Update flow successed! Result code: " + resultCode);
        }
    }

}
