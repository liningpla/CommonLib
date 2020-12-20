package com.example.notificationtest.oldmutil;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.ComponentActivity;

import com.common.utils.Utils;
import com.example.notificationtest.R;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MyMultiWindowActivity extends ComponentActivity {
    public static final String TAG = "MultiMain";

    private List<LeHomeView> homeViews = new ArrayList<>();
    private MyMultiView myScrollView;


    private Button btn_add, btn_show;
    private int currentIndex;
    private boolean isMultiType = false;//是否是多窗口模式
    private int screenWidth, screenHeight;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_window);
        initView();
    }
    private void initData() {
        screenWidth = Utils.getScreenWidth(this);
        screenHeight = Utils.getScreenHeight(this);
        LeHomeView leHomeView = LeHomeView.buildFragemnt(myScrollView, new LeWindowInfo(0));
        myScrollView.addContent(leHomeView.contentView);
        homeViews.add(leHomeView);
        currentIndex = 0;
        Log.i(TAG, "----initData------");
    }
    private void initView() {
        myScrollView = findViewById(R.id.home_scrollview);
        myScrollView.setScaleX(0.8f);
        myScrollView.setScaleY(0.8f);
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMultiType = false;
                addWindow();
                updateMultiType();
            }
        });
        btn_show = findViewById(R.id.btn_show);
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMultiType = true;
                updateMultiType();
            }
        });
        initData();
    }
    private void addWindow(){
        if(homeViews != null){
            currentIndex = homeViews.size();
            LeHomeView leHomeView = LeHomeView.buildFragemnt(myScrollView, new LeWindowInfo(currentIndex));
            myScrollView.addContent(leHomeView.contentView);
            homeViews.add(leHomeView);
            Log.i(TAG, "----addWindow currentIndex = " + currentIndex);
        }
    }


    /**更新多窗口的展示*/
    private void updateMultiType(){
            if(isMultiType){
            }else{
            }
            Log.i(TAG, "----updateMultiType isMultiType = " + isMultiType);
    }

    /**
     * @param view 动画执行View
     * @param scaleSize 大小
     * */
    public void animaSmall(View view, float scaleSize){
        view.setPivotX(screenWidth/2);
        view.setPivotY(screenHeight/2);
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "scaleX", 1, scaleSize);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "scaleY", 1, scaleSize);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(animX).with(animY);
        animSet.setDuration(300);
        animSet.start();
    }
}
