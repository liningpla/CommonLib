package com.example.notificationtest.homemulity;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.notificationtest.R;
import com.floatingwindow.views.LeBaseView;

public class LeHomeView extends LeBaseView {

    private FrameLayout fl_content;
    private TextView tv_home_name;
    private LeWindowInfo windowInfo;
    private int position;

    public static LeHomeView buildFragemnt(ViewGroup parentView, LeWindowInfo windowInfo) {
        return new LeHomeView(parentView.getContext(), parentView, windowInfo);
    }
    public static LeHomeView buildFragemnt(Context context, LeWindowInfo windowInfo) {
        return new LeHomeView(context, windowInfo);
    }
    /**
     * 调用
     *
     * @param context
     * @param parentView
     */
    public LeHomeView(Context context, ViewGroup parentView, LeWindowInfo windowInfo) {
        super(context, parentView, R.layout.layout_multi_home);
        this.windowInfo = windowInfo;
        this.position = windowInfo.position;
    }
    /**
     * 调用
     *
     * @param context
     */
    public LeHomeView(Context context, LeWindowInfo windowInfo) {
        super(context, R.layout.layout_multi_home);
    }
    /**
     * 调用
     *
     * @param context
     * @param parentView
     */
    public LeHomeView(Context context, ViewGroup parentView) {
        super(context, parentView, R.layout.layout_multi_home);
    }

    @Override
    public void initView() {
        super.initView();
        fl_content = findView(R.id.fl_content);
        tv_home_name = findView(R.id.tv_home_name);
        tv_home_name.setText("第" + position + "个页面");
        fl_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(MultiWindowActivity.TAG, "----Click = " + position);
            }
        });
        Log.i(MultiWindowActivity.TAG, "----initView position = " + position);
    }
}
