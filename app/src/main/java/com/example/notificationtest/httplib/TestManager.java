package com.example.notificationtest.httplib;

public enum TestManager {
    instance;

    String url = "http://vb.lenovo.com/mobile_cache.xhtml";

    public void testPostHttp() {

        HiHttp.<String>post(url).params("appID", "1410232134070.app.ln")
                .params("authName", "ZAgAAAAAAAGE9MTAxMDQ4MDY2NDkmYj0yJmM9NCZkPTEyMjAzJmU9OTAzODBBMzM3NkM0QjJFMzVDRUY2NUM4MzAxMzdCOUQxJmg9MTU1NTMwMDg0ODY4NyZpPTQzMjAwJmo9MCZvPTg2OTk5NDAzMDAxOTQzJnA9aW1laSZxPTAmdXNlcm5hbWU9MTgyMTAyNzUzNTYmaWw9Y26ZaxpXUlY8yRj4oH6iDDBf")
                .params("configVer", "36")
                .params("appConfigVer", "1")
                .params("isNewSdkFlag", "true")
                .params("cashierVer", "4.0")
                .toJson("body").execute(new HiCallBack<String>(String.class) {
            @Override
            void onSuccess(Response response) {
                super.onSuccess(response);
                HiLog.i(HiHttp.TAG, (String) response.body());
                UserInfo userInfo = HiJson.jsonObject(UserInfo.class, (String) response.body());
                HiLog.i(HiHttp.TAG, userInfo.body.channelList.get(0).getChannelName());
                HiLog.i(HiHttp.TAG, HiJson.objectJson(userInfo));
            }

            @Override
            void onError(Response response) {
                super.onError(response);
            }
        });

/*
        //表单上传
        HiHttp.<String>post("").params("","").multipart("",new File("")).execute(
                new HiCallBack<String>(String.class) {
            @Override
            void onSuccess(Response response) {
                super.onSuccess(response);
            }

            @Override
            void onError(Response response) {
                super.onError(response);
            }

            @Override
            void uploadProgress(long progress, long totalLength) {
                super.uploadProgress(progress, totalLength);
            }
        });
*/

    }

}
