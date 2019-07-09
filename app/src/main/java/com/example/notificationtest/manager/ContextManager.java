package com.example.notificationtest.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.common.utils.SDLog;

/**封装公用跳转逻辑*/
public class ContextManager {
    /**uri的方式调整activity*/
    public static void intentUri(Context context, String url){
        try {
            Uri uri= Uri.parse(url);
            Intent intent=new Intent(Intent.ACTION_VIEW,uri);
            context.startActivity(intent);
        }catch (Exception e){
            SDLog.i("intentUri", e.getMessage());
        }
    }
}
