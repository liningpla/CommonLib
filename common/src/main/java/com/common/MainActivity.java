package com.common;

import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.captureinfo.R;
import com.common.upgrade.Downer;
import com.common.upgrade.DownerCallBack;
import com.common.upgrade.DownlaodClient;
import com.common.upgrade.OnDownloadListener;
import com.common.upgrade.DownlaodException;
import com.common.upgrade.DownlaodManager;
import com.common.upgrade.DownlaodUtil;
import com.common.upgrade.model.DownlaodOptions;

import java.io.File;

/**
 * 支持：断点续传
 * 支持：暂停、取消
 * 支持：分流下载
 * 支持：动态网络监听下载
 * 支持：8.0 适配
 * <p>
 * 更新文档模板路径：../android-upgrade/upgradelibrary/app-update.xml
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = DownlaodManager.TAG;
    private DownlaodManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_main);
        manager = new DownlaodManager(this);
        initView();
    }

    private void initView() {
        findViewById(R.id.button_check_updates_default_common).setOnClickListener(this);
        findViewById(R.id.button_check_updates_default_forced).setOnClickListener(this);
        findViewById(R.id.button_check_updates_default_bate).setOnClickListener(this);
        findViewById(R.id.button_check_updates_custom).setOnClickListener(this);
        findViewById(R.id.button_check_updates_custom_download).setOnClickListener(this);
        findViewById(R.id.button_cancle).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void checkUpdates() {
        Downer.downLoad(this).setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                // 通知栏标题（可选）
                .setTitle("腾讯QQ")
                // 通知栏描述（可选）
                .setDescription("更新通知栏")
                // 下载链接或更新文档链接
                .setUrl("http://gdown.baidu.com/data/wisegame/2965a5c112549eb8/QQ_996.apk")
                // 下载文件存储路径（可选）
                .setStorage(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/com.upgrade.apk"))
                // 是否支持多线程下载（可选）
                .setMultithreadEnabled(true)
                // 线程池大小（可选）
                .setMultithreadPools(1)
                // 文件MD5（可选）
                .setMd5(null)
                .setAutoInstallEnabled(true)
                // 是否自动删除安装包（可选）
                .setAutocleanEnabled(true).execute(new DownerCallBack() {
            @Override
            public void onProgress(long max, long progress) {

            }

            @Override
            public void onError(DownlaodException e) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onErrorInstall(DownlaodException e) {

            }

            @Override
            public void onCompleteInstall() {

            }
        });


//        manager.checkForUpdates(new DownlaodOptions.Builder()
//                .setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
//                // 通知栏标题（可选）
//                .setTitle("腾讯QQ")
//                // 通知栏描述（可选）
//                .setDescription("更新通知栏")
//                // 下载链接或更新文档链接
//                .setUrl("https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk")
//                // 下载文件存储路径（可选）
//                .setStorage(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/com.upgrade.apk"))
//                // 是否支持多线性下载（可选）
//                .setMultithreadEnabled(true)
//                // 线程池大小（可选）
//                .setMultithreadPools(10)
//                // 文件MD5（可选）
//                .setMd5(null)
//                // 是否自动删除安装包（可选）
//                .setAutocleanEnabled(true)
//                .build(), new OnDownloadListener() {
//
//            @Override
//            public void onDownBefore(DownlaodClient downlaodClient) {
//
//            }
//
//            @Override
//            public void onProgress(long max, long progress) {
//
//            }
//
//            @Override
//            public void onError(DownlaodException e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
    }

    private void autoCheckUpdates() {
        manager.checkForUpdates(new DownlaodOptions.Builder()
                .setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                // 通知栏标题（可选）
                .setTitle("腾讯QQ")
                // 通知栏描述（可选）
                .setDescription("更新通知栏")
                // 下载链接或更新文档链接
                .setUrl("http://www.rainen.cn/test/app-update-common.xml")
                // 下载文件存储路径（可选）
                .setStorage(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/com.upgrade.apk"))
                // 是否支持多线性下载（可选）
                .setMultithreadEnabled(true)
                // 线程池大小（可选）
                .setMultithreadPools(1)
                // 文件MD5（可选）
                .setMd5(null)
                // 是否自动删除安装包（可选）
                .setAutocleanEnabled(true)
                .build(), true);
    }

    private void forceCheckUpdates() {
        manager.checkForUpdates(new DownlaodOptions.Builder()
                .setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                // 通知栏标题（可选）
                .setTitle("腾讯QQ")
                // 通知栏描述（可选）
                .setDescription("更新通知栏")
                // 下载链接或更新文档链接
                .setUrl("http://www.rainen.cn/test/app-update-forced.xml")
                // 下载文件存储路径（可选）
                .setStorage(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/com.upgrade.apk"))
                // 是否支持多线程下载（可选）
                .setMultithreadEnabled(true)
                // 线程池大小（可选）
                .setMultithreadPools(10)
                // 文件MD5（可选）
                .setMd5(null)
                // 是否自动删除安装包（可选）
                .setAutocleanEnabled(true)
                .build(), new OnDownloadListener() {

            @Override
            public void onDownBefore(DownlaodClient downlaodClient) {

            }

            @Override
            public void onProgress(long max, long progress) {

            }

            @Override
            public void onError(DownlaodException e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void customerCheckUpdates() {
        manager.checkForUpdates(new DownlaodOptions.Builder()
                .setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setTitle("腾讯QQ")
                .setDescription("更新通知栏")
                .setUrl("http://www.rainen.cn/test/app-update-common.xml")
                .setStorage(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/com.upgrade.apk"))
                .setMultithreadEnabled(true)
                .setMultithreadPools(1)
                .setMd5(null)
                .setAutocleanEnabled(true)
                .build(), new OnDownloadListener() {

            @Override
            public void onDownBefore(DownlaodClient downlaodClient) {

            }

            @Override
            public void onProgress(long max, long progress) {

            }

            @Override
            public void onError(DownlaodException e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void customerDownloadUpdates() {
        manager.checkForUpdates(new DownlaodOptions.Builder()
                .setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                // 通知栏标题（可选）
                .setTitle("腾讯QQ")
                // 通知栏描述（可选）
                .setDescription("更新通知栏")
                // 下载链接或更新文档链接
                .setUrl("http://gdown.baidu.com/data/wisegame/2965a5c112549eb8/QQ_996.apk")
                // 下载文件存储路径（可选）
                .setStorage(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/com.upgrade.apk"))
                // 是否支持多线程下载（可选）
                .setMultithreadEnabled(true)
                // 线程池大小（可选）
                .setMultithreadPools(1)
                // 文件MD5（可选）
                .setMd5(null)
                // 是否自动删除安装包（可选）
                .setAutocleanEnabled(true)
                .build(), new OnDownloadListener() {

            @Override
            public void onDownBefore(DownlaodClient downlaodClient) {

            }

            @Override
            public void onProgress(long max, long progress) {

            }

            @Override
            public void onError(DownlaodException e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == DownlaodUtil.REQUEST_CODE_WRITE_EXTERNAL_STORAGE ||
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
            if (DownlaodUtil.mayRequestExternalStorage(this, true)) {
                customerDownloadUpdates();
            }
        }
        if (v.getId() == R.id.button_cancle) {
            manager.cancel();
        }

    }
}
