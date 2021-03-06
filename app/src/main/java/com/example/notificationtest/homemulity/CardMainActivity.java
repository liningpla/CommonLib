package com.example.notificationtest.homemulity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notificationtest.R;
import com.example.notificationtest.cardstack.CardStackView;
import com.example.notificationtest.cardstack.UpDownStackAnimatorAdapter;
import com.example.notificationtest.oldmutil.LeWindowInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CardMainActivity extends AppCompatActivity implements CardStackView.ItemExpendListener {

    public static final String TAG = "MultiWindow";
    public static Integer[] TEST_DATAS = new Integer[]{
            R.color.color_1,
            R.color.color_2,
            R.color.color_3,
            R.color.color_4,
            R.color.color_5,
            R.color.color_6,
            R.color.color_7,
            R.color.color_8,
            R.color.color_9,
            R.color.color_10,
    };
    private CardStackView mStackView;
    private CardStackAdapter mTestStackAdapter;
    private Button btn_ad, btn_expand;
    private List<LeWindowInfo> windowInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_main);

        mStackView = (CardStackView) findViewById(R.id.stackview_main);
        btn_ad = findViewById(R.id.btn_ad);
        btn_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddClick();
            }
        });
        btn_expand = findViewById(R.id.btn_expand);
        btn_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onExpandClick();
            }
        });

        mStackView.setItemExpendListener(this);
        mTestStackAdapter = new CardStackAdapter(this);
        mStackView.setAdapter(mTestStackAdapter);
        List<Integer> datas = Arrays.asList(TEST_DATAS);
        for(Integer integer:datas){
            LeWindowInfo windowInfo = new LeWindowInfo();
            windowInfo.bgColor = integer;
            windowInfos.add(windowInfo);
        }
        mTestStackAdapter.updateData(windowInfos);
        mStackView.setAnimatorAdapter(new UpDownStackAnimatorAdapter(mStackView));
        mStackView.setExpendType(true);
        mStackView.setCurrPotition(windowInfos.size() - 1);
    }

    @Override
    public void onItemExpend(boolean expend) {
        Log.i(LeCardView.TAG, "--Activity-onItemExpend-expend = " + expend);
    }

    public void onAddClick(){
//        mStackView.pre();
        mStackView.setExpendType(true);

    }
    public void onExpandClick(){
//        mStackView.next();
        mStackView.setExpendType(false);
    }
}

