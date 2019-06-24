package com.common.upgrade;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.common.upgrade.model.DownlaodOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

import static com.common.upgrade.DownlaodException.ERROR_CODE_PACKAGE_FILE;
import static com.common.upgrade.DownlaodException.ERROR_CODE_PACKAGE_INVALID;

public class InstallThread extends Thread {
    private Context mContext;
    /**下载请求信息类*/
    private DownerRequest downerRequest;
    /**下载参数类*/
    private DownlaodOptions downlaodOptions;
    /**下载请求状态返回，用来通知外部调用者，有可能为空，需要非空判断*/
    private DownerCallBack downerCallBack;
    public InstallThread(ScheduleRunable scheduleRunable){
        this.downlaodOptions = scheduleRunable.downlaodOptions;
        this.mContext = scheduleRunable.mContext;
        this.downerRequest = scheduleRunable.downerRequest;
        this.downerCallBack = scheduleRunable.downerCallBack;
    }
    @Override
    public void run() {
        super.run();
        try {
            if(downerCallBack!=null){
                downerCallBack.onStartInstall();
                Log.i(Downer.TAG, "InstallThread:run:Schedule install start ");
            }
            if (downlaodOptions.getMd5() != null && !check()) {
                downerRequest.status = Downer.STATUS_INSTALL_ERROR;
                if(downerCallBack!=null){
                    downerCallBack.onErrorInstall(new DownlaodException(ERROR_CODE_PACKAGE_INVALID));
                    Log.i(Downer.TAG, "InstallThread:run:Schedule install  md5 check error");
                }
                return;
            }
            String filePath = downlaodOptions.getStorage().getPath();
            File file = new File(filePath);
            if (!file.exists() && downerCallBack!=null) {
                downerRequest.status = Downer.STATUS_INSTALL_ERROR;
                downerCallBack.onErrorInstall(new DownlaodException(ERROR_CODE_PACKAGE_FILE));
                Log.i(Downer.TAG, "DownlaodUtil:installApk：file is not exists");
                return;
            }
            downerRequest.apkPageName = (String) DownlaodUtil.getApkInfo(mContext, filePath).get("packageName");
            DownlaodUtil.installApk(mContext, filePath);
        } catch (IOException e) {
            downerRequest.status = Downer.STATUS_INSTALL_ERROR;
            if(downerCallBack!=null){
                downerCallBack.onErrorInstall(new DownlaodException());
                Log.i(Downer.TAG, "InstallThread:run:Exception:Schedule install error");
            }
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
            downerRequest.status = Downer.STATUS_INSTALL_CHECK;
            if(downerCallBack!=null){
                downerCallBack.onCheckInstall();
                Log.i(Downer.TAG, "InstallThread:run:Schedule install check");
            }
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
