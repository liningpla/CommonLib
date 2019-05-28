package com.floatingwindow.services;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

import com.floatingwindow.viewhelpers.AssistentHelper;

public class AssistantService extends Service {
    private AssistentHelper assistentHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        assistentHelper = new AssistentHelper(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        assistentHelper.showWindow();
        return START_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isPortrait = (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)?true:false;
        if(assistentHelper != null){
            assistentHelper.onConfigurationChanged(isPortrait);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
