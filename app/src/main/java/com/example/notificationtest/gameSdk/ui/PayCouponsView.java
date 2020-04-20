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
import com.example.notificationtest.gameSdk.CouponBean;
import com.example.notificationtest.gameSdk.ui.base.LeBaseView;
import com.example.notificationtest.gameSdk.view.CheckView;

import java.util.ArrayList;
import java.util.List;

/**
 * 优惠券列表界面
 */
public class PayCouponsView extends LeBaseView {

    private ImageView iv_coupons_back;
    private RecyclerView rv_coupons;
    private CouponsAdapter couponsAdapter;
    private List<CouponBean> couponBeans = new ArrayList<CouponBean>();
    public static CouponBean couponBeanSelect;

    public PayCouponsView(Context context, ViewGroup parentView) {
        super(context, parentView, R.layout.layout_pay_coupons);
    }

    @Override
    public void initView() {
        iv_coupons_back = findView(R.id.iv_coupons_back);
        iv_coupons_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove();
            }
        });
        initData();
        rv_coupons = findView(R.id.rv_coupons);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        couponsAdapter = new CouponsAdapter(getContext(), couponBeans);
        rv_coupons.setLayoutManager(layoutManager);
        rv_coupons.setAdapter(couponsAdapter);
    }

    /**初始化列表数据*/
    private void initData(){
        for(int i = 0; i < 1; i ++){
            CouponBean couponBean = new CouponBean();
            couponBean.couponId = i + "";
            if(i == 0){
                couponBean.isSelected = true;
                couponBeanSelect = couponBean;
            }else{
                couponBean.isSelected = false;
            }
            if(i % 2 == 0){
                couponBean.isUnUse = false;
            }else{
                couponBean.isUnUse = true;
            }
            couponBeans.add(couponBean);
        }
    }

    private class CouponsAdapter extends BaseRecyclerAdapter<CouponBean> {

        private Context mContext;
        private List<CouponBean> couponBeans;
        private CouponBean lastCouponBeanSelect;//上次被选中的优惠券

        public CouponsAdapter(Context mContext, List<CouponBean> couponBeans) {
            this.mContext = mContext;
            this.couponBeans = couponBeans;
            notifyDatas(couponBeans);
        }

        @Override
        public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon_view, parent, false);
            RecyclerView.ViewHolder  holder = new CouponsHolder(itemView);
            return holder;
        }

        @Override
        public void onBind(RecyclerView.ViewHolder viewHolder, int RealPosition, CouponBean data) {
            CouponsHolder holder = (CouponsHolder) viewHolder;
            holder.tv_pay_rmb_cny.setTextColor(getColor(!data.isUnUse, R.color.pay_center_blue, R.color.pay_center_gray));
            holder.tv_payment.setTextColor(getColor(!data.isUnUse, R.color.pay_center_blue, R.color.pay_center_gray));
            holder.tv_coupon_rule.setTextColor(getColor(!data.isUnUse, R.color.pay_center_gray, R.color.pay_center_gray));
            holder.tv_coupon_details.setTextColor(getColor(!data.isUnUse, R.color.pay_center_gray, R.color.pay_center_gray));
            holder.tv_coupon_name.setTextColor(getColor(!data.isUnUse, R.color.pay_center_black, R.color.pay_center_gray));
            holder.tv_coupon_data.setTextColor(getColor(!data.isUnUse, R.color.pay_center_gray, R.color.pay_center_gray));
            holder.cv_coupon_select.setVisibility(data.isUnUse?View.GONE:View.VISIBLE);
            holder.tv_coupon_unuse.setVisibility(data.isUnUse?View.VISIBLE:View.GONE);
            holder.tv_coupon_details_content.setVisibility(data.isShowDetails?View.VISIBLE:View.GONE);
            holder.tv_coupon_details_content.setTextColor(getColor(!data.isUnUse, R.color.pay_center_gray, R.color.pay_center_gray));
            holder.cv_coupon_select.setChecked(data.isSelected);
            if(data.isSelected){
                lastCouponBeanSelect = data;
            }
            holder.tv_coupon_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.isShowDetails = !data.isShowDetails;
                    notifyItemChanged(RealPosition);
                }
            });
            holder.itemView.setOnClickListener(!data.isUnUse?new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!lastCouponBeanSelect.equals(data)){
                        lastCouponBeanSelect.isSelected = false;
                        data.isSelected = !data.isSelected;
                    }else {
                        data.isSelected = !data.isSelected;
                    }
                    notifyDataSetChanged();
                }
            }:null);
        }

        @Override
        public int getItemType(int position) {
            return 0;
        }

        private int getColor(boolean isUnUse, int yesColor, int noColor){
            return ContextCompat.getColor(mContext, isUnUse?yesColor:noColor);
        }

        /**优惠券*/
        private class CouponsHolder extends RecyclerView.ViewHolder {
            public View itemView;
            public TextView tv_pay_rmb_cny;// 人民币符号
            public TextView tv_payment;// 优惠券金额
            public TextView tv_coupon_rule;// 优惠券使用规则
            public TextView tv_coupon_details;// 优惠券详情入口
            public TextView tv_coupon_name;// 优惠券名称
            public TextView tv_coupon_data;// 优惠券有效日期
            public CheckView cv_coupon_select;// 优惠券有效日期
            public TextView tv_coupon_unuse;//优惠券不可以使用
            public TextView tv_coupon_details_content;//优惠券具体详情信息

            public CouponsHolder(View itemView) {
                super(itemView);
                this.itemView = itemView;
                tv_pay_rmb_cny = itemView.findViewById(R.id.tv_pay_rmb_cny);
                tv_payment = itemView.findViewById(R.id.tv_payment);
                tv_coupon_rule = itemView.findViewById(R.id.tv_coupon_rule);
                tv_coupon_details = itemView.findViewById(R.id.tv_coupon_details);
                tv_coupon_name = itemView.findViewById(R.id.tv_coupon_name);
                tv_coupon_data = itemView.findViewById(R.id.tv_coupon_data);
                cv_coupon_select = itemView.findViewById(R.id.cv_coupon_select);
                tv_coupon_unuse = itemView.findViewById(R.id.tv_coupon_unuse);
                tv_coupon_details_content = itemView.findViewById(R.id.tv_coupon_details_content);
            }
        }
    }

}
