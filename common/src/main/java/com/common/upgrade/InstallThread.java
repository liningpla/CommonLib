package com.common.upgrade;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.common.upgrade.model.DownlaodOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

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
            if (downlaodOptions.getMd5() != null) {
                downerRequest.status = Downer.STATUS_INSTALL_CHECK;
                if(downerCallBack!=null){
                    downerCallBack.onCheckInstall();
                    Log.i(Downer.TAG, "InstallThread:run:Schedule install check");
                }
                if (!check()) {
                    downerRequest.status = Downer.STATUS_INSTALL_ERROR;
                    if(downerCallBack!=null){
                        downerCallBack.onErrorInstall(new DownlaodException());
                        Log.i(Downer.TAG, "InstallThread:run:Schedule install error");
                    }
                    return;
                }
            }
            if(downerCallBack!=null){
                downerCallBack.onStartInstall();
                Log.i(Downer.TAG, "InstallThread:run:Schedule install start ");
            }
            DownlaodUtil.installApk(mContext, downlaodOptions.getStorage().getPath());
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
