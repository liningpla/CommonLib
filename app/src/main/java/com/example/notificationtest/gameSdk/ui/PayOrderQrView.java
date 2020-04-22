package com.example.notificationtest.gameSdk.ui;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.notificationtest.R;
import com.example.notificationtest.gameSdk.PayFrgAction;
import com.example.notificationtest.gameSdk.PayTypeBean;
import com.example.notificationtest.gameSdk.SpannableUtils;
import com.example.notificationtest.gameSdk.Uitils;
import com.example.notificationtest.gameSdk.ui.base.LeBaseView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 支付中心-支付订单二维码页面
 * 本页面两种状态：1，V币充足时，不显示
 * 2，V币不充足时，显示
 */
public class PayOrderQrView extends LeBaseView {

    private TabLayout tab_pay_qr;
    private ViewPager vp_pay_qr;
    private LinearLayout ll_pay_details;//付款详情

    private List<PayOrderQrCodeView> qrCodeViews = new ArrayList<PayOrderQrCodeView>();
    private List<PayTypeBean> payTypeBeans = new ArrayList<PayTypeBean>();
    private MyPagerAdapter myPagerAdapter;
    private PayFrgAction payFrgAction;//需要PayCenterFrg操作的事件
    public void setPayFrgAction(PayFrgAction payFrgAction) {
        this.payFrgAction = payFrgAction;
    }

    public PayOrderQrView(Context context, ViewGroup parentView) {
        super(context, parentView, R.layout.layout_pay_order_qr);
    }

    @Override
    public void initView() {
        tab_pay_qr = findView(R.id.tab_pay_qr);
        vp_pay_qr = findView(R.id.vp_pay_qr);
        ll_pay_details = findView(R.id.ll_pay_details);
        ll_pay_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(payFrgAction != null){
                    payFrgAction.onQrShowDetails();
                }
            }
        });
        payTypeBeans.add(new PayTypeBean(getContext().getString(R.string.pay_name_weixin), R.drawable.ic_pay_weixin));
        payTypeBeans.add(new PayTypeBean(getContext().getString(R.string.pay_name_ali), R.drawable.ic_pay_ali));
        qrCodeViews.add(new PayOrderQrCodeView(mContext));
        qrCodeViews.add(new PayOrderQrCodeView(mContext));
        myPagerAdapter = new MyPagerAdapter(getContext(), qrCodeViews, payTypeBeans);
        vp_pay_qr.setAdapter(myPagerAdapter);
        tab_pay_qr.setupWithViewPager(vp_pay_qr);//此方法就是让tablayout和ViewPager联动
        myPagerAdapter.notifyDataSetChanged();
        setTabWidth(tab_pay_qr);
    }

    /**
     * 重新处理tablayout属性
     */
    private void setTabWidth(final TabLayout tabLayout) {
        tabLayout.setVisibility(View.INVISIBLE);
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //拿到tabLayout的mTabStrip属性
                    LinearLayout mTabStrip = (LinearLayout) tabLayout.getChildAt(0);
                    for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                        final View tabView = mTabStrip.getChildAt(i);
                        tabView.setPadding(0, 0, 0, 0);//固定不变
                        //设置tab左右间距 注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                        params.leftMargin = Uitils.getDensityDimen(getContext(), 38);
                        params.rightMargin = Uitils.getDensityDimen(getContext(), 38);
                        tabView.setLayoutParams(params);
                        tabView.invalidate();
                        tabView.setBackgroundResource(0);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    tabLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public class MyPagerAdapter extends PagerAdapter {
        private List<PayOrderQrCodeView> qrCodeViews;
        private List<PayTypeBean> payTypeBeans;
        private Context context;

        public MyPagerAdapter(Context context, List<PayOrderQrCodeView> qrCodeViews, List<PayTypeBean> payTypeBeans) {
            super();
            this.qrCodeViews = qrCodeViews;
            this.payTypeBeans = payTypeBeans;
            this.context = context;
        }

        @Override
        public int getCount() {
            return qrCodeViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(qrCodeViews.get(position).contentView);
            return qrCodeViews.get(position).contentView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(qrCodeViews.get(position).contentView);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            PayTypeBean payTypeBean = payTypeBeans.get(position);
            SpannableStringBuilder ss = SpannableUtils.imageToTextHead(context, payTypeBean.text, payTypeBean.drawableId,
                    Uitils.getDensityDimen(context, 18), Uitils.getDensityDimen(context, 18));
            return ss;
        }
    }

}
