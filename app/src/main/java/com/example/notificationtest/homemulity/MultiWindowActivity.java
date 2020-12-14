package com.example.notificationtest.homemulity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.ComponentActivity;
import androidx.viewpager.widget.ViewPager;

import com.common.utils.Utils;
import com.example.notificationtest.R;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MultiWindowActivity extends ComponentActivity {
    public static final String TAG = "MultiMain";

    private List<LeWindowInfo> windowInfos = new ArrayList<>();
    private HomeViewPager homePager;
    private HomePagerAdapter pagerAdapter;
    private OverlayTransformer transformer;


    private Button btn_add, btn_show;
    private int currentIndex;
    private BigDecimal sreenRatio;//当前手机屏幕的宽高比
    private boolean isMultiType = false;//是否是多窗口模式
    private int screenWidth, screenHeight;
    private BigDecimal scaleSize;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_window);
        initData();
        initView();
    }
    private void initData() {
        screenWidth = Utils.getScreenWidth(this);
        screenHeight = Utils.getScreenHeight(this);
        sreenRatio = new BigDecimal((float) screenWidth / (float) screenHeight);
        scaleSize = new BigDecimal(0.8f);
        LeWindowInfo windowInfo = new LeWindowInfo();
        windowInfos.add(windowInfo);
        currentIndex = 0;
        Log.i(TAG, "----initData scaleSize = " + scaleSize.doubleValue());
    }
    private void initView() {
        homePager = findViewById(R.id.home_pager);
        transformer = new OverlayTransformer(3, -1, -1);
        pagerAdapter = new HomePagerAdapter(this, windowInfos);
        homePager.setAdapter(pagerAdapter);
        homePager.setNoScroll(true);
        homePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
    }
    private void addWindow(){
        if(windowInfos != null){
            currentIndex = windowInfos.size() - 1;
            LeWindowInfo windowInfo = new LeWindowInfo(currentIndex);
            windowInfos.add(windowInfo);
            pagerAdapter.notifyChange(windowInfos);
            homePager.setCurrentItem(currentIndex);
            Log.i(TAG, "----addWindow currentIndex = " + currentIndex);
        }
    }


    /**更新多窗口的展示*/
    private void updateMultiType(){
        if(homePager != null && transformer != null){
            if(isMultiType){
                homePager.setNoScroll(false);
                homePager.setPageTransformer(true, transformer);
            }else{
                homePager.setNoScroll(true);
                homePager.setPageTransformer(false, transformer);
            }
            Log.i(TAG, "----updateMultiType isMultiType = " + isMultiType);
        }
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
