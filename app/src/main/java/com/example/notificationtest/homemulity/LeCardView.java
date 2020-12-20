package com.example.notificationtest.homemulity;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import com.example.notificationtest.R;
import com.example.notificationtest.oldmutil.LeWindowInfo;
import com.floatingwindow.views.LeBaseView;

public class LeCardView extends LeBaseView {
    public static final String TAG = CardMainActivity.TAG;
    private LeWindowInfo windowInfo;
    private int position;
    public static LeCardView buildFragemnt(ViewGroup parent, LeWindowInfo windowInfo) {
        return new LeCardView(parent.getContext(), parent, windowInfo);
    }
    public static LeCardView buildFragemnt(ViewGroup parent) {
        return new LeCardView(parent.getContext(), parent);
    }
    /**
     * 调用
     *
     * @param context
     */
    public LeCardView(Context context, ViewGroup parent, LeWindowInfo windowInfo) {
        super(context, parent, R.layout.layout_card_item);
    }

    /**
     * 调用
     * @param context
     */
    public LeCardView(Context context, ViewGroup parent) {
        super(context,parent, R.layout.layout_card_item);
    }


    @Override
    public void initView() {
        super.initView();
        Log.i(TAG, "----initView position = " + position);
    }

    public void onBind(LeWindowInfo data, int position){
        this.windowInfo = data;
        this.position = data.position;
    }
}
