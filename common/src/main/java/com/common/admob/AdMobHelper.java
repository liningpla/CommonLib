package com.common.admob;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.captureinfo.R;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
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
    public void addBanner(AdvertiserCallBack adCallBack) {
        AdView adView = new AdView(context);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.setAdSize(adSize == null ? AdSize.SMART_BANNER : adSize);
        adView.setAdUnitId(adUnitId);
        adView.setAdListener(new MyAdListener(adCallBack));
        adView.loadAd(adRequest);
        container.removeAllViews();
        container.addView(adView);
    }

    /**
     * 添加插页式广告-全屏-图文
     */
    public void addInterstitial(AdvertiserCallBack adCallBack) {
        InterstitialAd mInterstitialAd = new InterstitialAd(context);
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.setAdUnitId(adUnitId);
        mInterstitialAd.setAdListener(new MyAdListener(adCallBack,mInterstitialAd));
        mInterstitialAd.loadAd(adRequest);
    }

    /**
     * 添加激励广告-全屏-视频
     */
    public void addRewardedAd(AdvertiserCallBack adCallBack) {
        final RewardedAd rewardedAd = new RewardedAd(context, adUnitId);
        rewardedAd.loadAd(new AdRequest.Builder().build(), new MyRewardedCallback(context,rewardedAd,adCallBack));
    }

    /**
     * 添加信息流广告-原生
     */
    public void addNativeAd(AdModel.NewsFeedType feedType, AdvertiserCallBack adCallBack) {

    }
    /**
     * 添加信息流广告-原生
     */
    public void addNativeAd(AdvertiserCallBack adCallBack) {
        AdLoader adLoader = new AdLoader.Builder(context, adUnitId)
                .forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                        Log.i(Advertiser.TAG, "           getBody:"+unifiedNativeAd.getBody());
                        UnifiedNativeAdView adView = (UnifiedNativeAdView) LayoutInflater.from(context).inflate(R.layout.ad_unified, null);
                        bindNativeAdView(unifiedNativeAd, adView);
                        container.removeAllViews();
                        container.addView(adView);
                    }
                })
                .withAdListener(new MyAdListener(adCallBack))
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }
    /**
     * Populates a {@link UnifiedNativeAdView} object with data from a given
     * {@link UnifiedNativeAd}.
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView          the view to be populated
     */
    private void bindNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        // 标题设置。
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }
        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }
        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }
        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }
        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        // 设置媒体视图。媒体内容将在媒体视图中自动填充一次
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);
        // 这个方法告诉谷歌移动广告SDK，您已经用这个本地广告填充了您的本地广告视图
        // SDK将用这个本地广告中的媒体内容填充adView的MediaView。
        adView.setNativeAd(nativeAd);
        //获取视频控制器，用来检查有无视频资源
        VideoController vc = nativeAd.getVideoController();
        // 更新UI，以说明该广告是否具有视频
        if (vc.hasVideoContent()) {
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    super.onVideoEnd();
                }
            });
        } else {
            Log.i(Advertiser.TAG, "This ad is not a video");
        }
    }
    /**
     * 谷歌UnifiedNativeAd原生数据公共函数详解：
     *         取消之前为广告记录的未经证实的点击。
     *         cancelUnconfirmedClick()
     *         销毁广告对象。
     *         destroy()
     *         允许发布商使用自定义手势报告点击次数
     *         enableCustomClickGesture()
     *         返回AdChoices归因的信息。
     *         getAdChoicesInfo()
     *         返回标识广告客户的文本。
     *         getAdvertiser()
     *         返回标识广告商的文本。
     *         getBody()
     *         返回广告的号召性用语（例如“购买”或“安装”）。
     *         getCallToAction()
     *         返回与原生广告关联的一系列额外资源。
     *         getExtras()
     *         返回主要文本标题。
     *         getHeadline()
     *         返回标识广告商的小图片。
     *         getIcon()
     *         返回大图像列表。
     *         getImages()
     *         返回 UnifiedNativeAd.MediaContent与此广告相关联的内容。
     *         getMediaContent()
     *         返回中介适配器类名称。
     *         getMediationAdapterClassName()
     *         返回静音此广告可用的广告原因。
     *         getMuteThisAdReasons()
     *         对于有关应用的广告，返回表示应用费用的字符串。
     *         getPrice()
     *         对于有关应用的广告，返回从0到5的星级评级，表示该应用在提供该应用的商店中有多少颗星。
     *         getStarRating()
     *         对于有关应用的广告，请返回提供应用程序以供下载的商店的名称。
     *         getStore()
     *         返回与此广告关联的视频控制器。
     *         getVideoController()
     *         指示是否可以使用自定义单击手势报告点击次数。
     *         isCustomClickGestureEnabled()
     *         返回true此广告是否可以通过编程方式静音。
     *         isCustomMuteThisAdEnabled()
     *         以编程方式将此广告静音。
     *         muteThisAd(MuteThisAdReason muteThisAdReason)
     *         应在用户点击广告时调用。
     *         performClick(Bundle clickData)
     *         使用自定义单击手势 报告此点击次数。
     *         recordCustomClickGesture():UnifiedNativeAd
     *         应在首次显示广告时调用。
     *         recordImpression(Bundle impressionData)
     *         应在广告上发生触摸事件时调用。
     *         reportTouchEvent(Bundle touchEventData)
     *         设置MuteThisAdListener 广告。
     *         setMuteThisAdListener(MuteThisAdListener listener)
     *         为广告设置UnconfirmedClickListener。
     *         setUnconfirmedClickListener(UnifiedNativeAd.UnconfirmedClickListener listener)
     * */


}
