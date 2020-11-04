package com.example.notificationtest.homemulity;

import android.content.Context;
import android.view.ViewGroup;

import com.example.notificationtest.R;
import com.floatingwindow.views.LeBaseView;

public class LeHomeView extends LeBaseView {
    /**
     * 调用
     * @param context
     * @param parentView
     */
    public LeHomeView(Context context, ViewGroup parentView) {
        super(context, parentView, R.layout.layout_multi_home);
    }
}
