package com.common.admob;

import android.content.Context;
import android.view.ViewGroup;

import java.lang.ref.SoftReference;

/**广告请求类*/
public class AdverRequest extends BuilderParmas {

    private SoftReference<Context> softContext;
    /**对外抛出广告状态*/
    private SoftReference<AdvertiserCallBack> softAdCallBack;
    private AdvertiserCallBack adCallBack = null;
    /**广告模型*/
    private AdModel.AdMold adMold;
    /**构造广告请求
     * */
    public AdverRequest(Context context) {
        softContext = new SoftReference<>(context);
    }

    public AdverRequest container(ViewGroup container){
        super.container(container);
        return this;
    }

    @Override
    public AdverRequest widthAndHeight(int width, int height) {
        super.widthAndHeight(width, height);
        return this;
    }

    @Override
    public AdverRequest adUnitId(String adUnitId) {
        super.adUnitId(adUnitId);
        return this;
    }

    /**添加banner广告
     * */
    public void banner(AdvertiserCallBack adCallBack){
        adMold = AdModel.AdMold.BANNER;
        if(adCallBack == null){
            loadAd(null);
            return;
        }
        softAdCallBack = new SoftReference<>(adCallBack);
        loadAd(softAdCallBack.get());
    }
    /**添加banner广告
     * */
    public void banner(){
        banner(null);
    }
    /**添加插屏广告
     * */
    public void interstitial(AdvertiserCallBack adListener){
        adMold = AdModel.AdMold.INTERSTITIAL;
        if(adCallBack == null){
            loadAd(null);
            return;
        }
        softAdCallBack = new SoftReference<>(adListener);
        loadAd(softAdCallBack.get());
    }

    /**添加插屏广告
     * */
    public void interstitial(){
        interstitial(null);
    }

    /**添加开屏广告广告
     * */
    public void splash(AdvertiserCallBack adListener){
        adMold = AdModel.AdMold.SPLASH;
        if(adCallBack == null){
            loadAd(null);
            return;
        }
        softAdCallBack = new SoftReference<>(adListener);
        loadAd(softAdCallBack.get());
    }

    /**添加开屏广告广告
     * */
    public void splash(){
        splash(null);
    }

    /**添加信息流广告-原生广告
     * */
    public void newsFeed(AdvertiserCallBack adListener){
        adMold = AdModel.AdMold.NEWSFEED;
        if(adCallBack == null){
            loadAd(null);
            return;
        }
        softAdCallBack = new SoftReference<>(adListener);
        loadAd(softAdCallBack.get());
    }
    /**添加信息流广告-原生广告
     * */
    public void newsFeed(){
        newsFeed(null);
    }

    /**加载广告*/
    private void loadAd(AdvertiserCallBack adCallBack){
        switch (adMold){
            case BANNER:
                laodBanner(adCallBack);
                break;
            case SPLASH:
                laodRewardedAd(adCallBack);
                break;
            case INTERSTITIAL:
                laodInterstitial(adCallBack);
                break;
            case NEWSFEED:
                laodNewsfeed(adCallBack);
                break;
        }
    }

    /**加载横幅广告*/
    private void laodBanner(AdvertiserCallBack adCallBack){
        switch (Advertiser.adCP){
            case AD_MOB:
                new AdMobHelper(softContext.get())
                        .adUnitId(this.adUnitId)
                        .widthAndHeight(this.width, this.height)
                        .container(container)
                        .addBanner(adCallBack);
                break;
            case XIO_MI:
                break;
        }

    }

    /**加载激励广告-全屏-视频*/
    private void laodRewardedAd(AdvertiserCallBack adCallBack){
        switch (Advertiser.adCP){
            case AD_MOB:
                new AdMobHelper(softContext.get())
                        .adUnitId(this.adUnitId)
                        .widthAndHeight(this.width, this.height)
                        .addRewardedAd(adCallBack);
                break;
            case XIO_MI:
                break;
        }
    }

    /**加载插入广告-全屏-图文*/
    private void laodInterstitial(AdvertiserCallBack adCallBack){
        switch (Advertiser.adCP){
            case AD_MOB:
                new AdMobHelper(softContext.get())
                        .adUnitId(this.adUnitId)
                        .widthAndHeight(this.width, this.height)
                        .addInterstitial(adCallBack);
                break;
            case XIO_MI:
                break;
        }
    }

    /**加载原生-信息流广告*/
    private void laodNewsfeed(AdvertiserCallBack adCallBack){
        switch (Advertiser.adCP){
            case AD_MOB:
                break;
            case XIO_MI:
                break;
        }
    }

}
