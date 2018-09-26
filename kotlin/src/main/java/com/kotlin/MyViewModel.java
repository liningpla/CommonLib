package com.kotlin;

import android.app.Application;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.util.Log;

public class MyViewModel extends ViewModel {

    // 创建LiveData
    private MutableLiveData<AccountBean> mAccount = new MutableLiveData<>();
    private static MyViewModel mModel;

    public static MyViewModel init(Application application){
        if(mModel == null){
            mModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(MyViewModel.class);
        }
        return mModel;
    }


    /**传递信息*/
    public void post(AccountBean accountBean){
        mModel.mAccount.postValue(accountBean);
    }

    /**接收信息*/
    public void observe( LifecycleOwner owner, Observer observer){
        mModel.mAccount.observe(owner, observer);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i("lining", "---onCleared-----");
        mModel = null;
    }
}
