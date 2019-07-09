package com.common.admob;

import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
/**
 * 谷歌AdMob广告AdListener类扩展类
 * */
public class MyAdListener extends AdListener {
    AdvertiserCallBack adCallBack;
    private InterstitialAd mInterstitialAd;
    public MyAdListener(AdvertiserCallBack adCallBack, InterstitialAd mInterstitialAd) {
        this.adCallBack = adCallBack;
        this.mInterstitialAd = mInterstitialAd;
    }
    @Override
    public void onAdLoaded() {
        if (adCallBack != null) {
            adCallBack.onAdLoaded();
        }
        if (mInterstitialAd != null) {//如果是插页广告，加载完成后显示
            mInterstitialAd.show();
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
}