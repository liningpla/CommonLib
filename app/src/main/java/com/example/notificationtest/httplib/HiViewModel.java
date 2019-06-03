package com.example.notificationtest.httplib;

import android.app.Application;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class HiViewModel<T> extends ViewModel {

    // 创建LiveData
    private MutableLiveData<T> mAccount = new MutableLiveData<>();
    private static HiViewModel mModel;

    public static HiViewModel init(Application application){
        if(mModel == null){
            mModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(HiViewModel.class);
        }
        return mModel;
    }


    /**传递信息*/
    public void post(T t){
        mModel.mAccount.postValue(t);
    }

    /**接收信息*/
    public void observe(LifecycleOwner owner, Observer observer){
        mModel.mAccount.observe(owner, observer);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mModel = null;
    }
}
