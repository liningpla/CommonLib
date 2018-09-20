package com.kotlin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class KTActivity extends AppCompatActivity {

    public  static String URI = CommonDefine.INSTANCE.getHOST()+"KTActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kotlin_main);
    }
}
