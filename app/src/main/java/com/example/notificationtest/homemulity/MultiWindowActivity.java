package com.example.notificationtest.homemulity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.ComponentActivity;

import com.common.utils.Utils;
import com.example.notificationtest.R;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MultiWindowActivity extends ComponentActivity {
    public static final String TAG = "MultiMain";
    private MyConstraintLayout myConstraint;
    private Button btn_add, btn_show;
    private List<TextView> textViews = new ArrayList<>();
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

    private void addView(){
        TextView textView = new TextView(this);
        textViews.add(textView);
        textView.setText("布局"+textViews.size());
        myConstraint.addView(textView);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) textView.getLayoutParams();
        params.width = Utils.getScreenWidth(this);
        params.height = Utils.getScreenHeight(this);
        textView.setLayoutParams(params);
    }

    private void initData() {
        screenWidth = Utils.getScreenWidth(this);
        screenHeight = Utils.getScreenHeight(this);
        sreenRatio = new BigDecimal((float) screenWidth / screenHeight);
        Log.i(TAG, "----initData scaleSize = " + scaleSize.doubleValue());
        scaleSize = new BigDecimal(0.8f);
    }
    private void initView() {
        myConstraint = findViewById(R.id.myConstraint);
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addView();
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
        addView();
    }

    /**更新多窗口的展示*/
    public void updateMultiType(){
        for(int i = 0; i < textViews.size(); i ++){
            TextView textView = textViews.get(i);
            if(isMultiType){
                animaSmall(textView, (float) scaleSize.doubleValue());
            }else{
                animaSmall(textView, 1/(float) scaleSize.doubleValue());
            }
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
