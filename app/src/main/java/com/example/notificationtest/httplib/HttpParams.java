package com.example.notificationtest.httplib;

import java.util.LinkedHashMap;

public class HttpParams {

    /** 普通的键值对参数 */
    public LinkedHashMap<String, String> httpParams;

    public HttpParams() {
        init();
    }
    private void init(){
        httpParams = new LinkedHashMap<>();
    }
    public void put(String key, String value) {
        if (key != null && value != null) {
            httpParams.put(key, value);
        }
    }
    public void put(HttpParams headers) {
        if (headers != null) {
            if (headers.httpParams != null && !headers.httpParams.isEmpty()) httpParams.putAll(headers.httpParams);
        }
    }
    public int getSize(){
        if(httpParams != null){
            return httpParams.size();
        }
        return 0;
    }
}
