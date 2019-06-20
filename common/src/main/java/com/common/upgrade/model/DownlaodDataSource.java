package com.common.upgrade.model;

/**
 */
public interface DownlaodDataSource {

    /**
     * 获取升级缓存
     *
     * @param url
     * @return
     */
    DownlaodBuffer getUpgradeBuffer(String url);

    /**
     * 设置升级缓存
     *
     * @param buffer
     */
    void setUpgradeBuffer(DownlaodBuffer buffer);

}
