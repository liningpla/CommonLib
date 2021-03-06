package com.common.download.model;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 */
public class DownerRepository implements DownerDataSource {
    private static DownerRepository instance;
    private DownerDBHelper helper;

    public static DownerRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (DownerRepository.class) {
                if (instance == null) {
                    instance = new DownerRepository(context);
                }
            }
        }
        return instance;
    }

    private DownerRepository(Context context) {
        helper = new DownerDBHelper(context);
    }

    @Override
    public DownerBuffer getUpgradeBuffer(String url) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "SELECT * FROM " +
                DownerContrat.DownerBufferEntry.TABLE_NAME + " WHERE " +
                DownerContrat.DownerBufferEntry.COLUMN_NAME_DOWNLOAD_URL + "=?";
        String[] selectionArgs = new String[]{url};
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, selectionArgs);
            while (cursor.moveToNext()) {
                DownerBuffer upgradeBuffer = new DownerBuffer();
                upgradeBuffer.setDownloadUrl(cursor.getString(cursor.getColumnIndex(DownerContrat.DownerBufferEntry.COLUMN_NAME_DOWNLOAD_URL)));
                upgradeBuffer.setFileMd5(cursor.getString(cursor.getColumnIndex(DownerContrat.DownerBufferEntry.COLUMN_NAME_FILE_MD5)));
                upgradeBuffer.setFileLength(cursor.getLong(cursor.getColumnIndex(DownerContrat.DownerBufferEntry.COLUMN_NAME_FILE_LENGTH)));
                upgradeBuffer.setBufferLength(cursor.getLong(cursor.getColumnIndex(DownerContrat.DownerBufferEntry.COLUMN_NAME_BUFFER_LENGTH)));
                String bufferPart = cursor.getString(cursor.getColumnIndex(DownerContrat.DownerBufferEntry.COLUMN_NAME_BUFFER_PART));
                List<DownerBuffer.BufferPart> bufferParts = new CopyOnWriteArrayList<>();
                JSONArray ja = new JSONArray(bufferPart);
                for (int index = 0; index < ja.length(); index++) {
                    JSONObject jo = ja.getJSONObject(index);
                    long startLength = jo.optLong("start_length");
                    long endLength = jo.optLong("end_length");
                    bufferParts.add(new DownerBuffer.BufferPart(startLength, endLength));
                }
                upgradeBuffer.setBufferParts(bufferParts);
                upgradeBuffer.setLastModified(cursor.getLong(cursor.getColumnIndex(DownerContrat.DownerBufferEntry.COLUMN_NAME_LAST_MODIFIED)));
                return upgradeBuffer;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return null;
    }

    @Override
    public void setUpgradeBuffer(DownerBuffer buffer) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.enableWriteAheadLogging();
        String sql = "INSERT OR REPLACE INTO " +
                DownerContrat.DownerBufferEntry.TABLE_NAME + "(" +
                DownerContrat.DownerBufferEntry.COLUMN_NAME_DOWNLOAD_URL + "," +
                DownerContrat.DownerBufferEntry.COLUMN_NAME_FILE_MD5 + "," +
                DownerContrat.DownerBufferEntry.COLUMN_NAME_FILE_LENGTH + "," +
                DownerContrat.DownerBufferEntry.COLUMN_NAME_BUFFER_LENGTH + "," +
                DownerContrat.DownerBufferEntry.COLUMN_NAME_BUFFER_PART + "," +
                DownerContrat.DownerBufferEntry.COLUMN_NAME_LAST_MODIFIED + ")VALUES(?,?,?,?,?,?)";

        JSONArray ja = new JSONArray();
        List<DownerBuffer.BufferPart> bufferParts = buffer.getBufferParts();
        for (int index = 0; index < bufferParts.size(); index++) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("start_length", bufferParts.get(index).getStartLength());
                jo.put("end_length", bufferParts.get(index).getEndLength());
                ja.put(index, jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Object[] bindArgs = new Object[]{
                buffer.getDownloadUrl(),
                buffer.getFileMd5(),
                buffer.getFileLength(),
                buffer.getBufferLength(),
                ja.toString(),
                buffer.getLastModified(),
        };
        try {
            db.execSQL(sql, bindArgs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                // db.close();
            }
        }

    }
}
