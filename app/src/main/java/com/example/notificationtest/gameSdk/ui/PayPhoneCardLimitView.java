package com.example.notificationtest.gameSdk.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notificationtest.R;
import com.example.notificationtest.gameSdk.BaseRecyclerAdapter;
import com.example.notificationtest.gameSdk.PayCardActivityAction;
import com.example.notificationtest.gameSdk.SpannableUtils;
import com.example.notificationtest.gameSdk.Uitils;
import com.example.notificationtest.gameSdk.biz.PhoneCardBiz;
import com.example.notificationtest.gameSdk.ui.base.LeBaseView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 手机充值卡支付操作界面
 */
public class PayPhoneCardLimitView extends LeBaseView {


    private ImageView iv_pay_card_back;
    private TextView tv_account, tv_card_next;
    private RecyclerView rv_pay_card;
    private PayCardAdapter payCardAdapter;
    private PayCardLimitBean lasetBeanSelected;
    private List<PayCardLimitBean> mCardLimitBeans = new ArrayList<PayCardLimitBean>();
    private PayCardActivityAction cardActivityAction;//PayCardActivity操作

    public void setCardActivityAction(PayCardActivityAction cardActivityAction) {
        this.cardActivityAction = cardActivityAction;
    }

    public PayPhoneCardLimitView(Context context, ViewGroup parentView) {
        super(context, parentView, R.layout.layout_pay_phone_card_limit);
    }
    @SuppressLint("WrongConstant")
    @Override
    public void initView() {
        tv_account = findView(R.id.tv_account);
        tv_account.setText(getAccoutnText());
        tv_account.setMovementMethod(LinkMovementMethod.getInstance());//加上这句话才有效果
        tv_card_next = findView(R.id.tv_card_next);
        iv_pay_card_back = findView(R.id.iv_pay_card_back);
        iv_pay_card_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardActivityAction != null){
                    cardActivityAction.onCloseActivity();
                }
            }
        });
        rv_pay_card = findView(R.id.rv_pay_card);
        tv_card_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cardActivityAction != null && lasetBeanSelected != null){
                    cardActivityAction.onShowInputView(lasetBeanSelected);
                }
            }
        });
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        payCardAdapter = new PayCardAdapter(getContext(), mCardLimitBeans);
        layoutManager.setOrientation(GridLayout.VERTICAL);
        rv_pay_card.addItemDecoration(new ItemDecoration(Uitils.getDensityDimen(getContext(), 12)));
        rv_pay_card.setLayoutManager(layoutManager);
        rv_pay_card.setAdapter(payCardAdapter);
        initData();
    }

    private SpannableStringBuilder getAccoutnText(){
        String cionStr = SpannableUtils.formatStr(getContext(), R.string.pay_order_v_coin, "-500");
        String accountStr = SpannableUtils.formatStr(getContext(), R.string.pay_card_limit_account, "13811439627");
        String balanceStr = SpannableUtils.formatStr(getContext(), R.string.pay_card_limit_balance, cionStr);
        Map<String, String> changeInfo = new HashMap<String, String>();
        changeInfo.put(accountStr, "#4C96E0");
        changeInfo.put(cionStr, "#FF0000");
        SpannableStringBuilder textStr = SpannableUtils.textColorChange(accountStr+balanceStr, changeInfo);
        return textStr;
    }

    /**
     * 初始化列表数据
     */
    private void initData() {
        PhoneCardBiz.INIT.initPhoneCardData(null, new PhoneCardBiz.PayCardCallBack() {
            @Override
            public void onCallBack(List<PayCardLimitBean> cardLimitBeans) {
                mCardLimitBeans = cardLimitBeans;
                mCardLimitBeans.get(0).isSelected = true;
                payCardAdapter.notifyDatas(mCardLimitBeans);
            }
        });

    }

    private class PayCardAdapter extends BaseRecyclerAdapter<PayCardLimitBean> {

        private Context mContext;
        private List<PayCardLimitBean> mCardLimitBeans;

        public PayCardAdapter(Context mContext, List<PayCardLimitBean> cardLimitBeans) {
            this.mContext = mContext;
            this.mCardLimitBeans = cardLimitBeans;
            notifyDatas(mCardLimitBeans);
        }

        @Override
        public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
            TextView itemView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pay_card_limit_view, parent, false);
            RecyclerView.ViewHolder holder = new PayCardLimitHolder(itemView);
            return holder;
        }

        @Override
        public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, PayCardLimitBean data) {
            PayCardLimitHolder holder = (PayCardLimitHolder) viewHolder;
            if(data.isSelected){
                lasetBeanSelected = data;
            }
            holder.itemView.setText(mContext.getString(R.string.pay_rmb_cny)+data.cardLimit);
            holder.itemView.setBackgroundResource(getBackgroundResource(data.isSelected, R.drawable.corner_blue_3_bg, R.drawable.corner_coupon_gray_3_bg));
            holder.itemView.setTextColor(getColor(data.isSelected, R.color.pay_center_white_bg, R.color.pay_center_blue));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(lasetBeanSelected != null){
                        lasetBeanSelected.isSelected = false;
                        if (!lasetBeanSelected.equals(data)) {
                            data.isSelected = !data.isSelected;
                        } else {
                            data.isSelected = !data.isSelected;
                        }
                    }else{
                        data.isSelected = !data.isSelected;
                        if(data.isSelected){
                            lasetBeanSelected = data;
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemType(int position) {
            return 0;
        }
        private int getColor(boolean isFlag, int yesColor, int noColor){
            return ContextCompat.getColor(mContext, isFlag?yesColor:noColor);
        }
        private int getBackgroundResource(boolean isFlag, int yesColor, int noColor){
            return isFlag?yesColor:noColor;
        }
        private class PayCardLimitHolder extends RecyclerView.ViewHolder {
            public TextView itemView;

            public PayCardLimitHolder(TextView itemView) {
                super(itemView);
                this.itemView = itemView;
            }
        }
    }

}
