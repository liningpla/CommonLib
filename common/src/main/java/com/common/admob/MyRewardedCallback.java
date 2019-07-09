package com.common.admob;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

/**
 * 谷歌AdMob广告RewardedAdLoadCallback类扩展类
 * */
public class MyRewardedCallback extends RewardedAdLoadCallback {
    Context context;
    RewardedAd rewardedAd;
    AdvertiserCallBack adCallBack;

    public MyRewardedCallback(Context context, RewardedAd rewardedAd, AdvertiserCallBack adCallBack) {
        this.context = context;
        this.rewardedAd = rewardedAd;
        this.adCallBack = adCallBack;
    }
    @Override
    public void onRewardedAdLoaded() {
        RewardedAdCallback adCallback = new RewardedAdCallback() {
            public void onRewardedAdOpened() {
                if (adCallBack != null) {
                    adCallBack.onAdOpened();
                }
                Log.i(Advertiser.TAG, "AdMobHelper:addRewardedAd: onRewardedAdOpened");
            }
            public void onRewardedAdClosed() {
                if (adCallBack != null) {
                    adCallBack.onAdClosed();
                }
                Log.i(Advertiser.TAG, "AdMobHelper:addRewardedAd: onRewardedAdClosed");
            }
            public void onUserEarnedReward(@NonNull RewardItem reward) {
                Log.i(Advertiser.TAG, "AdMobHelper:addRewardedAd: onUserEarnedReward  User earned reward.");
            }
            public void onRewardedAdFailedToShow(int errorCode) {
                if (adCallBack != null) {
                    adCallBack.onAdFailedToLoad(errorCode);
                }
                Log.i(Advertiser.TAG, "AdMobHelper:addRewardedAd: onRewardedAdFailedToShow  errorCode:"+errorCode);
            }
        };
        if(context instanceof Activity){
            rewardedAd.show((Activity) context, adCallback);
        }else{
            Log.e(Advertiser.TAG, "AdMobHelper:addRewardedAd: the Google AdMob RewardedAd need context of Activity");
        }
    }
    public void onRewardedAdFailedToLoad(int errorCode) {
        if (adCallBack != null) {
            adCallBack.onAdFailedToLoad(errorCode);
        }
        Log.i(Advertiser.TAG, "AdMobHelper:addRewardedAd: onRewardedAdFailedToLoad  errorCode:"+errorCode);
    }
}
