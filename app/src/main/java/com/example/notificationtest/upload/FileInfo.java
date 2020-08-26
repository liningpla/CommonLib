package com.example.notificationtest.upload;

import java.io.Serializable;
import java.util.List;

/**
 * Created by lining on 2017/3/25.
 * 文件新
 */
public class FileInfo implements Serializable{
    public String Md5;//下载文件的Md5
    public String filePath;//加载文件的路径
    public boolean isLoad;//加载文件的状态,true加载完成，false为加载完成
    public long loadSize;//已经加载的总大小
    public List<FilePartInfo> filePartInfos;//文件分片集合
}
