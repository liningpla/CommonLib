package com.example.notificationtest.oldmutil;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

/**
 * 叠加卡片效果
 */
public class OverlayTransformer implements ViewPager.PageTransformer {
    private float transOffect;
    private float scaleSize = 0.8f;//page的缩放比例
    private float scaleOffset = 40;
    private float transOffset = 40;
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
        if (position <= 0.0f) {//当前页
            page.setTranslationX(0f);
            page.setAlpha(1 - 0.5f * Math.abs(position));
            page.setClickable(true);
        } else {
            otherTrans(page, position);
            page.setClickable(false);
        }
    }

    private void otherTrans(View page, float position) {
        //缩放比例
        float scale = (page.getHeight() - scaleOffset * position) / (float) (page.getHeight());
        page.setScaleX(scale);
        page.setScaleY(scale);

        page.setAlpha(1f);
        if (position > overlayCount - 1 && position < overlayCount) { //当前页向右滑动时,最右面第四个及以后页面应消失
            float curPositionOffset = transOffset * (float) Math.floor(position); //向下取整
            float lastPositionOffset = transOffset * (float) Math.floor(position - 1); //上一个卡片的偏移量
            float singleOffset = 1 - Math.abs(position % (int) position);
            float transY = (-page.getHeight() * position) + (lastPositionOffset + singleOffset * (curPositionOffset - lastPositionOffset));
            page.setTranslationY(transY);
        } else if (position <= overlayCount - 1) {
            float transY = (-page.getWidth() * position) + (transOffset * position);
            page.setTranslationY(transY);
        } else {
            page.setAlpha(0f);
//            page.setTranslationX(0); //不必要的隐藏在下面
        }
    }
}
