package com.common.admob;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.captureinfo.R;
import com.google.android.gms.ads.AdView;

public class AdMobActivity extends AppCompatActivity {

    private AdView mAdView;
    private FrameLayout fl_parent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_main);
        Advertiser.initAdMob(this);
        fl_parent = findViewById(R.id.fl_parent);
        Advertiser.load(this, fl_parent).adUnitId("ca-app-pub-3940256099942544/6300978111").banner();
    }
}
