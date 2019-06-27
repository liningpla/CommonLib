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
import com.common.download.downer.DownerCallBack;
import com.common.download.downer.DownerRequest;
import com.common.download.DownerException;
import com.common.download.DownerdUtil;

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

        Downer.init() .setMultithreadEnabled(true)// 是否支持多线程下载（可选）
                      // 线程池大小（可选）
                      .setMultithreadPools(4)
                      .setSupportRange(true)
                      // 文件MD5（可选）
                      .setAutoInstallEnabled(true)
                      // 是否自动删除安装包（可选）
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
                .setTitle("腾讯QQ")
                // 通知栏描述（可选）
                .setDescription("更新通知栏")
                // 下载链接或更新文档链接
                .setUrl("http://gdown.baidu.com/data/wisegame/2965a5c112549eb8/QQ_996.apk")
                // 下载文件存储路径（可选）
                .setStorage(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/com.upgrade.apk"))
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
        });
    }

    private void forceCheckUpdates() {
        String url = "http://s9.pstatp.com/package/apk/aweme/app_aweGW_v6.8.0_e140aa5.apk?v=1560868759";

        Downer.downLoad(new Object()).setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                // 通知栏标题（可选）
                .setTitle("抖音APP")
                // 通知栏描述（可选）
                .setDescription("更新通知栏")
                // 下载链接或更新文档链接
                .setUrl(url)
                // 下载文件存储路径（可选）
                .setStorage(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/com.365.apk"))
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
        });
    }

    private void customerCheckUpdates() {
    }

    private void customerDownloadUpdates() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == DownerdUtil.REQUEST_CODE_WRITE_EXTERNAL_STORAGE ||
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
            if (DownerdUtil.mayRequestExternalStorage(this, true)) {
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
