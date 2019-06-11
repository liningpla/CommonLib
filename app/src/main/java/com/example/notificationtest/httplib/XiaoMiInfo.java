package com.example.notificationtest.httplib;

import java.util.List;

public class XiaoMiInfo {

    /**
     * deviceInfo : {"screenWidth":1080,"screenHeight":2246,"screenDensity":3,"model":"Lenovo L78011","device":"jd2018","androidVersion":"8.1.0","miuiVersion":"4.0.285_180815","miuiVersionName":"UNKNOWN","bc":"UNKNOWN","make":"lenovo","isInter":false,"os":"android"}
     * userInfo : {"locale":"zh_CN","language":"zh","country":"CN","customization":"","networkType":-1,"connectionType":"WIFI","ua":"Dalvik/2.1.0 (Linux; U; Android 8.1.0; Lenovo L78011 Build/OPM1.171019.019)","serviceProvider":"46003","triggerId":"c8e9657450dd528c04628ce2ad742ff5","imei":"84b60a33ccb017501b34704ecdd55433","mac":"7809940c6004f53eefde3807f6568c1d","aaid":"","androidId":"12014411f5281b2046eeff8846a665d5","ip":"192.168.31.110"}
     * appInfo : {"platform":"xiaomi","packageName":"com.xiaomi.ad.mimo.demo","version":2018033100}
     * impRequests : [{"tagId":"0c220d9bf7029e71461f247485696d07","adsCount":1}]
     * adSdkInfo : {"version":"2.1.1","chameleonPluginVersion":"2.4.0"}
     * context : {"ds":{"ov":27,"abis":"arm64-v8a,arm64-v8a,armeabi-v7a,armeabi,armeabi","advc":2019051500,"advn":"2.4.0"},"token":""}
     */

    @ SerializedName("deviceInfo")
    public DeviceInfoBean deviceInfo;
    @ SerializedName("userInfo")
    public UserInfoBean userInfo;
    @ SerializedName("appInfo")
    public AppInfoBean appInfo;
    @ SerializedName("adSdkInfo")
    public AdSdkInfoBean adSdkInfo;
    @ SerializedName("context")
    public ContextBean context;
    @ SerializedName("impRequests")
    public List<ImpRequestsBean> impRequests;

    public static class DeviceInfoBean {
        /**
         * screenWidth : 1080
         * screenHeight : 2246
         * screenDensity : 3
         * model : Lenovo L78011
         * device : jd2018
         * androidVersion : 8.1.0
         * miuiVersion : 4.0.285_180815
         * miuiVersionName : UNKNOWN
         * bc : UNKNOWN
         * make : lenovo
         * isInter : false
         * os : android
         */

        @ SerializedName("screenWidth")
        public int screenWidth;
        @ SerializedName("screenHeight")
        public int screenHeight;
        @ SerializedName("screenDensity")
        public int screenDensity;
        @ SerializedName("model")
        public String model;
        @ SerializedName("device")
        public String device;
        @ SerializedName("androidVersion")
        public String androidVersion;
        @ SerializedName("miuiVersion")
        public String miuiVersion;
        @ SerializedName("miuiVersionName")
        public String miuiVersionName;
        @ SerializedName("bc")
        public String bc;
        @ SerializedName("make")
        public String make;
        @ SerializedName("isInter")
        public boolean isInter;
        @ SerializedName("os")
        public String os;
    }

    public static class UserInfoBean {
        /**
         * locale : zh_CN
         * language : zh
         * country : CN
         * customization : 
         * networkType : -1
         * connectionType : WIFI
         * ua : Dalvik/2.1.0 (Linux; U; Android 8.1.0; Lenovo L78011 Build/OPM1.171019.019)
         * serviceProvider : 46003
         * triggerId : c8e9657450dd528c04628ce2ad742ff5
         * imei : 84b60a33ccb017501b34704ecdd55433
         * mac : 7809940c6004f53eefde3807f6568c1d
         * aaid : 
         * androidId : 12014411f5281b2046eeff8846a665d5
         * ip : 192.168.31.110
         */

        @ SerializedName("locale")
        public String locale;
        @ SerializedName("language")
        public String language;
        @ SerializedName("country")
        public String country;
        @ SerializedName("customization")
        public String customization;
        @ SerializedName("networkType")
        public int networkType;
        @ SerializedName("connectionType")
        public String connectionType;
        @ SerializedName("ua")
        public String ua;
        @ SerializedName("serviceProvider")
        public String serviceProvider;
        @ SerializedName("triggerId")
        public String triggerId;
        @ SerializedName("imei")
        public String imei;
        @ SerializedName("mac")
        public String mac;
        @ SerializedName("aaid")
        public String aaid;
        @ SerializedName("androidId")
        public String androidId;
        @ SerializedName("ip")
        public String ip;
    }

    public static class AppInfoBean {
        /**
         * platform : xiaomi
         * packageName : com.xiaomi.ad.mimo.demo
         * version : 2018033100
         */

        @ SerializedName("platform")
        public String platform;
        @ SerializedName("packageName")
        public String packageName;
        @ SerializedName("version")
        public int version;
    }

    public static class AdSdkInfoBean {
        /**
         * version : 2.1.1
         * chameleonPluginVersion : 2.4.0
         */

        @ SerializedName("version")
        public String version;
        @ SerializedName("chameleonPluginVersion")
        public String chameleonPluginVersion;
    }

    public static class ContextBean {
        /**
         * ds : {"ov":27,"abis":"arm64-v8a,arm64-v8a,armeabi-v7a,armeabi,armeabi","advc":2019051500,"advn":"2.4.0"}
         * token : 
         */

        @ SerializedName("ds")
        public DsBean ds;
        @ SerializedName("token")
        public String token;

        public static class DsBean {
            /**
             * ov : 27
             * abis : arm64-v8a,arm64-v8a,armeabi-v7a,armeabi,armeabi
             * advc : 2019051500
             * advn : 2.4.0
             */

            @ SerializedName("ov")
            public int ov;
            @ SerializedName("abis")
            public String abis;
            @ SerializedName("advc")
            public int advc;
            @ SerializedName("advn")
            public String advn;
        }
    }

    public static class ImpRequestsBean {
        /**
         * tagId : 0c220d9bf7029e71461f247485696d07
         * adsCount : 1
         */

        @ SerializedName("tagId")
        public String tagId;
        @ SerializedName("adsCount")
        public int adsCount;
    }
}
