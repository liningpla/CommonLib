package com.example.notificationtest.ui.swipeload;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class LeRefreshViewHeader extends FrameLayout {


    public static final int STATE_NORMAL = 0;//下拉
    public static final int STATE_READY = 1;//释放
    public static final int STATE_REFRESHING = 2;//正在

    public static final int STATE_SUCCEED = 3;//刷新成功
    public static final int STATE_FAIL = 4;//刷新失败

    public LeRefreshViewHeader(@NonNull Context context) {
        super(context);
    }

    public LeRefreshViewHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LeRefreshViewHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setState(@IntRange(from=0, to=4) int state){}

    public void setVisibleHeight(int height){}

    public int getVisibleHeight(){ return 0;}

    public int getIntrinsicHeight(){return 0;}

    public void setShowMessage(String msg){}

    public void clearAnimation(){}
}
