package com.common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.captureinfo.R;
import com.common.log.SDCardLogHelper;

public class CommonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_main);
        SDCardLogHelper.getInstance().writeMessage("common","CommonActivity","--onCreate--");
    }
}
