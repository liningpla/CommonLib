package com.common.admob;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

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
    /**
     * 是否需要设置大小
     */
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
        if (this.width == width && this.height == height) {
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
    public void addBanner(ViewGroup container, final AdvertiserCallBack adCallBack) {
        AdView adView = new AdView(context);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdSize(adSize == null ? AdSize.SMART_BANNER : adSize);
        adView.setAdUnitId(adUnitId);
        adView.setAdListener(new MyAdListener(adCallBack,null));
        adView.loadAd(adRequest);
        container.removeAllViews();
        container.addView(adView);
    }

    /**
     * 添加插页式广告
     */
    public void addInterstitial(final AdvertiserCallBack adCallBack) {
        InterstitialAd mInterstitialAd = new InterstitialAd(context);
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.setAdUnitId(adUnitId);
        mInterstitialAd.setAdListener(new MyAdListener(adCallBack,mInterstitialAd));
        mInterstitialAd.loadAd(adRequest);
    }

    /**
     * 添加开屏广告
     */
    public void addSplash(final AdvertiserCallBack adCallBack) {
        final RewardedAd rewardedAd = new RewardedAd(context, adUnitId);
        RewardedAdLoadCallback adLoadCallback = new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                RewardedAdCallback adCallback = new RewardedAdCallback() {
                    public void onRewardedAdOpened() {
                        if (adCallBack != null) {
                            adCallBack.onAdOpened();
                        }
                        Log.e(Advertiser.TAG, "AdMobHelper:addSplash: onRewardedAdOpened");
                    }
                    public void onRewardedAdClosed() {
                        if (adCallBack != null) {
                            adCallBack.onAdClosed();
                        }
                        Log.i(Advertiser.TAG, "AdMobHelper:addSplash: onRewardedAdClosed");
                    }
                    public void onUserEarnedReward(@NonNull RewardItem reward) {
                        Log.i(Advertiser.TAG, "AdMobHelper:addSplash: onUserEarnedReward  User earned reward.");
                    }
                    public void onRewardedAdFailedToShow(int errorCode) {
                        if (adCallBack != null) {
                            adCallBack.onAdFailedToLoad(errorCode);
                        }
                        Log.i(Advertiser.TAG, "AdMobHelper:addSplash: onRewardedAdFailedToShow  errorCode:"+errorCode);
                    }
                };
                if(context instanceof Activity){
                    rewardedAd.show((Activity) context, adCallback);
                }else{
                    Log.e(Advertiser.TAG, "AdMobHelper:addSplash: the Google AdMob RewardedAd need context of Activity");
                }
            }
            public void onRewardedAdFailedToLoad(int errorCode) {
                if (adCallBack != null) {
                    adCallBack.onAdFailedToLoad(errorCode);
                }
                Log.i(Advertiser.TAG, "AdMobHelper:addSplash: onRewardedAdFailedToLoad  errorCode:"+errorCode);
            }
        };
        rewardedAd.loadAd(new AdRequest.Builder().build(), adLoadCallback);

    }

    /**拓展AdMob广告监听*/
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


}
