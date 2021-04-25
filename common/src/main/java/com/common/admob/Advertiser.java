//package com.common.admob;
//
//import android.content.Context;
//
///**广告商
// * 负责广告的调度
// * */
//public class Advertiser {
//
//    public static final String TAG = "AdLog";
//    private static Advertiser advertiser;
//    public static AdModel.AdCP adCP;
//    /**
//     * 初始化谷歌AdMob广告
//     * */
//    public static Advertiser initAdMob(Context context){
//        if(advertiser == null){
//            advertiser = new Advertiser();
//        }
//        adCP = AdModel.AdCP.AD_MOB;
//        AdMobHelper.initAdMob(context);
//        return advertiser;
//    }
//
//    /**
//     * 初始化小米广告平台业务
//     * TODO::未来可以扩展功能，这里先占位
//     * */
//    public static Advertiser initXiaoMi(Context context){
//        if(advertiser == null){
//            advertiser = new Advertiser();
//        }
//        adCP = AdModel.AdCP.XIO_MI;
//        return advertiser;
//    }
//
//
//    /**加载广告
//     * */
//    public static AdverRequest load(Context context){
//        return new AdverRequest(context);
//    }
//
//
//
//}
