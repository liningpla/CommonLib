package com.common.admob;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**广告业务相关工作类*/
public class AdvertiserUtil {

    /**获取meta-data数据*/
    public static String getMetaData(Context context, String metaKey){
        String metaData = "";
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            metaData = appInfo.metaData.getString(metaKey);
        }catch (Exception e){
        }
        return metaData;
    }


}
