package com.example.notificationtest.httplib;


import android.content.Context;
import android.content.SharedPreferences;


public class HiSpHelper {
    
    private static HiSpHelper instance;
	private static SharedPreferences mPreferences;
    private final static String sName = "hi_http_cache";
    private HiSpHelper(Context c){
        mPreferences = c.
                getSharedPreferences(c.getPackageName()+sName, Context.MODE_PRIVATE);

    }
    
    public static synchronized HiSpHelper getInstance(Context c){
        if(instance == null){
            instance = new HiSpHelper(c);
        }
        return instance;
    }

    public int getInt(String key,int defaultVal){
        return mPreferences.getInt(key,defaultVal);
    }
    public synchronized void setInt(String key,int val){
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putInt(key,val);
        applyToEditor(edit);
    }
    
    public long getLong(String key,long defaultVal){
        return mPreferences.getLong(key,defaultVal);
    }
    public synchronized void setLong(String key,long val){
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putLong(key,val);
        applyToEditor(edit);
    }

    public String getString(String key,String defaultStr){
        return mPreferences.getString(key,defaultStr);
    }
    public synchronized void setString(String key,String val) {
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putString(key,val);
        applyToEditor(edit);
    }

    public boolean getBoolean(String key, Boolean defaultBoolean){
        return mPreferences.getBoolean(key,defaultBoolean);
    }
    public synchronized void setBoolean(String key,Boolean val) {
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean(key,val);
        applyToEditor(edit);
    }

    public void applyToEditor(SharedPreferences.Editor editor) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.GINGERBREAD) {
            editor.apply();
        } else {
            editor.commit();
        }
    }
}
