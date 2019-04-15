package com.example.notificationtest.httplib;

import java.util.LinkedHashMap;

public class HttpHeaders {

    /** 普通的键值对参数 */
    public LinkedHashMap<String, String> httpHeaders;

    public HttpHeaders() {
        init();
    }
    private void init(){
        httpHeaders = new LinkedHashMap<>();
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            httpHeaders.put(key, value);
        }
    }
    public void put(HttpHeaders headers) {
        if (headers != null) {
            if (headers.httpHeaders != null && !headers.httpHeaders.isEmpty()) httpHeaders.putAll(headers.httpHeaders);
        }
    }
    public int getSize(){
        if(httpHeaders != null){
            return httpHeaders.size();
        }
        return 0;
    }
}
