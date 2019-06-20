package com.common.upgrade.model;

import com.common.upgrade.model.bean.DownlaodBuffer;
import com.common.upgrade.model.bean.DownlaodVersion;

/**
 */
public interface DownlaodDataSource {

    /**
     * 获取上级版本
     *
     * @param version
     * @return
     */
    DownlaodVersion getUpgradeVersion(int version);

    /**
     * 忽略版本
     *
     * @param version
     */
    void setUpgradeVersion(DownlaodVersion version);

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
