package com.example.notificationtest.gameSdk.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.util.EthiopicCalendar;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.notificationtest.R;
import com.example.notificationtest.gameSdk.PayCardActivityAction;
import com.example.notificationtest.gameSdk.SpannableUtils;
import com.example.notificationtest.gameSdk.ui.base.LeBaseView;

import java.util.HashMap;
import java.util.Map;

/**
 * 手机充值卡支付操作界面
 */
public class PayPhoneCardInputView extends LeBaseView {

    private ImageView iv_pay_input_back;
    private TextView tv_input_payment;//支付额度
    private TextView tv_input_error;//支付错误提示
    private EditText et_card_number;//输入序列号
    private EditText et_card_passwd;//输入密码
    private TextView tv_input_immediate;//立即支付
    private TextView  tv_input_hint;//额度换算说明
    private PayCardLimitBean lasetBeanSelected;
    private PayCardActivityAction cardActivityAction;//PayCardActivity操作

    public void setCardActivityAction(PayCardActivityAction cardActivityAction) {
        this.cardActivityAction = cardActivityAction;
    }

    public PayPhoneCardInputView(Context context, ViewGroup parentView, PayCardLimitBean lasetBeanSelected) {
        super(context, parentView, R.layout.layout_pay_phone_card_input);
        this.lasetBeanSelected = lasetBeanSelected;
    }
    @SuppressLint("WrongConstant")
    @Override
    public void initView() {

        tv_input_payment = findView(R.id.tv_input_payment);
        tv_input_payment.setText(SpannableUtils.formatStr(getContext(), R.string.pay_rmb_cny_number, lasetBeanSelected.cardLimit));
        tv_input_error = findView(R.id.tv_input_error);

        et_card_number = findView(R.id.et_card_number);
        et_card_passwd = findView(R.id.et_card_passwd);

        tv_input_immediate = findView(R.id.tv_input_immediate);

        tv_input_hint = findView(R.id.tv_input_hint);
        tv_input_hint.setText(getAccoutnText());
        tv_input_hint.setMovementMethod(LinkMovementMethod.getInstance());//加上这句话才有效果

        iv_pay_input_back = findView(R.id.iv_pay_input_back);
        iv_pay_input_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               remove();
            }
        });
    }

    private SpannableStringBuilder getAccoutnText(){
        String cionStr = SpannableUtils.formatStr(getContext(), R.string.pay_card_input_limit_hint, "4700");
        String accountStr = SpannableUtils.formatStr(getContext(), R.string.pay_card_input_hint, cionStr);
        Map<String, String> changeInfo = new HashMap<String, String>();
        changeInfo.put(cionStr, "#4C96E0");
        SpannableStringBuilder textStr = SpannableUtils.textColorChange(accountStr, changeInfo);
        return textStr;
    }

}
