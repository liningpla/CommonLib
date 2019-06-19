package com.common;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.captureinfo.R;
import com.common.upgrade.DownLoadClient;
import com.common.upgrade.DownManager;
import com.common.upgrade.OnDownloadListener;
import com.common.upgrade.UpgradeException;
import com.common.upgrade.UpgradeUtil;
import com.common.upgrade.model.bean.DownOptions;
import com.common.upgrade.model.bean.Upgrade;

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
    private static final String TAG = MainActivity.class.getSimpleName();
    private DownManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_main);
        manager = new DownManager(this);
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
        manager.doDownLoad(new DownOptions.Builder()
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
                .setMultithreadPools(10)
                // 文件MD5（可选）
                .setMd5(null)
                // 是否自动删除安装包（可选）
                .setAutocleanEnabled(true)
                .build(), null);
    }

    private void forceCheckUpdates() {
        manager.doDownLoad(new DownOptions.Builder()
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
                .build(), null);
    }

    private void customerCheckUpdates() {
        manager.doDownLoad(new DownOptions.Builder()
                .setIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round))
                .setTitle("腾讯QQ")
                .setDescription("更新通知栏")
                .setUrl("http://www.rainen.cn/test/app-update-common.xml")
                .setStorage(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/com.upgrade.apk"))
                .setMultithreadEnabled(true)
                .setMultithreadPools(1)
                .setMd5(null)
                .setAutocleanEnabled(true)
                .build(), null);
    }

    private void customerDownloadUpdates() {
        manager.doDownLoad(new DownOptions.Builder()
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
                .build(), null);
    }

    /**
     * 显示更新提示（自定义提示）
     *
     * @param stable Upgrade.Stable
     * @param client DownLoadClient
     */
    private void showUpgradeDialog(Upgrade.Stable stable, final DownLoadClient client) {
        StringBuffer logs = new StringBuffer();
        for (int i = 0; i < stable.getLogs().size(); i++) {
            logs.append(stable.getLogs().get(i));
            logs.append(i < stable.getLogs().size() - 1 ? "\n" : "");
        }

        View view = View.inflate(this, R.layout.dialog_custom, null);
        TextView tvMessage = view.findViewById(R.id.tv_dialog_custom_message);
        Button btnUpgrade = view.findViewById(R.id.btn_dialog_custom_upgrade);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                client.remove();
            }
        });

        tvMessage.setText(logs.toString());
        btnUpgrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 开始下载
                if (UpgradeUtil.mayRequestExternalStorage(MainActivity.this, true)) {
                    client.start();
                }
                dialog.dismiss();
            }
        });
        client.setOnDownloadListener(new OnDownloadListener() {

            @Override
            public void onStart() {
                super.onStart();
                Log.d(TAG, "onStart");
            }

            @Override
            public void onProgress(long max, long progress) {
                Log.d(TAG, "onProgress：" + UpgradeUtil.formatByte(progress) + "/" + UpgradeUtil.formatByte(max));
            }

            @Override
            public void onPause() {
                super.onPause();
                Log.d(TAG, "onPause");
            }

            @Override
            public void onCancel() {
                super.onCancel();
                Log.d(TAG, "onCancel");
            }

            @Override
            public void onError(UpgradeException e) {
                Log.d(TAG, "onError");
            }

            @Override
            public void onComplete() {
                dialog.dismiss();
                Log.d(TAG, "onComplete");
            }
        });
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == UpgradeUtil.REQUEST_CODE_WRITE_EXTERNAL_STORAGE ||
                grantResults.length == 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            customerDownloadUpdates();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.button_check_updates_default_common){
            checkUpdates();
        }
        if(id == R.id.button_check_updates_default_forced){
            forceCheckUpdates();
        }
        if(id == R.id.button_check_updates_default_bate){

        }
        if(id == R.id.button_check_updates_custom){
            customerCheckUpdates();
        }
        if(id == R.id.button_check_updates_custom_download){
            if (UpgradeUtil.mayRequestExternalStorage(this, true)) {
                customerDownloadUpdates();
            }
        }
        if(id == R.id.button_cancle){
            manager.cancel();
        }

    }
}
