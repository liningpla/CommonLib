package com.floatingwindow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class FloatingWindowActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_main);

        Intent intent = new Intent();
        String pkg = getPackageName();
        intent.setClassName(pkg, "com.floatingwindow.services.AssistantService");
        startService(intent);

    }
}
