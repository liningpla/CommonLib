package com.example.notificationtest.gameSdk.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.notificationtest.R;
import com.example.notificationtest.gameSdk.PayActivityAction;

/**
 * 支付操作Activity
 * 1，获取优惠券信息
 * 2，获取用户个人账户信息
 * 3，获取用户默认支付方式
 * 4，判断是否是模拟器
 */
public class PayActionActivity extends FragmentActivity {

    private PayCenterFrg payCenterFrg;//支付中心界面

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_plus);
        addPayCenter();
    }

    private PayActivityAction payActionListener = new PayActivityAction() {
        @Override
        public void onCloseActivity() {
            finish();
        }
    };

    /**
     * 添加支付中心界面
     */
    private void addPayCenter() {
        payCenterFrg = PayCenterFrg.buildFragemnt(this, R.id.fl_pay_dialog_parent);
        payCenterFrg.setPayActionListener(payActionListener);
    }

}
