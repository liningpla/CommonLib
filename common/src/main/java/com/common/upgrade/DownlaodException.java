package com.common.upgrade;

import java.io.IOException;

/**
 */
public class DownlaodException extends IOException {

    /**
     * 安装包md5效验失败
     */
    public static final int ERROR_CODE_PACKAGE_INVALID = 10020;
    /**
     * 未知错误
     */
    public static final int ERROR_CODE_UNKNOWN = 10045;

    private int code;

    public DownlaodException() {
        this(ERROR_CODE_UNKNOWN);
    }

    public DownlaodException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
