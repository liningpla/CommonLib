package com.common.admob;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.ads.MobileAds;

/**广告商
 * 负责广告的调度
 * */
public class Advertiser {

    public static final String TAG = "AdLog";
    /**Manifest中配置AdMob的Key*/
    public static final String ADMOB_KEY = "com.google.android.gms.ads.APPLICATION_ID";
    /**AdMob的分配的ID*/
    public static final String ADMOB_ID = "ca-app-pub-6725710354938817~4696680990";

    /**初始化广告*/
    public static void init(Context context){
        //初始化谷歌广告
        if(!TextUtils.isEmpty(AdvertiserUtil.getMetaData(context, ADMOB_KEY))){
            MobileAds.initialize(context, ADMOB_ID);
        }
    }

    /**添加横幅广告*/
    public static void addBannerAd(Context context, AdvertiserListener listener){

    }




}
