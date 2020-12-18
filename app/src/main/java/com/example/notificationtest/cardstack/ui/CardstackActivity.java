package com.example.notificationtest.cardstack.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.ComponentActivity;

import com.example.notificationtest.R;
import com.example.notificationtest.cardstack.CardStackView;
import com.example.notificationtest.cardstack.UpDownAnimatorAdapter;
import com.example.notificationtest.homemulity.LeWindowInfo;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CardstackActivity extends ComponentActivity {
    public static final String TAG = "MultiMain";

    private List<LeWindowInfo> windowList = new ArrayList<>();
    private CardStackView card_stack_view;
    private CardStackAdapter stackAdapter;
    private Button btn_add, btn_show;
    private int currentIndex;
    private boolean isMultiType = false;//是否是多窗口模式

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_stack);
        initData();
        initView();
    }

    private void initData() {
        windowList.add(new LeWindowInfo(0));
    }

    private void initView() {
        card_stack_view = findViewById(R.id.card_stack_view);
        stackAdapter = new CardStackAdapter(this, windowList);
        card_stack_view.setAdapter(stackAdapter);
        card_stack_view.setScrollEnable(false);
        stackAdapter.updateData(windowList);
        card_stack_view.setAnimatorAdapter(new UpDownAnimatorAdapter(card_stack_view));
        card_stack_view.setItemExpendListener(new CardStackView.ItemExpendListener(){
            @Override
            public void onItemExpend(boolean expend) {

            }
        });
        btn_add = findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMultiType = false;
                addView();
            }
        });
        btn_show = findViewById(R.id.btn_show);
        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMultiType = true;
            }
        });
    }

    private void addView(){
        windowList.add(new LeWindowInfo(windowList.size() + 1));
        currentIndex = windowList.size() - 1;
        if(stackAdapter != null){
            stackAdapter.updateData(windowList);
        }
    }
}
