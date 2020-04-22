package com.example.notificationtest.gameSdk.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.notificationtest.R;
import com.example.notificationtest.gameSdk.PayCardActivityAction;

/**
 *手机充值卡界面
 */
public class PayPhoneCardActivity extends FragmentActivity {
    private FrameLayout fl_pay_card_limit;//充值卡额度界面父布局
    private FrameLayout fl_pay_card_number;//充值卡，卡号和密码输入界面父布局
    private PayPhoneCardLimitView cardLimitView;//充值卡额度界面
    private PayPhoneCardInputView cardInputView;//充值开信息输入界面


    /**启动手机充值卡界面*/
    public static void startPhoneCardActivity(Context context){
        Intent intent = new Intent(context, PayPhoneCardActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_phone_card);
        fl_pay_card_limit = findViewById(R.id.fl_pay_card_limit);
        fl_pay_card_number = findViewById(R.id.fl_pay_card_number);
        addCardLimitView();
    }

    private PayCardActivityAction cardActivityAction = new PayCardActivityAction() {
        @Override
        public void onCloseActivity() {
            finish();
        }

        @Override
        public void onShowInputView(PayCardLimitBean cardLimitBean) {
            addCardInputView(cardLimitBean);
        }
    };

    /**添加充值卡额度界面*/
    private void addCardLimitView(){
        if(fl_pay_card_limit != null){
            fl_pay_card_limit.removeAllViews();
            cardLimitView = new PayPhoneCardLimitView(this, fl_pay_card_limit);
            cardLimitView.addToParent();
            cardLimitView.setCardActivityAction(cardActivityAction);
        }
    }

    /**添加充值卡信息输入界面*/
    private void addCardInputView(PayCardLimitBean cardLimitBean){
        if(fl_pay_card_number != null){
            fl_pay_card_number.removeAllViews();
            cardInputView = new PayPhoneCardInputView(this, fl_pay_card_number, cardLimitBean);
            cardInputView.addToParent();
        }
    }

    @Override
    public void onBackPressed() {
        if(fl_pay_card_number != null && fl_pay_card_number.getChildCount() > 0 && cardInputView != null){
            cardInputView.remove();
            return;
        }
        super.onBackPressed();
    }
}
