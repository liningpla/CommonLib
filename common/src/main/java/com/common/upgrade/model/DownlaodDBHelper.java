package com.common.upgrade.model;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 */
public class DownlaodDBHelper extends SQLiteOpenHelper {

    /**
     * 数据库名称
     */
    public static final String DB_NAME = "upgrade.db";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;

    /**
     * 版本忽略表
     */
    private static final String SQL_CREATE_UPGRADE_VERSION = "CREATE TABLE IF NOT EXISTS " +
            DownlaodPersistenceContrat.UpgradeVersionEntry.TABLE_NAME + " (" +
            DownlaodPersistenceContrat.UpgradeVersionEntry.COLUMN_NAME_VERSION + " INTEGER NOT NULL," +
            DownlaodPersistenceContrat.UpgradeVersionEntry.COLUMN_NAME_IS_IGNORED + " INTEGER,PRIMARY KEY(" +
            DownlaodPersistenceContrat.UpgradeVersionEntry.COLUMN_NAME_VERSION + "))";

    /**
     * 版本缓存表
     */
    private static final String SQL_CREATE_UPGRADE_BUFFER = "CREATE TABLE IF NOT EXISTS " +
            DownlaodPersistenceContrat.UpgradeBufferEntry.TABLE_NAME + " (" +
            DownlaodPersistenceContrat.UpgradeBufferEntry.COLUMN_NAME_DOWNLOAD_URL + " TEXT NOT NULL," +
            DownlaodPersistenceContrat.UpgradeBufferEntry.COLUMN_NAME_FILE_MD5 + " TEXT," +
            DownlaodPersistenceContrat.UpgradeBufferEntry.COLUMN_NAME_FILE_LENGTH + " INTEGER," +
            DownlaodPersistenceContrat.UpgradeBufferEntry.COLUMN_NAME_BUFFER_LENGTH + " INTEGER," +
            DownlaodPersistenceContrat.UpgradeBufferEntry.COLUMN_NAME_BUFFER_PART + " INTEGER," +
            DownlaodPersistenceContrat.UpgradeBufferEntry.COLUMN_NAME_LAST_MODIFIED + " INTEGER,PRIMARY KEY(" +
            DownlaodPersistenceContrat.UpgradeBufferEntry.COLUMN_NAME_DOWNLOAD_URL + "))";

    public DownlaodDBHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }

    public DownlaodDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        this(context, name, factory, version, null);
    }

    public DownlaodDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_UPGRADE_VERSION);
        db.execSQL(SQL_CREATE_UPGRADE_BUFFER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
