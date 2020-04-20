package com.example.notificationtest.gameSdk.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.notificationtest.R;
import com.example.notificationtest.gameSdk.PayFrgAction;
import com.example.notificationtest.gameSdk.ui.base.LeBaseView;

/**
 * 支付中心-支付订单二维码支付-订单详情页面
 */
public class PayOrderQrInfoView extends LeBaseView {


    private RelativeLayout rl_qr_info;
    private TextView tv_order_shop_name;//商品名称
    private TextView tv_v_coin;//V币花费数目
    private RelativeLayout rl_coupon;//优惠券条目
    private TextView tv_order_coupon;//优惠券个数显示
    private TextView tv_pay_amount;//支付金额
    private TextView tv_pay_v_coin;//支付金额同等V币数目
    private PayFrgAction payFrgAction;//需要PayCenterFrg操作的事件
    public void setPayFrgAction(PayFrgAction payFrgAction) {
        this.payFrgAction = payFrgAction;
    }
    public PayOrderQrInfoView(Context context, ViewGroup parentView) {
        super(context, parentView, R.layout.layout_pay_order_qr_info);
    }

    @Override
    public void initView() {
        rl_qr_info = findView(R.id.rl_qr_info);
        rl_qr_info.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                remove();
        }});
        tv_order_shop_name = findView(R.id.tv_order_shop_name);
        tv_v_coin = findView(R.id.tv_v_coin);
        rl_coupon = findView(R.id.rl_coupon);
        rl_coupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentCouponList();
            }
        });
        tv_order_coupon = findView(R.id.tv_order_coupon);
        tv_pay_amount = findView(R.id.tv_pay_amount);
        tv_pay_v_coin = findView(R.id.tv_pay_v_coin);
    }

    /**
     * 跳转到优惠券列表
     */
    private void intentCouponList() {
        if(payFrgAction != null){
            payFrgAction.onShowCouponLists();
        }
    }

}
