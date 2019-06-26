package com.common.upgrade.model;

/**
 */
public class DownerContrat {

    public static abstract class DownerBufferEntry {
        public static final String TABLE_NAME = "upgrade_buffer";
        public static final String COLUMN_NAME_DOWNLOAD_URL = "download_url";
        public static final String COLUMN_NAME_FILE_MD5 = "file_md5";
        public static final String COLUMN_NAME_FILE_LENGTH = "file_length";
        public static final String COLUMN_NAME_BUFFER_LENGTH = "buffer_length";
        public static final String COLUMN_NAME_BUFFER_PART = "buffer_part";
        public static final String COLUMN_NAME_LAST_MODIFIED = "last_modified";
    }

    public static abstract class DownerString {
        public static final String DOWN_CONNECTING= "下载连接中&#8230;.";
        public static final String DOWN_PAUSE = "下载暂停";
        public static final String DONW_CANCEL = "下载取消";
        public static final String DONW_ORROR = "下载出错";
        public static final String DOWN_COMPLETE= "下载完成";
    }

}
