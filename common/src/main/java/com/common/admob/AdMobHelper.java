package com.common.admob;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.rewarded.RewardedAd;

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

    @Override
    public AdMobHelper container(ViewGroup container) {
        super.container(container);
        return this;
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
    public void addBanner(final AdvertiserCallBack adCallBack) {
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
     * 添加插页式广告-全屏-图文
     */
    public void addInterstitial(final AdvertiserCallBack adCallBack) {
        InterstitialAd mInterstitialAd = new InterstitialAd(context);
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.setAdUnitId(adUnitId);
        mInterstitialAd.setAdListener(new MyAdListener(adCallBack,mInterstitialAd));
        mInterstitialAd.loadAd(adRequest);
    }

    /**
     * 添加激励广告-全屏-视频
     */
    public void addRewardedAd(final AdvertiserCallBack adCallBack) {
        final RewardedAd rewardedAd = new RewardedAd(context, adUnitId);
        rewardedAd.loadAd(new AdRequest.Builder().build(), new MyRewardedCallback(context,rewardedAd,adCallBack));
    }

    /**
     * 添加信息流广告-原生
     */
    public void addNewsFeed(final ViewGroup container, final AdvertiserCallBack adCallBack) {
//        AdLoader.Builder builder = new AdLoader.Builder(context, "<your ad unit ID>")
//                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
//                    @Override
//                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
//                        // Assumes you have a placeholder FrameLayout in your View layout
//                        // (with id fl_adplaceholder) where the ad is to be placed.
//                        FrameLayout frameLayout =
//                                findViewById(R.id.fl_adplaceholder);
//                        // Assumes that your ad layout is in a file call ad_unified.xml
//                        // in the res/layout folder
//                        UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
//                                .inflate(R.layout.ad_unified, null);
//                        // This method sets the text, images and the native ad, etc into the ad
//                        // view.
//                        populateUnifiedNativeAdView(unifiedNativeAd, adView);
//                        container.removeAllViews();
//                        container.addView(adView);
//                    }
//                });
    }

}
