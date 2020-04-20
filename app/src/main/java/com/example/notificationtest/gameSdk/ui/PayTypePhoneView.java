package com.example.notificationtest.gameSdk.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notificationtest.R;
import com.example.notificationtest.gameSdk.BaseRecyclerAdapter;
import com.example.notificationtest.gameSdk.PayFrgAction;
import com.example.notificationtest.gameSdk.PaySelectBean;
import com.example.notificationtest.gameSdk.ui.base.LeBaseView;
import com.example.notificationtest.gameSdk.view.CheckView;

import java.util.ArrayList;
import java.util.List;

/**
 * 手机支付-支付方式选择界面
 * 本页面两种状态：1，V币充足时，不显示支付方式逻辑
 * 2，V币不充足时，显示支付方式逻辑
 */
public class PayTypePhoneView extends LeBaseView {


    private ImageView iv_pay_type_back;
    private RecyclerView rv_pay_type;
    private PayTypeAdapter payTypeAdapter;
    private List<PaySelectBean> payTypeBeans = new ArrayList<PaySelectBean>();
    private PaySelectBean typeBeanSelect;//上次被选中的优惠券
    private PayFrgAction payFrgAction;//需要PayCenterFrg操作的事件
    public void setPayFrgAction(PayFrgAction payFrgAction) {
        this.payFrgAction = payFrgAction;
    }
    public PayTypePhoneView(Context context, ViewGroup parentView) {
        super(context, parentView, R.layout.layout_pay_type_list);
    }

    @Override
    public void initView() {
        iv_pay_type_back = findView(R.id.iv_pay_type_back);
        iv_pay_type_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove();
            }
        });
        initData();
        rv_pay_type = findView(R.id.rv_pay_type);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        payTypeAdapter = new PayTypeAdapter(getContext(), this, payTypeBeans);
        rv_pay_type.setLayoutManager(layoutManager);
        rv_pay_type.setAdapter(payTypeAdapter);
    }

    /**
     * 初始化列表数据
     */
    private void initData() {
        for (int i = 0; i < 3; i++) {
            PaySelectBean payTypeBean = new PaySelectBean();
            if (i == 0) {
                payTypeBean.isSelected = true;
            }
            payTypeBean.payTypeId = i + 1;
            payTypeBeans.add(payTypeBean);
        }
    }

    private class PayTypeAdapter extends BaseRecyclerAdapter<PaySelectBean> {

        private Context mContext;
        private List<PaySelectBean> payTypeBeans;
        private PayTypePhoneView typePhoneView;


        public PayTypeAdapter(Context mContext, PayTypePhoneView typePhoneView, List<PaySelectBean> payTypeBeans) {
            this.mContext = mContext;
            this.payTypeBeans = payTypeBeans;
            this.typePhoneView = typePhoneView;
            notifyDatas(payTypeBeans);
        }

        @Override
        public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pay_type_view, parent, false);
            RecyclerView.ViewHolder holder = new PayTypeHolder(itemView);
            return holder;
        }

        @Override
        public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, PaySelectBean data) {
            PayTypeHolder holder = (PayTypeHolder) viewHolder;
            switch (data.payTypeId) {
                case PaySelectBean.PAY_WEIXIN:
                    holder.iv_pay_type.setImageResource(R.drawable.ic_pay_weixin);
                    data.payTypeName = mContext.getString(R.string.pay_type_weixin);
                    break;
                case PaySelectBean.PAY_ALI:
                    holder.iv_pay_type.setImageResource(R.drawable.ic_pay_ali);
                    data.payTypeName = mContext.getString(R.string.pay_type_ali);
                    break;
                case PaySelectBean.PAY_PHONE:
                    holder.iv_pay_type.setImageResource(R.drawable.ic_pay_phone_card);
                    data.payTypeName = mContext.getString(R.string.pay_type_phone);
                    break;
            }
            holder.tv_pay_type.setText(data.payTypeName);
            if (data.isSelected) {
                typeBeanSelect = data;
            }
            holder.cv_pay_type_select.setChecked(data.isSelected);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    typeBeanSelect.isSelected = false;
                    if (!typeBeanSelect.equals(data)) {
                        data.isSelected = !data.isSelected;
                    } else {
                        data.isSelected = !data.isSelected;
                    }
                    notifyDataSetChanged();
                    if(typeBeanSelect != null && payFrgAction != null){
                        payFrgAction.onSelectPayType(typeBeanSelect);
                    }
                    if (typePhoneView != null) {
                        typePhoneView.remove();
                    }
                }
            });
        }

        @Override
        public int getItemType(int position) {
            return 0;
        }

        private int getColor(boolean isUnUse, int yesColor, int noColor) {
            return ContextCompat.getColor(mContext, isUnUse ? yesColor : noColor);
        }

        /**
         * 优惠券
         */
        private class PayTypeHolder extends RecyclerView.ViewHolder {
            public View itemView;
            public ImageView iv_pay_type;// 支付方式图标
            public TextView tv_pay_type;// 支付方式名称
            public CheckView cv_pay_type_select;// 支付方式选择状态

            public PayTypeHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                iv_pay_type = itemView.findViewById(R.id.iv_pay_type);
                tv_pay_type = itemView.findViewById(R.id.tv_pay_type);
                cv_pay_type_select = itemView.findViewById(R.id.cv_pay_type_select);
            }
        }
    }

}
