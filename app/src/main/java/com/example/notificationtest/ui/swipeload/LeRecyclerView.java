package com.example.notificationtest.ui.swipeload;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.Nullable;

public class LeRecyclerView extends RecyclerView implements Pullable {
    public LeRecyclerView(Context context) {
        super(context);
    }

    public LeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public boolean isNoLoad, isNoMore;

    @Override
    public boolean canPullDown() {   // 没有子布局的时候可以下拉刷新
        if(isNoLoad){
            return false;
        }
        if(getAdapter().getItemCount() == 0){
            return true;
        }
        if (getLayoutManager() instanceof LinearLayoutManager) {
            int firstVisibleItem = ((LinearLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if (firstVisibleItem == 0) {
                return true;
            }
        }
        if (getLayoutManager() instanceof GridLayoutManager) {
            int firstVisibleItem = ((GridLayoutManager) getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if (firstVisibleItem == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canPullUp() {
        if(isNoMore){
            return false;
        }
        if(getAdapter().getItemCount() == 0){
            return true;
        }
        if (getLayoutManager() instanceof LinearLayoutManager) {
            int lastVisibleItem = ((LinearLayoutManager) getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItem == getAdapter().getItemCount() - 1) {
                return true;
            }
        }
        if (getLayoutManager() instanceof GridLayoutManager) {
            int lastVisibleItem = ((GridLayoutManager) getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItem == getAdapter().getItemCount() - 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
}
