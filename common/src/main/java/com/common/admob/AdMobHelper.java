package com.common.admob;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

/**
 * 谷歌广告AdMob辅助类
 */
public class AdMobHelper extends BuilderParmas {
    /**
     * Manifest中配置AdMob的Key
     */
    public static final String ADMOB_KEY = "com.google.android.gms.ads.APPLICATION_ID";
    /**
     * 初始化AdMob SDK
     */
    public static void initAdMob(Context context) {
        if (!TextUtils.isEmpty(AdvertiserUtil.getMetaData(context, ADMOB_KEY))) {
            String ad_id = AdvertiserUtil.getMetaData(context, ADMOB_KEY);
            MobileAds.initialize(context, ad_id);
        }
    }


    private Context context;
    private AdvertiserCallBack adCallBack;
    /**是否需要设置大小*/
    private AdSize adSize;

    public AdMobHelper(Context context) {
        this.context = context;
    }

    /**
     * 配置广告宽高
     *
     * @param width  宽
     * @param height 高
     */
    public AdMobHelper widthAndHeight(int width, int height) {
        if(this.width == width && this.height == height){
            return this;
        }
        adSize = new AdSize(width, height);
        return this;
    }

    /**
     * 配置广告id
     *
     * @param adUnitId 广告id
     */
    public AdMobHelper adUnitId(String adUnitId) {
        this.adUnitId = adUnitId;
        return this;
    }

    /**
     * 添加横幅广告
     */
    public void addBanner(ViewGroup container, AdvertiserCallBack adCallBack) {
        this.adCallBack = adCallBack;
        AdView adView = new AdView(context);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdSize(adSize==null?AdSize.SMART_BANNER:adSize);
        adView.setAdUnitId(adUnitId);
        adView.setAdListener(adListener);
        adView.loadAd(adRequest);
        container.removeAllViews();
        container.addView(adView);
    }

    /**
     * AdMob广告监听
     */
    private AdListener adListener = new AdListener() {
        @Override
        public void onAdLoaded() {
            if (adCallBack != null) {
                adCallBack.onAdLoaded();
            }
            Log.i(Advertiser.TAG, "AdMobHelper:AdListener:onAdLoaded");
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            if (adCallBack != null) {
                adCallBack.onAdFailedToLoad(errorCode);
            }
            Log.i(Advertiser.TAG, "AdMobHelper:AdListener:onAdFailedToLoad");
        }

        @Override
        public void onAdOpened() {
            if (adCallBack != null) {
                adCallBack.onAdOpened();
            }
            Log.i(Advertiser.TAG, "AdMobHelper:AdListener:onAdOpened");
        }

        @Override
        public void onAdClicked() {
            if (adCallBack != null) {
                adCallBack.onAdClicked();
            }
            Log.i(Advertiser.TAG, "AdMobHelper:AdListener:onAdClicked");
        }

        @Override
        public void onAdLeftApplication() {
            if (adCallBack != null) {
                adCallBack.onAdLeftApplication();
            }
            Log.i(Advertiser.TAG, "AdMobHelper:AdListener:onAdLeftApplication");
        }

        @Override
        public void onAdClosed() {
            if (adCallBack != null) {
                adCallBack.onAdClosed();
            }
            Log.i(Advertiser.TAG, "AdMobHelper:AdListener:onAdClosed");
        }
    };

}
