package com.example.notificationtest.biz;

import android.support.annotation.NonNull;

import com.common.log.SDLog;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public enum  RxjavaBiz {

    INSTANCE;

    private static final String TAG = "RxjavaBiz";

    public void testRxjava(){
        Observable.create(new ObservableOnSubscribe<Integer>() { // 第一步：初始化Observable
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                SDLog.i("Observable emit 1");
                e.onNext(1);
                SDLog.i("Observable emit 2");
                e.onNext(2);
                SDLog.i("Observable emit 3");
                e.onNext(3);
                e.onComplete();
                SDLog.i("Observable emit 4");
                e.onNext(4);
            }
        }).subscribe(new Observer<Integer>() { // 第三步：订阅

            // 第二步：初始化Observer
            private int i;
            private Disposable mDisposable;

            @Override
            public void onSubscribe(@NonNull Disposable d) {
                SDLog.i();
                mDisposable = d;
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                SDLog.i();
                i++;
                if (i == 2) {
                    // 在RxJava 2.x 中，新增的Disposable可以做到切断的操作，让Observer观察者不再接收上游事件
                    mDisposable.dispose();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                SDLog.i(e.getMessage());
            }

            @Override
            public void onComplete() {
                SDLog.i();
            }
        });
    }

}
