package com.floatingwindow;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.floatingwindow.biz.FloatBiz;

public class FloatingWindowActivity extends AppCompatActivity {
    public static final String URI = "floating://window.com/floatingwindowactivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_main);
        FloatBiz.INIT.initFloat(getApplication());
    }
}
