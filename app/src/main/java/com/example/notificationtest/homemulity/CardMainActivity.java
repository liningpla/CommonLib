package com.example.notificationtest.homemulity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notificationtest.R;
import com.example.notificationtest.cardstack.CardStackView;
import com.example.notificationtest.cardstack.UpDownStackAnimatorAdapter;

import java.util.Arrays;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_main);

        mStackView = (CardStackView) findViewById(R.id.stackview_main);
        mStackView.setItemExpendListener(this);
        mTestStackAdapter = new CardStackAdapter(this);
        mStackView.setAdapter(mTestStackAdapter);
        mTestStackAdapter.updateData(Arrays.asList(TEST_DATAS));

//        mStackView.setAnimatorAdapter(new AllMoveDownAnimatorAdapter(mStackView));
//        mStackView.setAnimatorAdapter(new UpDownAnimatorAdapter(mStackView));
        mStackView.setAnimatorAdapter(new UpDownStackAnimatorAdapter(mStackView));
        mStackView.setSelectPosition(5);
        mStackView.updateSelectPosition(5);
    }

    @Override
    public void onItemExpend(boolean expend) {
        Log.i(LeCardView.TAG, "--Activity-onItemExpend-expend = " + expend);
    }
}

