package com.floatingwindow.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

/**
 * 首页视图构造基础类
 */
public class LeBaseView implements LifecycleOwner {

    public Context mContext;
    public View contentView;
    ViewGroup parentView;

    /**
     * 调用
     *
     * @param layoutId 当前View的layout ID;
     */
    public LeBaseView(Context context, ViewGroup parentView, int layoutId) {
        this.parentView = parentView;
        mContext = context;
        contentView = LayoutInflater.from(mContext).inflate(layoutId, null);
        setAttachStateChangeListener();
    }

    /**
     * 调用
     *
     * @param layoutId 当前View的layout ID;
     */
    public LeBaseView(Context context, int layoutId) {
        mContext = context;
        contentView = LayoutInflater.from(mContext).inflate(layoutId, null);
        setAttachStateChangeListener();
    }

    public void setContentView(int lyaoutId) {
        contentView = LayoutInflater.from(mContext).inflate(lyaoutId, null);
        setAttachStateChangeListener();
    }

    private void setAttachStateChangeListener() {
        if (contentView != null) {
            contentView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View v) {
                    initView();
                }

                @Override
                public void onViewDetachedFromWindow(View v) {
                    onRemove();
                }
            });
        }
    }

    public void setContentView(View view) {
        contentView = view;
    }

    /**
     * 执行添加到父布局
     */
    public void addToParent() {
        if (parentView != null && contentView != null) {
            parentView.addView(contentView);
        }
    }

    /**
     * 执行添加到父布局
     */
    public void addToParent(ViewGroup.LayoutParams layoutParams) {
        if (parentView != null && contentView != null) {
            parentView.addView(contentView, layoutParams);
        }
    }

    /**
     * 执行添加到父布局
     */
    public void remove() {
        if (parentView != null && contentView != null) {
            parentView.removeView(contentView);
        }
    }


    public View getContentView() {
        return contentView;
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 查找View
     */
    public <T extends View> T findView(int id) {
        return contentView.findViewById(id);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    /**
     * 被父元素添加成功后一定要调用
     */
    public void initView() {
    }

    public void onRemove() {
    }
}
