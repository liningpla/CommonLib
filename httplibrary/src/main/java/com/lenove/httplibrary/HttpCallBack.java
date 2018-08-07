package com.lenove.httplibrary;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.callback.AbsCallback;

import okhttp3.Response;

/**
 * Created by lining on 2018/5/24.
 */

public class HttpCallBack<T> extends AbsCallback<T> {
    Class<T> clazz;
    public HttpCallBack(Class<T> clazz) {
        this.clazz = clazz;
    }
    @Override
    public void onSuccess(com.lzy.okgo.model.Response<T> response) {
    }
    @Override
    public T convertResponse(Response response) throws Throwable {
        return JSON.parseObject(response.body().string(), clazz);
    }
}
