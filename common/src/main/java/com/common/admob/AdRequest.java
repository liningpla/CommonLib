package com.common.admob;

import android.content.Context;
import android.view.ViewGroup;

/**广告请求类*/
public class AdRequest {

    /**对外抛出广告状态*/
    private AdvertiserListener adListener;
    /**广告模型*/
    private AdMold adMold;
    /**广告父类容器*/
    private ViewGroup container;

    /**添加banner广告
     * @param container 广告父类容器
     * */
    public AdRequest banner(Context context, ViewGroup container){
        adMold = AdMold.BANNER;
        return this;
    }

    /**添加插屏广告
     *@param container 广告父类容器
     * */
    public AdRequest interstitial(Context context, ViewGroup container){
        adMold = AdMold.INTERSTITIAL;
        return this;
    }

    /**添加开屏广告广告
     * @param container 广告父类容器
     * */
    public AdRequest splash(Context context, ViewGroup container){
        adMold = AdMold.SPLASH;
        return this;
    }

    /**添加信息流广告-原生广告
     * @param container 广告父类容器
     * */
    public AdRequest newsFeed(Context context, ViewGroup container){
        adMold = AdMold.NEWSFEED;
        return this;
    }

}
