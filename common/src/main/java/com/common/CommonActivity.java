package com.common;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.captureinfo.R;
import com.common.log.SDLog;

public class CommonActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_main);
        SDLog.i("CommonActivity","--onCreate--");
    }
}
