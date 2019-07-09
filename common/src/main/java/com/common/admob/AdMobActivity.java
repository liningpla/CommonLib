package com.common.admob;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.captureinfo.R;

public class AdMobActivity extends AppCompatActivity {


    private Button btn_banner, btn_interstitial, btn_rewardedAd, btn_nativead;
    private FrameLayout fl_parent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_main);
        Advertiser.initAdMob(this);
        fl_parent = findViewById(R.id.fl_parent);

        btn_banner = findViewById(R.id.btn_banner);
        btn_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Advertiser.load(AdMobActivity.this)
                        .container(fl_parent)
                        .adUnitId("ca-app-pub-3940256099942544/6300978111")
                        .banner();
            }
        });

        btn_interstitial = findViewById(R.id.btn_interstitial);
        btn_interstitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Advertiser.load(AdMobActivity.this)
                        .adUnitId("ca-app-pub-3940256099942544/1033173712")
                        .interstitial();
            }
        });

        btn_rewardedAd = findViewById(R.id.btn_rewardedad);
        btn_rewardedAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Advertiser.load(AdMobActivity.this)
                        .adUnitId("ca-app-pub-3940256099942544/5224354917")
                        .rewarded();
            }
        });

        btn_nativead = findViewById(R.id.btn_nativead);
        btn_nativead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Advertiser.load(AdMobActivity.this)
                        .adUnitId("ca-app-pub-3940256099942544/2247696110")
                        .nativeAd();
            }
        });
    }
}
