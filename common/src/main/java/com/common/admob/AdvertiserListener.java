package com.common.admob;

/**广告监听器*/
public interface AdvertiserListener {

    /**广告加载完成*/
    void onAdLoaded();

    /**当广告请求失败*/
    void onAdFailedToLoad(int errorCode);

    /**当广告显示在屏幕上*/
    void onAdOpened();

    /**点击广告*/
    void onAdClicked();

    /**点击广告离开应用程序*/
    void onAdLeftApplication();

    /**用户关闭广告返回到应用*/
    void onAdClosed();
}
