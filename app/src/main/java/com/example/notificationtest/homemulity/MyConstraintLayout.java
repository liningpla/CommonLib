package com.example.notificationtest.homemulity;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.customview.widget.ViewDragHelper;


public class MyConstraintLayout extends ConstraintLayout {
    private ViewDragHelper mViewDragHelper;

    public MyConstraintLayout(Context context) {
        super(context);
        createDrager();
    }

    public MyConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        createDrager();
    }

    public MyConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createDrager();
    }

    private void createDrager(){
        mViewDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {
            private int mLeft;
            private int mTop;
            @Override
            public void onViewCaptured(View capturedChild, int activePointerId) {
                super.onViewCaptured(capturedChild, activePointerId);
                mLeft = capturedChild.getLeft();
                mTop = capturedChild.getTop();
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;

            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                mViewDragHelper.settleCapturedViewAt(mLeft, mTop);
                invalidate();
            }

            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                return false;
            }
        });
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }


    @Override
    public void computeScroll() {
        if (mViewDragHelper != null && mViewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }
}
