package com.lenove.httplibrary;

import android.app.Application;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import okhttp3.OkHttpClient;

/**
 * Created by lining on 2018/5/24.
 */

public class OkGoManager {
    private static OkGoManager okGoManager;
    public static OkGoManager getInstance() {
        if (okGoManager == null) {
            synchronized (OkGoManager.class) {
                if (okGoManager == null) {
                    okGoManager = new OkGoManager();
                }
            }
        }
        return okGoManager;
    }
    /**初始化网络库
     * @param application 申请网络服务的应用
     * @param headers 请求头
     * @param params 请求公共内容
     * */
    public void initOkGo(Application application, HttpHeaders headers, HttpParams params){
        try {
            if(headers == null){
                headers = new HttpHeaders();
            }
            if(params == null){
                params = new HttpParams();
            }
            //使用OkGo的拦截器
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("meee");
            //日志的打印范围
            loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
            //在logcat中的颜色
            loggingInterceptor.setColorLevel(Level.INFO);
            //默认是Debug日志类型
            builder.addInterceptor(loggingInterceptor);

            //设置请求超时时间,默认60秒
            builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);      //读取超时时间
            builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);     //写入超时时间
            builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);   //连接超时时间

            //okhttp默认不保存cookes/session信息,需要自己的设置
            //builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));            //使用sp保持cookie，如果cookie不过期，则一直有效
            //builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));              //使用数据库保持cookie，如果cookie不过期，则一直有效
            builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));            //使用内存保存cookie,退出后失效
            OkGo.getInstance()
                    .init(application)
                    .setOkHttpClient(builder.build())//不设置则使用默认
                    .setCacheMode(CacheMode.NO_CACHE)//设置缓存模式
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)//设置缓存时间,默认永不过期
                    .setRetryCount(3)//请求超时重连次数,默认3次
                    .addCommonHeaders(headers)
                    .addCommonParams(params);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
