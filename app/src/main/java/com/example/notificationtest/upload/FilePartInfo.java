package com.example.notificationtest.upload;

import java.io.Serializable;

/**
 * Created by lining on 2017/3/16.
 * 分片文件信息
 */
public class FilePartInfo implements Serializable {
    /**父类文件的md5*/
    public String parentMd5;
    /**父文件*/
    public String parentFilePath;
    /**分片文件相对父文件开始位置*/
    public int startPos;
    /**分片文件相对父文件结束位置*/
    public int endPos;
    /**分片文件已经加载的大小*/
    public int loadSize;
    /**分片文件每次while读的大小*/
    public int num;
    /**分片文件range起始位置*/
    public int rangeSize;
    /**分片文件总大小*/
    public long totalSize;
    /**是否加载完成*/
    public boolean isLoadSuccess;

    public int getRangeSize() {
        return startPos + loadSize;
    }
}
