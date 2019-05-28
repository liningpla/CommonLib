package com.floatingwindow;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FloatingWindowActivity extends AppCompatActivity {
    public static final String URI = "floating://window.com/floatingwindowactivity";
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
