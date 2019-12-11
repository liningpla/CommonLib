package com.example.notificationtest.ui.swipeload;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import java.io.Serializable;

/**
 * 可暂停的倒计时 Timer
 * Created by lining on 2017/6/27.
 */

public abstract class PauseTimer implements Serializable {

    private static final int MSG = 1;

    public static final int COUNTDOWN = 1;  // 倒计时中
    public static final int PAUSE = 2;  // 暂停中
    public static final int CANCEL = 3;  // 已取消

    public int currentState = 0;  // 当前状态

    // 总时长
    private final long mTotalTime;
    // 倒计时间隔
    private final long mCountdownInterval;
    // 预计结束时间（真实时间）
    private long mStopTimeInFuture;
    // 暂停时时间（真实时间）
    private long mPausingTime;
    // 恢复时时间（真实时间）
    private long mResumingTime;

    private HandlerThread handlerThread;//独立线程
    private Looper currLooper;
    // handles counting down
    private Handler mHandler;


    /**
     * @param totalTime the total time 毫秒
     * @param countDownInterval the interval time 毫秒
     * @param isThread the current countdown is a separate thread
     * */
    public PauseTimer(long totalTime, long countDownInterval, boolean isThread) {
        mTotalTime = totalTime;
        mCountdownInterval = countDownInterval;
        currLooper = Looper.myLooper();
        if(isThread){
            handlerThread = new HandlerThread("PauseTimer");
            handlerThread.start();
            currLooper = handlerThread.getLooper();
        }
        initHandler(currLooper);
    }

    /**
     * Start the countdown.
     */
    public synchronized final PauseTimer start() {
        currentState = COUNTDOWN;
        if (mTotalTime <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mTotalTime;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    /**
     * Pause the countdown.
     */
    public synchronized final void pause() {
        if (currentState == COUNTDOWN) {  // 只有在 倒计时中 才能暂停
            mPausingTime = SystemClock.elapsedRealtime();
            currentState = PAUSE;
        }
    }

    /**
     * Resume the countdown.
     */
    public synchronized final void resume() {
        if (currentState == PAUSE) {  // 只有在 暂停中 才能恢复
            mResumingTime = SystemClock.elapsedRealtime();
            currentState = COUNTDOWN;

            mStopTimeInFuture += mResumingTime - mPausingTime;
            mHandler.sendMessage(mHandler.obtainMessage(MSG));
        }
    }

    /**
     * 跟 美摄sdk 对 时间轴 专用函数
     * Resume the countdown.
     */
    public synchronized final void resume(long RemainingTime) {
        if (currentState == PAUSE) {  // 只有在 暂停中 才能恢复
            currentState = COUNTDOWN;

            if (RemainingTime <= 0) {
                onFinish();
            }
            mStopTimeInFuture = SystemClock.elapsedRealtime() + RemainingTime;
            mHandler.sendMessage(mHandler.obtainMessage(MSG));
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
     * @param millisUntilFinished The amount of time until finished.
     */
    public abstract void onTick(long millisUntilFinished);

    /**
     * Callback fired when the time is up.
     */
    public abstract void onFinish();

    private void initHandler(Looper looper){
        mHandler = new Handler(looper) {

            @Override
            public void handleMessage(Message msg) {

                synchronized (PauseTimer.this) {
                    if (currentState == CANCEL || currentState == PAUSE) {
                        return;
                    }

                    final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

                    if (millisLeft <= 0) {
                        onFinish();
                        cancel();
                    } else if (millisLeft < mCountdownInterval) {
                        // no tick, just delay until done
                        sendMessageDelayed(obtainMessage(MSG), millisLeft);
                    } else {
                        long lastTickStart = SystemClock.elapsedRealtime();
                        onTick(millisLeft);

                        // take into account user's onTick taking time to execute
                        long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();

                        // special case: user's onTick took more than interval to
                        // complete, skip to next interval
                        while (delay < 0) delay += mCountdownInterval;

                        sendMessageDelayed(obtainMessage(MSG), delay);
                    }
                }
            }
        };
    }
}