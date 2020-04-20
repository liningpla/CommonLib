package com.example.notificationtest.gameSdk.ui;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.notificationtest.R;
import com.example.notificationtest.gameSdk.ui.base.LeBaseView;

/**
 * 支付中心-支付订单二维码显示页面
 * 本页面两种状态：1，微信支付生成的二维码
 * 2，支付宝支付生成的二维码
 */
public class PayOrderQrCodeView extends LeBaseView {

    private TextView tv_pay_qr_amount;
    private TextView tv_pay_qr_v_coin;
    private ImageView iv_qr_code;

    public PayOrderQrCodeView(Context context) {
        super(context, R.layout.layout_pay_order_qr_code);
    }

    @Override
    public void initView() {
        tv_pay_qr_amount = findView(R.id.tv_pay_qr_amount);
        tv_pay_qr_v_coin = findView(R.id.tv_pay_qr_v_coin);
        iv_qr_code = findView(R.id.iv_qr_code);
    }

}
