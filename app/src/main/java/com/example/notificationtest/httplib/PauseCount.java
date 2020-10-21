package com.example.notificationtest.httplib;

import java.io.Serializable;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

/**
 * 可暂停计数器 Timer
 * Created by lining on 2017/6/27.
 */

public abstract class PauseCount implements Serializable {
    private static final int MSG = 1;
    public static final int COUNTDOWN = 1;  // 倒计时中
    public static final int PAUSE = 2;  // 暂停中
    public static final int CANCEL = 3;  // 已取消
    public volatile int currentState = 0;  // 当前状态
    // 总时次数
    private final int mTotalCount;
    // 次数时间隔
    private final long mCountdownInterval;
    private HandlerThread handlerThread;//独立线程
    // 当前正在轮询的次数
    private volatile int currCount;
    private Looper currLooper;
    // handles counting down
    private Handler mHandler;


    /**
     * @param totalCount 总次数
     * @param countDownInterval 每次完成后间隔多少时间  毫秒
     * @param isThread 执行内容是否单独线程
     * */
    public PauseCount(int totalCount, long countDownInterval, boolean isThread) {
    	mTotalCount = totalCount;
        mCountdownInterval = countDownInterval;
        currLooper = Looper.myLooper();
        if(isThread){
            handlerThread = new HandlerThread("PauseCount");
            handlerThread.start();
            currLooper = handlerThread.getLooper();
        }
        initHandler(currLooper);
    }

    /**
     * Start the countdown.
     */
    public synchronized final PauseCount start() {
        currentState = COUNTDOWN;
        if (mTotalCount <= 0) {
            onFinish();
            return this;
        }
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    /**
     * Pause the countdown.
     */
    public synchronized final void pause() {
        if (currentState == COUNTDOWN) {  // 只有在 倒计时中 才能暂停
            currentState = PAUSE;
        }
    }

    /**
     * 跟 美摄sdk 对 时间轴 专用函数
     * Resume the countdown.
     */
    public synchronized final void resume() {
        if (currentState == PAUSE) {  // 只有在 暂停中 才能恢复
            currentState = COUNTDOWN;
            if (mTotalCount <= 0) {
                onFinish();
            }
        }
    }

    /**
     * Cancel the countdown.
     */
    public synchronized final void cancel() {
        currentState = CANCEL;
        mHandler.removeMessages(MSG);
        if(handlerThread != null){
            handlerThread.quit();
        }
    }

    /**
     * Callback fired on regular interval.
     * @param currCount The amount of time until finished.
     */
    public abstract void onTick(int currCount);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();

    private void initHandler(Looper looper){
        mHandler = new Handler(looper) {

            @Override
            public void handleMessage(Message msg) {

                synchronized (PauseCount.this) {
                    if (mTotalCount <= currCount) {
                        onFinish();
                        cancel();
                    } else {
                        if (currentState == CANCEL || currentState == PAUSE) {
                            return;
                        }
                        currCount ++;
                        onTick(currCount);
                        if(mTotalCount == currCount){
                            sendMessage(obtainMessage(MSG));
                        }else{
                            sendMessageDelayed(obtainMessage(MSG), mCountdownInterval);
                        }
                    }
                }
            }
        };
    }
}