package com.example.notificationtest.gameSdk;

/**需要PayCenterFrg操作的事件*/
public interface PayFrgAction {
    /**二维码支付显示支付信息详情页*/
    void onQrShowDetails();
    /**显示优惠券界面*/
    void onShowCouponLists();
    /**显示支付方式*/
    void onShowPayTypes();
    /**选择支付方式回调*/
    void onSelectPayType(PaySelectBean typeBeanSelect);
}
