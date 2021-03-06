package com.example.notificationtest.httplib;


/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/24
 * 描    述：
 * 修订历史：
 * ================================================
 */
public enum HttpMethod {
    GET("GET"),

    POST("POST");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public boolean hasBody() {
        switch (this) {
            case POST:
                return true;
            default:
                return false;
        }
    }
}
