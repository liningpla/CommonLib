package com.example.notificationtest.gameSdk.ui;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.notificationtest.R;
import com.example.notificationtest.gameSdk.PayActivityAction;
import com.example.notificationtest.gameSdk.PayFrgAction;
import com.example.notificationtest.gameSdk.PaySelectBean;
import com.example.notificationtest.gameSdk.Uitils;
import com.example.notificationtest.gameSdk.ui.base.LeBaseFragment;

public class PayCenterFrg extends LeBaseFragment {

    private static final String FRG_TAG = "PayCenterFrg";

    private ImageView iv_pay_qrcode, iv_pay_close;
    private LinearLayout ll_center_content, ll_qr_code_detais, ll_other_parent;

    private PayOrderPhoneView payOrderView;//手机客户端支付显示内容
    private PayOrderQrView payOrderQrView;//二维码扫描支付显示
    private PayOrderQrInfoView qrInfoView;//二维码扫描支付显示界面-支付详情页
    private PayCouponsView couponsView;//优惠券列表界面
    private PayTypePhoneView typePhoneView;//支付方式界面

    private boolean isSimulator;//是否是模拟器 true是


    /**
     * PayActionActivity操作监听
     */
    private PayActivityAction payActionListener;

    /**
     * PayActionActivity设置监听
     */
    public void setPayActionListener(PayActivityAction payActionListener) {
        this.payActionListener = payActionListener;
    }
    /**
     * 构造Fragment
     */
    public static PayCenterFrg buildFragemnt(Context context, int container) {
        PayCenterFrg baseFragment = (PayCenterFrg) new PayCenterFrg().contentViewTag(R.layout.frg_pay_center, FRG_TAG);
        baseFragment.commit(context, container);
        return baseFragment;
    }

    @Override
    public void initView() {
        ll_center_content = findView(R.id.ll_center_content);
        ll_qr_code_detais = findView(R.id.ll_qr_code_detais);
        ll_other_parent = findView(R.id.ll_other_parent);
        isSimulator = Uitils.isSimulator(getContext());
        iv_pay_qrcode = findView(R.id.iv_pay_qrcode);
        iv_pay_close = findView(R.id.iv_pay_close);
        iv_pay_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(getContext());
                if (payActionListener != null) {
                    payActionListener.onCloseActivity();
                }
            }
        });
        iv_pay_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSimulator = !isSimulator;
                addCenterContent(isSimulator);
            }
        });
        addCenterContent(isSimulator);
    }

    /**添加支付中心内容*/
    private void addCenterContent(boolean isSimulator){
        if(ll_center_content != null){
            ll_center_content.removeAllViews();
        }else{
            return;
        }
        if(iv_pay_qrcode != null){
            iv_pay_qrcode.setImageResource(isSimulator ? R.drawable.ic_pay_phone : R.drawable.ic_pay_qr_code);
        }
        if(!isSimulator){//显示手机支付页面
            showPhone();
        }else {//是模拟器，显示二维码支付页面
            showQrCode();
        }
    }

    /**显示手机支付页面*/
    private void showPhone(){
        payOrderView = new PayOrderPhoneView(getContext(), ll_center_content);
        payOrderView.addToParent();
        payOrderView.setPayFrgAction(payFrgAction);
    }

    /**显示二维码支付页面*/
    private void showQrCode(){
        payOrderQrView = new PayOrderQrView(getContext(), ll_center_content);
        payOrderQrView.addToParent();
        payOrderQrView.setPayFrgAction(payFrgAction);
    }

    /**当前需要此Fragment操作的事件*/
    private PayFrgAction payFrgAction = new PayFrgAction() {
        @Override
        public void onQrShowDetails() {
            qrShowDetails();
        }

        @Override
        public void onShowCouponLists() {
            showCouponList();
        }

        @Override
        public void onShowPayTypes() {
            intentPayTypeList();
        }

        @Override
        public void onSelectPayType(PaySelectBean typeBeanSelect) {
            notifyPayOrderChange(typeBeanSelect);
        }
    };

    /**接收到支付方式通知*/
    private void notifyPayOrderChange(PaySelectBean typeBeanSelect){
        if(payOrderView != null){
            payOrderView.notifyPayTypeChange(typeBeanSelect);
        }
    }

    /**二维码支付时-显示订单详情信息*/
    private void qrShowDetails(){
        if(ll_qr_code_detais != null){
            ll_qr_code_detais.removeAllViews();
            qrInfoView = new PayOrderQrInfoView(getContext(), ll_qr_code_detais);
            qrInfoView.addToParent(getLayoutParams());
            qrInfoView.setPayFrgAction(payFrgAction);
        }
    }

    /**展示优惠券列表*/
    private void showCouponList(){
        if(ll_other_parent != null){
            ll_other_parent.removeAllViews();
            couponsView = new PayCouponsView(getContext(), ll_other_parent);
            couponsView.addToParent(getLayoutParams());
        }
    }

    /**
     * 跳转到支付方式列表
     */
    private void intentPayTypeList() {
        if(ll_other_parent != null) {
            ll_other_parent.removeAllViews();
            typePhoneView = new PayTypePhoneView(getContext(), ll_other_parent);
            typePhoneView.addToParent(getLayoutParams());
            typePhoneView.setPayFrgAction(payFrgAction);
        }
    }

    private LinearLayout.LayoutParams getLayoutParams(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        return params;
    }

    @Override
    public void applyTheme() {

    }

    @Override
    public void onRemove() {

    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
