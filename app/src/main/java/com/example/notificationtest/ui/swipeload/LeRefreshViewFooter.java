package com.example.notificationtest.ui.swipeload;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

public class LeRefreshViewFooter extends FrameLayout {

    public static final int STATE_NORMAL = 0;//上拉
    public static final int STATE_READY = 1;//释放
    public static final int STATE_LOADING = 2;//正在

    public static final int STATE_SUCCEED = 3;//加载成功
    public static final int STATE_LOAD_FAILED = 4;//加载失败
    public static final int STATE_NO_MORE_DATA = 5;//没有数据

    public LeRefreshViewFooter(@NonNull Context context) {
        super(context);
    }

    public LeRefreshViewFooter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LeRefreshViewFooter(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setState(@IntRange(from=0, to=5) int state){}

    public void show(){}

    public void hide(){}
    public int getIntrinsicHeight() {
        return 0;
    }
    public void clearAnimation(){}
}
