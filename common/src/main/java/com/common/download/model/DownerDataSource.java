package com.common.download.model;

/**
 */
public interface DownerDataSource {

    /**
     * 获取升级缓存
     *
     * @param url
     * @return
     */
    DownerBuffer getUpgradeBuffer(String url);

    /**
     * 设置升级缓存
     *
     * @param buffer
     */
    void setUpgradeBuffer(DownerBuffer buffer);

}
