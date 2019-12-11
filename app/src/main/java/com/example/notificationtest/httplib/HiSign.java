package com.example.notificationtest.httplib;

import com.example.notificationtest.httplib.sign.Constants;
import com.example.notificationtest.httplib.sign.SignatureUtils;

public enum  HiSign {

    INIT;

    public String getSignStr(HttpParams params){
        HttpParams signParams = new HttpParams();
        signParams.put(params);
        signParams.put(HiHttp.instance.getCommonParams());
        String sb =  SignatureUtils.getSignCheckContent(signParams.httpParams);
        HiLog.i(HiHttp.TAG, sb);
        String sign =  SignatureUtils.rsa256Sign(sb, Constants.privateKey);
        HiLog.i(HiHttp.TAG, sign);
        return sign;
    }
}
