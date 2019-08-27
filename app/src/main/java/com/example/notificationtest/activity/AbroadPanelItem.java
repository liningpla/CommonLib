package com.example.notificationtest.activity;

import android.widget.FrameLayout;

/**海外版首页item模型*/
public class AbroadPanelItem {

    private FrameLayout.LayoutParams layoutParams;
    private String image;
    private String name;
    private String url;

    public FrameLayout.LayoutParams getLayoutParams() {
        return layoutParams;
    }

    public void setLayoutParams(FrameLayout.LayoutParams layoutParams) {
        this.layoutParams = layoutParams;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
