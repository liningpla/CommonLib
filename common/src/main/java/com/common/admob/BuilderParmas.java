package com.common.admob;

import android.view.ViewGroup;

/**
 * 构造参数类
 * */
public abstract class BuilderParmas {
    /**广告宽*/
    public int width = 320;
    /**广告高*/
    public int height = 100;
    /**广告的id*/
    public String adUnitId;
    /**广告父类容器*/
    public ViewGroup container;

    /**
     * 配置广告宽高
     * @param width 宽
     * @param height 高
     * */
    public BuilderParmas widthAndHeight(int width, int height){
        this.width = width > 0?width:this.width;
        this.height = height > 0?height:this.height;
        return this;
    }

    /**
     * 配置广告id
     * @param adUnitId 广告id
     * */
    public BuilderParmas adUnitId(String adUnitId){
        this.adUnitId = adUnitId;
        return this;
    }
    /**配置广告父类容器*/
    public BuilderParmas container(ViewGroup container){
        this.container = container;
        return this;
    }
}
