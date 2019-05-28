package com.kotlin;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

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
    public void observe(LifecycleOwner owner, Observer observer){
        mModel.mAccount.observe(owner, observer);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i("lining", "---onCleared-----");
        mModel = null;
    }
}
