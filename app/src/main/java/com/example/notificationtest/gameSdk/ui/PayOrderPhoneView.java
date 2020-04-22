package com.example.notificationtest.gameSdk.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.notificationtest.R;
import com.example.notificationtest.gameSdk.PayFrgAction;
import com.example.notificationtest.gameSdk.PaySelectBean;
import com.example.notificationtest.gameSdk.ui.base.LeBaseView;

/**
 * 支付中心-支付订单手机页面
 * 本页面两种状态：1，V币充足时，不显示支付方式逻辑
 * 2，V币不充足时，显示支付方式逻辑
 */
public class PayOrderPhoneView extends LeBaseView {

    private TextView tv_order_shop_name;//商品名称
    private TextView tv_v_coin;//V币花费数目
    private RelativeLayout rl_coupon;//优惠券条目
    private TextView tv_order_coupon;//优惠券个数显示
    private LinearLayout ll_pay_tpye;//支付方式条目
    private TextView tv_pay_amount;//支付金额
    private TextView tv_pay_v_coin;//支付金额同等V币数目
    private TextView tv_pay_type;//支付方式名称
    private TextView tv_pay_center_confirm;//确认支付
    private PayFrgAction payFrgAction;//需要PayCenterFrg操作的事件


    public void setPayFrgAction(PayFrgAction payFrgAction) {
        this.payFrgAction = payFrgAction;
    }

    public PayOrderPhoneView(Context context, ViewGroup parentView) {
        super(context, parentView, R.layout.layout_pay_order_phone);
    }

    @Override
    public void initView() {
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
        ll_pay_tpye = findView(R.id.ll_pay_tpye);
        ll_pay_tpye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentPayTypeList();
            }
        });
        tv_pay_type = findView(R.id.tv_pay_type);
        tv_pay_center_confirm = findView(R.id.tv_pay_center_confirm);
        tv_pay_center_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comfirmPay();
            }
        });
    }

    /**
     * 跳转到支付方式界面
     */
    private void intentPayTypeList() {
        if(payFrgAction != null){
            payFrgAction.onShowPayTypes();
        }
    }

    /**
     * 跳转到优惠券列表
     */
    private void intentCouponList() {
        if(payFrgAction != null){
            payFrgAction.onShowCouponLists();
        }
    }

    /**
     * 确认支付
     */
    private void comfirmPay() {
        PayPhoneCardActivity.startPhoneCardActivity(getContext());
    }

    /**接收到支付方式变更通知*/
    public void notifyPayTypeChange(PaySelectBean typeBeanSelect) {
        if(tv_pay_type != null){
            tv_pay_type.setText(typeBeanSelect.payTypeName);
        }
    }
}
