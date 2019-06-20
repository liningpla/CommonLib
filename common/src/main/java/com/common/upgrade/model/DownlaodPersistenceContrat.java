package com.common.upgrade.model;

/**
 */
public class DownlaodPersistenceContrat {

    public static abstract class UpgradeVersionEntry {
        public static final String TABLE_NAME = "upgrade_version";
        public static final String COLUMN_NAME_VERSION = "version";
        public static final String COLUMN_NAME_IS_IGNORED = "is_ignored";
    }

    public static abstract class UpgradeBufferEntry {
        public static final String TABLE_NAME = "upgrade_buffer";
        public static final String COLUMN_NAME_DOWNLOAD_URL = "download_url";
        public static final String COLUMN_NAME_FILE_MD5 = "file_md5";
        public static final String COLUMN_NAME_FILE_LENGTH = "file_length";
        public static final String COLUMN_NAME_BUFFER_LENGTH = "buffer_length";
        public static final String COLUMN_NAME_BUFFER_PART = "buffer_part";
        public static final String COLUMN_NAME_LAST_MODIFIED = "last_modified";
    }

}
