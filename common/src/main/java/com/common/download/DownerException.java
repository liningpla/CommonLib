package com.common.download;

import java.io.IOException;

/**
 */
public class DownerException extends IOException {

    /**
     * 安装包md5效验失败
     */
    public static final int ERROR_CODE_PACKAGE_INVALID = 10020;

    /**
     * 安装包文件不存在
     */
    public static final int ERROR_CODE_PACKAGE_FILE = 10021;
    /**
     * 未知错误
     */
    public static final int ERROR_CODE_UNKNOWN = 10045;

    private int code;

    public DownerException() {
        this(ERROR_CODE_UNKNOWN);
    }

    public DownerException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
