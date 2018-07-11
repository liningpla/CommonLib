package com.floatingwindow.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
