package com.example.notificationtest.httplib;

import android.util.Log;

public enum TestManager {
    instance;

    String url = "http://vb.lenovo.com/mobile_cache.xhtml";
    public void testPostHttp(){
        HiHttp.<String>post(url).params("appID","1410232134070.app.ln")
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
        });
    }

}
