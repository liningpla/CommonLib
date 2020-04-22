package com.example.notificationtest.gameSdk;

import com.example.notificationtest.gameSdk.ui.PayCardLimitBean;

/**需要PayPhoneCardActivity操作的事件*/
public interface PayCardActivityAction {
    /**关闭Activity*/
    void onCloseActivity();
    /**显示输入卡号和密码界面*/
    void onShowInputView(PayCardLimitBean cardLimitBean);
}
