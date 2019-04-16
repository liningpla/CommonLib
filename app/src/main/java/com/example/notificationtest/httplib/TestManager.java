package com.example.notificationtest.httplib;

import android.util.Log;

public enum TestManager {
    instance;

    String url = "http://vb.lenovo.com/mobile_cache.xhtml";
    public void testPostHttp(){

/*        HiHttp.<String>post(url).params("appID","1410232134070.app.ln")
                                .params("authName","ZAgAAAAAAAGE9MTAxMDQ4MDY2NDkmYj0yJmM9NCZkPTEyMjAzJmU9OTAzODBBMzM3NkM0QjJFMzVDRUY2NUM4MzAxMzdCOUQxJmg9MTU1NTMwMDg0ODY4NyZpPTQzMjAwJmo9MCZvPTg2OTk5NDAzMDAxOTQzJnA9aW1laSZxPTAmdXNlcm5hbWU9MTgyMTAyNzUzNTYmaWw9Y26ZaxpXUlY8yRj4oH6iDDBf")
                                .params("configVer", "36")
                                .params("appConfigVer", "1")
                                .params("isNewSdkFlag", "true")
                                .params("cashierVer", "4.0")
                                .toJson("body").execute(new HiCallBack<String>(String.class) {
            @Override
            void onSuccess(Response response) {
                super.onSuccess(response);
                Log.i(HiHttp.TAG, (String) response.body());
            }

            @Override
            void onError(Response response) {
                super.onError(response);
            }
        });*/



        String body = "{\"body\":{\"activityInfo\":{},\"appConfigVer\":\"1\",\"appName\":\"游戏示例测试\",\"channelList\":[{\"discount\":100,\"enName\":\"WeChatPayment\",\"feeRate\":0,\"minFee\":0,\"name\":\"微信支付\",\"order\":\"0\",\"twName\":\"微信支付\",\"type\":28,\"visible\":3},{\"discount\":100,\"enName\":\"Alipay\",\"feeRate\":0,\"maxAliQuickAmount\":10000,\"minFee\":0,\"name\":\"支付宝\",\"order\":\"1\",\"twName\":\"支付寶\",\"type\":8,\"visible\":3},{\"discount\":100,\"enName\":\"BankCardEasyLink\",\"feeRate\":0,\"minFee\":500,\"name\":\"银行卡易联\",\"order\":\"3\",\"twName\":\"銀行卡易聯\",\"type\":7,\"visible\":3},{\"discount\":100,\"enName\":\"PhoneRechargeCard\",\"feeRate\":0,\"minFee\":0,\"name\":\"手机充值卡\",\"order\":\"5\",\"twName\":\"手機充值卡\",\"type\":10,\"visible\":3},{\"discount\":100,\"enName\":\"GameCard\",\"feeRate\":10.00,\"minFee\":0,\"name\":\"游戏点卡\",\"order\":\"5\",\"twName\":\"遊戲點卡\",\"type\":27,\"visible\":3},{\"discount\":100,\"enName\":\"\",\"feeRate\":0,\"minFee\":0,\"name\":\"\",\"order\":\"5\",\"twName\":\"\",\"type\":0,\"visible\":3}],\"configVer\":\"36\",\"goodsList\":[{\"feeID\":\"539\",\"feeName\":\"生产环境\",\"feeTip\":\"0.0元/次\",\"feeType\":1,\"price\":0},{\"feeID\":\"123456789\",\"feeName\":\"0.01    元\",\"feeTip\":\"0.01元/次\",\"feeType\":0,\"price\":1},{\"feeID\":\"10\",\"feeName\":\" 3元 \",\"feeTip\":\"0.01元/次\",\"feeType\":0,\"price\":1},{\"feeID\":\"298\",\"feeName\":\"7元 \",\"feeTip\":\"0.5元/次\",\"feeType\":0,\"price\":50},{\"feeID\":\"299\",\"feeName\":\"15元\",\"feeTip\":\"15.0元/次\",\"feeType\":0,\"price\":1500},{\"feeID\":\"300\",\"feeName\":\"30元\",\"feeTip\":\"30.0元/次\",\"feeType\":0,\"price\":3000},{\"feeID\":\"301\",\"feeName\":\"100元\",\"feeTip\":\"1.0元/次\",\"feeType\":0,\"price\":100},{\"feeID\":\"302\",\"feeName\":\"200元\",\"feeTip\":\"200.0元/次\",\"feeType\":0,\"price\":20000},{\"feeID\":\"99080\",\"feeName\":\"测试商品1\",\"feeTip\":\"2001.0元/次\",\"feeType\":0,\"price\":200100},{\"feeID\":\"99081\",\"feeName\":\"测试商品2\",\"feeTip\":\"501.0元/次\",\"feeType\":0,\"price\":50100}],\"paymentTip\":\"暂无公告\",\"userInfo\":{\"balance\":47,\"cashBalance\":1182,\"isActive\":1,\"userID\":\"18210275356\",\"userType\":0}},\"resultCode\":0}";
        UserInfo userInfo = HiJson.jsonObject(UserInfo.class, body);
        Log.i(HiHttp.TAG, userInfo.body.channelList.get(0).getChannelName());
        Log.i(HiHttp.TAG, HiJson.objectJson(userInfo));
    }

}
