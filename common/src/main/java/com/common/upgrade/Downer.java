package com.common.upgrade;

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
    /**安装效验*/
    public static final int STATUS_INSTALL_CHECK = 0x2001;
    /**安装开始*/
    public static final int STATUS_INSTALL_START = 0x2002;
    /**安装错误*/
    public static final int STATUS_INSTALL_ERROR = 0x2004;
    /**安装完成*/
    public static final int STATUS_INSTALL_COMPLETE = 0x2005;

    /**开始下载*/
    public static DownerRequest downLoad(){
        return new DownerRequest();
    }
}
