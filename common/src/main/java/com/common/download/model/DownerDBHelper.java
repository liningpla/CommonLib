package com.common.download.model;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 */
public class DownerDBHelper extends SQLiteOpenHelper {

    /**
     * 数据库名称
     */
    public static final String DB_NAME = "upgrade.db";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;

    /**
     * 版本缓存表
     */
    private static final String SQL_CREATE_UPGRADE_BUFFER = "CREATE TABLE IF NOT EXISTS " +
            DownerContrat.DownerBufferEntry.TABLE_NAME + " (" +
            DownerContrat.DownerBufferEntry.COLUMN_NAME_DOWNLOAD_URL + " TEXT NOT NULL," +
            DownerContrat.DownerBufferEntry.COLUMN_NAME_FILE_MD5 + " TEXT," +
            DownerContrat.DownerBufferEntry.COLUMN_NAME_FILE_LENGTH + " INTEGER," +
            DownerContrat.DownerBufferEntry.COLUMN_NAME_BUFFER_LENGTH + " INTEGER," +
            DownerContrat.DownerBufferEntry.COLUMN_NAME_BUFFER_PART + " INTEGER," +
            DownerContrat.DownerBufferEntry.COLUMN_NAME_LAST_MODIFIED + " INTEGER,PRIMARY KEY(" +
            DownerContrat.DownerBufferEntry.COLUMN_NAME_DOWNLOAD_URL + "))";
    public DownerDBHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }
    public DownerDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        this(context, name, factory, version, null);
    }
    public DownerDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
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
