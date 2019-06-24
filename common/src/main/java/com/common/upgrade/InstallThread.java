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
            if(check()){
                downerRequest.apkPageName = (String) DownlaodUtil.getApkInfo(mContext, downlaodOptions.getStorage().getPath()).get("packageName");
                DownlaodUtil.installApk(mContext, downlaodOptions.getStorage().getPath());
            }
        } catch (IOException e) {
            downerRequest.status = Downer.STATUS_INSTALL_ERROR;
            if(downerCallBack!=null){
                downerCallBack.onErrorInstall(new DownlaodException(ERROR_CODE_PACKAGE_INVALID));
                Log.i(Downer.TAG, "InstallThread:run:Schedule install  md5 check error");
            }
        }
    }

    /**
     * 检测文件完整性
     *
     * @return
     */
    private boolean check() throws IOException {
        MessageDigest messageDigest = null;
        FileInputStream fileInputStream = null;
        try {
            downerRequest.status = Downer.STATUS_INSTALL_CHECK;
            if(downerCallBack!=null){
                downerCallBack.onCheckInstall();
                Log.i(Downer.TAG, "InstallThread:run:Schedule install check");
            }
            File file = new File(downlaodOptions.getStorage().getPath());
            if (!file.exists()) {
                if(downerCallBack!=null){
                    downerCallBack.onErrorInstall(new DownlaodException(ERROR_CODE_PACKAGE_FILE));
                    Log.i(Downer.TAG, "InstallThread:installApk：file is not exists");
                }
                return false;
            }
            if (downlaodOptions.getMd5() != null) {
                fileInputStream = new FileInputStream(downlaodOptions.getStorage());
                messageDigest = MessageDigest.getInstance("MD5");
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = fileInputStream.read(buffer)) != -1) {
                    messageDigest.update(buffer, 0, len);
                }
                BigInteger bigInteger = new BigInteger(1, messageDigest.digest());
                boolean isMd5 = TextUtils.equals(bigInteger.toString(), downlaodOptions.getMd5());
                if(!isMd5){//true
                    downerRequest.status = Downer.STATUS_INSTALL_ERROR;
                    if(downerCallBack!=null){
                        downerCallBack.onErrorInstall(new DownlaodException(ERROR_CODE_PACKAGE_INVALID));
                        Log.i(Downer.TAG, "InstallThread:run:Schedule install  md5 check error");
                    }
                }
                return isMd5;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
        }
        return true;
    }
}
