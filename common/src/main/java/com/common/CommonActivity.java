package com.common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.captureinfo.R;
import com.common.log.SDLog;

public class CommonActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_main);
        SDLog.create().i("common","CommonActivity","--onCreate--");
    }
}
