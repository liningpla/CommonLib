package com.common.download;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.common.download.downer.DownerCallBack;
import com.common.download.downer.DownerRequest;
import com.common.download.downer.ScheduleRunable;
import com.common.download.model.DownerOptions;
import com.common.download.thread.PauseTimer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

import static com.common.download.DownerException.ERROR_CODE_PACKAGE_FILE;
import static com.common.download.DownerException.ERROR_CODE_PACKAGE_INVALID;

public class InstallThread extends Thread {
    private Context mContext;
    /**下载调度类*/
    private ScheduleRunable scheduleRunable;
    /**下载请求信息类*/
    private DownerRequest downerRequest;
    /**下载参数类*/
    private DownerOptions downerOptions;
    /**下载请求状态返回，用来通知外部调用者，有可能为空，需要非空判断*/
    private DownerCallBack downerCallBack;
    private long maxLength;
    /**安装等待时间-5分钟*/
    private final long INSTALL_DELAY_TIME =  3*60*1000;
    /**安装等待间隔上报时间-每隔3秒钟*/
    private final long INATALL_DELAY_SPACE = 3*1000;
    /**安装监听倒计时
     * Service里监听安装状态，但是service放置后台会被系统回收，因此这里另起线程监听下载状态。
     * 设置监听时限可能导致DownRequest无法及时释放，只能时限到达才能释放，
     * 尽管对DownRequest做了软引用处理，但是仍有一定内存占用风险
    * */
    private PauseTimer pauseTimer = new PauseTimer(INSTALL_DELAY_TIME, INATALL_DELAY_SPACE, true) {
        @Override
        public void onTick(long millisUntilFinished) {
        }
        @Override
        public void onFinish() {
            if(DownerUtil.isAppInstalled(mContext, downerRequest.apkPageName)){
                scheduleRunable.completeInstall(downerRequest.apkPageName);
            }
            Log.i(Downer.TAG, "InstallThread:PauseTimer:"+downerRequest.apkPageName+" install status "+DownerUtil.isAppInstalled(mContext, downerRequest.apkPageName));
            pauseTimer.cancel();
        }
    };
    public InstallThread(ScheduleRunable scheduleRunable){
        this.scheduleRunable = scheduleRunable;
        this.downerOptions = scheduleRunable.downerOptions;
        this.mContext = scheduleRunable.mContext;
        this.downerRequest = scheduleRunable.downerRequest;
        this.downerCallBack = scheduleRunable.downerCallBack;
        this.maxLength = scheduleRunable.maxProgress;
    }
    @Override
    public void run() {
        super.run();
        try {
            if(downerCallBack!=null){
                downerCallBack.onStartInstall(downerRequest.getModel());
                Log.i(Downer.TAG, "InstallThread:run:Schedule install start ");
            }
            if(check()){
                downerRequest.apkPageName = (String) DownerUtil.getApkInfo(mContext, downerOptions.getStorage().getPath()).get("packageName");
                DownerUtil.installApk(mContext, downerOptions.getStorage().getPath());
                pauseTimer.start();
            }
        } catch (IOException e) {
            if(downerCallBack!=null){
                downerCallBack.onErrorInstall(downerRequest.getModel(), new DownerException(ERROR_CODE_PACKAGE_INVALID));
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

            File file = new File(downerOptions.getStorage().getPath());
            if (!file.exists()) {
                if(downerCallBack!=null){
                    downerCallBack.onErrorInstall(downerRequest.getModel(), new DownerException(ERROR_CODE_PACKAGE_FILE));
                    Log.i(Downer.TAG, "InstallThread:installApk：file is not exists");
                }
                return false;
            }else{
                if(file.length() != maxLength){
                    return false;
                }
            }
            if(downerCallBack!=null){
                downerCallBack.onCheckInstall(downerRequest.getModel());
                Log.i(Downer.TAG, "InstallThread:run:Schedule install check");
            }
            if (downerOptions.getMd5() != null) {
                fileInputStream = new FileInputStream(downerOptions.getStorage());
                messageDigest = MessageDigest.getInstance("MD5");
                byte[] buffer = new byte[1024];
                int len = -1;
                while ((len = fileInputStream.read(buffer)) != -1) {
                    messageDigest.update(buffer, 0, len);
                }
                BigInteger bigInteger = new BigInteger(1, messageDigest.digest());
                boolean isMd5 = TextUtils.equals(bigInteger.toString(), downerOptions.getMd5());
                if(!isMd5){//true
                    if(downerCallBack!=null){
                        downerCallBack.onErrorInstall(downerRequest.getModel(), new DownerException(ERROR_CODE_PACKAGE_INVALID));
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
