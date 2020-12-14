package com.example.notificationtest.homemulity;

import android.util.Log;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import com.common.utils.Utils;

/**
 * 叠加卡片效果
 */
public class OverlayTransformer implements ViewPager.PageTransformer {
    private float transOffect;
    private float scaleSize = 0.8f;//page的缩放比例
    private int overlayCount; //显示在前端的page
    private HomeViewPager homePager;

    public OverlayTransformer(HomeViewPager homePager, int overlayCount) {
        this.overlayCount = overlayCount;
        this.homePager = homePager;
    }

    public int getOverlayCount() {
        return overlayCount;
    }

    @Override
    public void transformPage(View page, float position) {
        Log.i(MultiWindowActivity.TAG, "----transformPage position = " + position + "  getHeight = "+page.getHeight());
        int mOffset = Utils.dip2px(page.getContext(), 72);
        if (position <= 0.0f) {
//被滑动的那页，设置水平位置偏移量为0，即无偏移
            page.setTranslationY(0f);
            page.setAlpha(0.1f);
        } else {//未被滑动的页
            page.setTranslationY((-(page.getHeight()-mOffset) * position));
            page.setAlpha(position * 0.1f);
        }


    }
}
