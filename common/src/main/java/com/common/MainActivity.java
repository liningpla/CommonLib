package com.common;

import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.captureinfo.R;
import com.common.download.Downer;
import com.common.download.DownerException;
import com.common.download.DownerUtil;
import com.common.download.downer.DownerCallBack;
import com.common.download.downer.DownerRequest;

import java.io.File;

/**
 * 支持：断点续传
 * 支持：暂停、取消
 * 支持：分流下载
 * 支持：动态网络监听下载
 * 支持：8.0 适配
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = Downer.TAG;
    private DownerRequest downerRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.button_check_updates_default_common).setOnClickListener(this);
        findViewById(R.id.button_check_updates_default_forced).setOnClickListener(this);
        findViewById(R.id.button_check_updates_default_bate).setOnClickListener(this);
        findViewById(R.id.button_check_updates_custom).setOnClickListener(this);
        findViewById(R.id.button_check_updates_custom_download).setOnClickListener(this);
        findViewById(R.id.button_cancle).setOnClickListener(this);

        Downer.init().setMultithreadEnabled(true)// 是否支持多线程下载
                      // 线程池大小
                      .setMultithreadPools(1)
                      .setSupportRange(false)
                      // 文件MD5（可选）
                      .setAutoInstallEnabled(true)
                      //覆盖下载
                      .setOverride(false)
                      .needNotify(true)
                      // 是否自动删除安装包
                      .setAutocleanEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void getObject(){



    }

    private void checkUpdates() {
        Downer.downLoad(new Object()).setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                // 通知栏标题（可选）
                .setTitle("tourCustomer")
                // 通知栏描述（可选）
                .setDescription("tourCustomer更新通知栏")
                // 下载链接或更新文档链接
                .setUrl("http://imtt.dd.qq.com/16891/apk/26B2754677EA904F8A19C82E80605134.apk?fsname=com.shuqi.controller_11.2.7.125_206.apk&csr=a24f")
                // 下载文件存储路径（可选）
                .setStorage(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/qq.apk"))
                // 文件MD5（可选）
                .setMd5(null).execute(MainActivity.this, new DownerCallBack() {
            @Override
            public void onConnected(DownerRequest request) {
                downerRequest = request;
            }
            @Override
            public void onProgress(long max, long progress) {
            }
            @Override
            public void onStop(Object model, DownerException e) {
            }
            @Override
            public void onComplete(Object model) {

            }
            @Override
            public void onCompleteInstall(Object model) {
                Log.i(Downer.TAG, "MainActivity:  onCompleteInstall is Complete Install");
            }
        });
    }

    private void forceCheckUpdates() {
        String url = "https://8496e715f8c254b4cf90d53453d10e9e.dlied1.cdntips.net/dlied1.qq.com/qqweb/QQ_1/android_apk/Android_8.5.0.5025_537066738.apk?mkey=5fe326f76fcd0d12&f=5807&cip=111.205.43.231&proto=https&access_type=$header_ApolloNet";
        //http://pr1-greentea-test.mbgcdn.lenovo.com.cn/default/2020/08/11/e3a2a3d6-b8a3-4ca4-b707-64fa8ecc3e55.apk
//        String url = "http://pr1-greentea-test.mbgcdn.lenovo.com.cn/default/2020/08/11/e3a2a3d6-b8a3-4ca4-b707-64fa8ecc3e55.apk";
        Downer.downLoad(new Object()).setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                // 通知栏标题（可选）
                .setTitle("QQ")
                // 通知栏描述（可选）
                .setDescription("更新通知栏")
                // 下载链接或更新文档链接
                .setUrl(url)
                // 下载文件存储路径（可选）
                .setStorage(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/QQ.apk"))
                // 文件MD5（可选）
                .setMd5(null).execute(MainActivity.this, new DownerCallBack() {
            @Override
            public void onConnected(DownerRequest request) {
            }
            @Override
            public void onProgress(long max, long progress) {

            }

            @Override
            public void onStop(Object model, DownerException e) {

            }

            @Override
            public void onComplete(Object model) {
            }
            @Override
            public void onCompleteInstall(Object model) {
                Log.i(Downer.TAG, "MainActivity:  onCompleteInstall is Complete Install");
            }
        });
    }

    private void customerCheckUpdates() {
        Downer.downLoad(new Object()).setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                // 通知栏标题（可选）
                .setTitle("微信")
                // 通知栏描述（可选）
                .setDescription("更新通知栏")
                .setMultithreadPools(1)
                .setSupportRange(true)
                // 下载链接或更新文档链接
                .setUrl("http://dldir1.qq.com/weixin/android/weixin704android1420.apk")
                // 下载文件存储路径（可选）
                .setStorage(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/weixin.apk"))
                // 文件MD5（可选）
                .setMd5(null).execute(MainActivity.this, new DownerCallBack() {
            @Override
            public void onConnected(DownerRequest request) {
                downerRequest = request;
            }
            @Override
            public void onProgress(long max, long progress) {
            }
            @Override
            public void onStop(Object model, DownerException e) {
            }
            @Override
            public void onComplete(Object model) {

            }
            @Override
            public void onCompleteInstall(Object model) {
                Log.i(Downer.TAG, "MainActivity:  onCompleteInstall is Complete Install");
            }
        });
    }

    private void customerDownloadUpdates() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == DownerUtil.REQUEST_CODE_WRITE_EXTERNAL_STORAGE ||
                grantResults.length == 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            customerDownloadUpdates();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_check_updates_default_common) {
            checkUpdates();
        }
        if (v.getId() == R.id.button_check_updates_default_forced) {
            forceCheckUpdates();
        }
        if (v.getId() == R.id.button_check_updates_custom) {
            customerCheckUpdates();
        }
        if (v.getId() == R.id.button_check_updates_custom_download) {
            if (DownerUtil.mayRequestExternalStorage(this, true)) {
                customerDownloadUpdates();
            }
        }
        if (v.getId() == R.id.button_cancle) {
            if(downerRequest != null){
                downerRequest.cancle();
            }
        }

    }
}
