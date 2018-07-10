package com.common.threadPool;

public class Test {

    public static void testCountDownTimer(){
        /**10*1000毫秒倒计时，  每隔1000毫秒执行onTick*/
        PauseAbleCountDownTimer timer = new PauseAbleCountDownTimer(10*1000, 1000, false) {
            @Override
            public void onTick(long millisUntilFinished) {//阻塞当前线程，如果是主线程，避免处理复杂业务逻辑
                //millisUntilFinished 还剩多少毫秒
            }
            @Override
            public void onFinish() {
            }
        };
    }

}
