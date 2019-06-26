package com.common.download;

import com.common.download.downer.DownerRequest;

/**下载师傅*/
public class Downer {

    public static final String TAG = "HiDower";
    /**连接超时时长*/
    public static final int CONNECT_TIMEOUT = 60 * 1000;
    /**读取超时时长*/
    public static final int READ_TIMEOUT = 60 * 1000;
    /**重定向302 key*/
    public static final int SC_MOVED_TEMPORARILY = 302;
    /**重定向3301 key*/
    public static final int SC_MOVED_PERMANENTLY = 301;
    /**重定向url key*/
    public static final String REDIRECT_LOCATION_KEY = "Location";

    /**下载开始*/
    public static final int STATUS_DOWNLOAD_START = 0x1001;
    /**下载进度*/
    public static final int STATUS_DOWNLOAD_PROGRESS = 0x1002;
    /**下载暂停*/
    public static final int STATUS_DOWNLOAD_PAUSE = 0x1003;
    /**下载取消*/
    public static final int STATUS_DOWNLOAD_CANCEL = 0x1004;
    /**下载错误*/
    public static final int STATUS_DOWNLOAD_ERROR = 0x1005;
    /**下载完成*/
    public static final int STATUS_DOWNLOAD_COMPLETE = 0x1006;
    /**安装完成*/
    public static final int STATUS_INSTALL_COMPLETE = 0x2005;

    private static Downer downer;

    public boolean isMuliti;

    public int pools;

    public boolean isInstall;

    public boolean isClean;

    public boolean isSupportRange;

    /**开始下载*/
    public static DownerRequest downLoad(){
        return new DownerRequest();
    }

    public static Downer init(){
        if(downer == null){
            downer = new Downer();
        }
        return downer;
    }
    /**
     * 是否支持多线程下载
     */
    public Downer setMultithreadEnabled(boolean isMuliti) {
        this.isMuliti = isMuliti;
        return downer;
    }

    /**
     * 设置线程池大小
     */
    public Downer setMultithreadPools(int pools) {
        this.pools = pools;
        return downer;
    }

    /**
     * 是否自动删除安装（可选）
     */
    public Downer setAutoInstallEnabled(boolean isInstall) {
        this.isInstall = isInstall;
        return downer;
    }

    /**
     * 是否自动删除安装包（可选）
     */
    public Downer setAutocleanEnabled(boolean isClean) {
        this.isClean = isClean;
        return downer;
    }

    /**
     * 是否使用断点续传
     */
    public Downer setSupportRange(boolean isSupportRange) {
        this.isSupportRange = isSupportRange;
        return downer;
    }

}
