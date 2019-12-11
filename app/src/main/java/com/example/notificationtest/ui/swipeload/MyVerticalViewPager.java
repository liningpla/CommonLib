package com.example.notificationtest.ui.swipeload;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by lining on 2017/5/26.
 */

public class MyVerticalViewPager extends VerticalViewPager implements Pullable {

    public MyVerticalViewPager(Context context) {
        super(context);
    }

    public MyVerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean isNoLoad, isNoMore;


    @Override
    public boolean canPullDown() {   // 没有子布局的时候可以下拉刷新
        if(isNoLoad){
            return false;
        }
        if(getAdapter().getCount() == 0){
            return true;
        }else {
            //如果当前view是第一个view，执行刷新
            if(this.getCurrentItem() == 0){
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean canPullUp() {
        if(isNoMore){
            return false;
        }
        // 没有子布局的时候不可以上拉加载
        if(getAdapter() != null && getAdapter().getCount() == 0){
            return false;
        }else {
            //如果当前view是最后一个view，执行加载
            if(getAdapter() != null && this.getCurrentItem() == (getAdapter().getCount() - 1)){
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }
}
