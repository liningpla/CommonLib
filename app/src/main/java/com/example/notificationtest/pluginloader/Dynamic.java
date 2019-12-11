package com.example.notificationtest.pluginloader;

import android.app.Activity;
import android.widget.Toast;

public class Dynamic {
    private Activity mActivity;

    public void init(Activity activity) {
        mActivity = activity;
    }

    public void showBanner() {
        Toast.makeText(mActivity, "我是ShowBannber方法", Toast.LENGTH_LONG).show();
    }

    public void showDialog() {
        Toast.makeText(mActivity, "我是ShowDialog方法", Toast.LENGTH_LONG).show();
    }

    public void showFullScreen() {
        Toast.makeText(mActivity, "我是ShowFullScreen方法", Toast.LENGTH_LONG).show();
    }

    public void showAppWall() {
        Toast.makeText(mActivity, "我是ShowAppWall方法", Toast.LENGTH_LONG).show();
    }

    public void destory() {
        Toast.makeText(mActivity, "我是destory方法", Toast.LENGTH_LONG).show();
    }
}
