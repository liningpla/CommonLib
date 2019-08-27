package com.example.notificationtest.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**海外版首页视图构造基础类*/
public class LeAboradBaseView {

    public Context mContext;
    public ViewGroup rootView;

    public LeAboradBaseView(Context context, int layoutId){
        mContext = context;
        rootView = (ViewGroup) LayoutInflater.from(mContext).inflate(layoutId, null);
    }
    public LeAboradBaseView(Context context, ViewGroup layout){
        mContext = context;
        rootView = layout;
    }

    public ViewGroup getContentView(){
        return rootView;
    }

    public Context getContext(){
        return mContext;
    }

    /**加载视图*/
    public void laodView(){}
}
