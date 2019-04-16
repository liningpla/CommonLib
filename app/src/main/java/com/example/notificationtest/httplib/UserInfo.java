package com.example.notificationtest.httplib;

import java.util.List;

public class UserInfo {
    /**
     * body : {"activityInfo":{},"appConfigVer":"1","appName":"游戏示例测试","channelList":[{"discount":100,"enName":"WeChatPayment","feeRate":0,"minFee":0,"name":"微信支付","order":"0","twName":"微信支付","type":28,"visible":3,"maxAliQuickAmount":10000},{"discount":100,"enName":"Alipay","feeRate":0,"maxAliQuickAmount":10000,"minFee":0,"name":"支付宝","order":"1","twName":"支付寶","type":8,"visible":3},{"discount":100,"enName":"BankCardEasyLink","feeRate":0,"minFee":500,"name":"银行卡易联","order":"3","twName":"銀行卡易聯","type":7,"visible":3},{"discount":100,"enName":"PhoneRechargeCard","feeRate":0,"minFee":0,"name":"手机充值卡","order":"5","twName":"手機充值卡","type":10,"visible":3},{"discount":100,"enName":"GameCard","feeRate":10,"minFee":0,"name":"游戏点卡","order":"5","twName":"遊戲點卡","type":27,"visible":3},{"discount":100,"enName":"","feeRate":0,"minFee":0,"name":"","order":"5","twName":"","type":0,"visible":3}],"configVer":"36","goodsList":[{"feeID":"539","feeName":"生产环境","feeTip":"0.0元/次","feeType":1,"price":0},{"feeID":"123456789","feeName":"0.01    元","feeTip":"0.01元/次","feeType":0,"price":1},{"feeID":"10","feeName":" 3元 ","feeTip":"0.01元/次","feeType":0,"price":1},{"feeID":"298","feeName":"7元 ","feeTip":"0.5元/次","feeType":0,"price":50},{"feeID":"299","feeName":"15元","feeTip":"15.0元/次","feeType":0,"price":1500},{"feeID":"300","feeName":"30元","feeTip":"30.0元/次","feeType":0,"price":3000},{"feeID":"301","feeName":"100元","feeTip":"1.0元/次","feeType":0,"price":100},{"feeID":"302","feeName":"200元","feeTip":"200.0元/次","feeType":0,"price":20000},{"feeID":"99080","feeName":"测试商品1","feeTip":"2001.0元/次","feeType":0,"price":200100},{"feeID":"99081","feeName":"测试商品2","feeTip":"501.0元/次","feeType":0,"price":50100}],"paymentTip":"暂无公告","userInfo":{"balance":47,"cashBalance":1182,"isActive":1,"userID":"18210275356","userType":0}}
     * resultCode : 0
     */
    public BodyBean body;
    public int resultCode;
    public static class BodyBean {
        /**
         * activityInfo : {}
         * appConfigVer : 1
         * appName : 游戏示例测试
         * channelList : [{"discount":100,"enName":"WeChatPayment","feeRate":0,"minFee":0,"name":"微信支付","order":"0","twName":"微信支付","type":28,"visible":3},{"discount":100,"enName":"Alipay","feeRate":0,"maxAliQuickAmount":10000,"minFee":0,"name":"支付宝","order":"1","twName":"支付寶","type":8,"visible":3},{"discount":100,"enName":"BankCardEasyLink","feeRate":0,"minFee":500,"name":"银行卡易联","order":"3","twName":"銀行卡易聯","type":7,"visible":3},{"discount":100,"enName":"PhoneRechargeCard","feeRate":0,"minFee":0,"name":"手机充值卡","order":"5","twName":"手機充值卡","type":10,"visible":3},{"discount":100,"enName":"GameCard","feeRate":10,"minFee":0,"name":"游戏点卡","order":"5","twName":"遊戲點卡","type":27,"visible":3},{"discount":100,"enName":"","feeRate":0,"minFee":0,"name":"","order":"5","twName":"","type":0,"visible":3}]
         * configVer : 36
         * goodsList : [{"feeID":"539","feeName":"生产环境","feeTip":"0.0元/次","feeType":1,"price":0},{"feeID":"123456789","feeName":"0.01    元","feeTip":"0.01元/次","feeType":0,"price":1},{"feeID":"10","feeName":" 3元 ","feeTip":"0.01元/次","feeType":0,"price":1},{"feeID":"298","feeName":"7元 ","feeTip":"0.5元/次","feeType":0,"price":50},{"feeID":"299","feeName":"15元","feeTip":"15.0元/次","feeType":0,"price":1500},{"feeID":"300","feeName":"30元","feeTip":"30.0元/次","feeType":0,"price":3000},{"feeID":"301","feeName":"100元","feeTip":"1.0元/次","feeType":0,"price":100},{"feeID":"302","feeName":"200元","feeTip":"200.0元/次","feeType":0,"price":20000},{"feeID":"99080","feeName":"测试商品1","feeTip":"2001.0元/次","feeType":0,"price":200100},{"feeID":"99081","feeName":"测试商品2","feeTip":"501.0元/次","feeType":0,"price":50100}]
         * paymentTip : 暂无公告
         * userInfo : {"balance":47,"cashBalance":1182,"isActive":1,"userID":"18210275356","userType":0}
         */
        public ActivityInfoBean activityInfo;
        @SerializedName(name = "appConfigVer")
        public String appConfigVera;

        public String appName;
        public String configVer;
        public String paymentTip;
        public UserInfoBean userInfo;
        public List<ChannelListBean> channelList;
        public List<GoodsListBean> goodsList;
        public static class ActivityInfoBean {
        }
        public static class UserInfoBean {
            /**
             * balance : 47
             * cashBalance : 1182
             * isActive : 1
             * userID : 18210275356
             * userType : 0
             */
            public int balance;
            public int cashBalance;
            public int isActive;
            public String userID;
            public int userType;
        }
        public static class ChannelListBean {
            /**
             * discount : 100
             * enName : WeChatPayment
             * feeRate : 0
             * minFee : 0
             * name : 微信支付
             * order : 0
             * twName : 微信支付
             * type : 28
             * visible : 3
             * maxAliQuickAmount : 10000
             */
            public int discount;
            public String enName;
            public int feeRate;
            public int minFee;
            @SerializedName(name = "name")
            private String channelName;

            public String getChannelName() {
                return channelName;
            }

            public void setChannelName(String channelName) {
                this.channelName = channelName;
            }

            public String order;
            public String twName;
            public int type;
            public int visible;
            public int maxAliQuickAmount;
        }
        public static class GoodsListBean {
            /**
             * feeID : 539
             * feeName : 生产环境
             * feeTip : 0.0元/次
             * feeType : 1
             * price : 0
             */
            public String feeID;
            public String feeName;
            public String feeTip;
            public int feeType;
            public int price;
        }
    }
}
