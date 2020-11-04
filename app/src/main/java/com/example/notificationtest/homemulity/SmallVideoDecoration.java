package com.example.notificationtest.homemulity;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SmallVideoDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public SmallVideoDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //不是第一个的格子都设一个左边和底部的间距
        outRect.bottom = 2 * space;
        outRect.left = space;
        outRect.right = space;
    }

}
