package com.example.notificationtest.httplib;

public enum TestManager {
    instance;

    String url = "http://vb.lenovo.com/mobile_cache.xhtml";

    public String testPostHttp() {
        testPost();
//        testFormUplaod();
        try{
            return "1";
        }catch (Exception e){
        }finally {
            return "2";
        }
    }

    private void testPost(){
        HiHttp.<UserInfo>post(url).params("appID", "1410232134070.app.ln")
                .params("authName", "ZAgAAAAAAAGE9MTAxMDQ4MDY2NDkmYj0yJmM9NCZkPTEyMjAzJmU9OTAzODBBMzM3NkM0QjJFMzVDRUY2NUM4MzAxMzdCOUQxJmg9MTU1NTMwMDg0ODY4NyZpPTQzMjAwJmo9MCZvPTg2OTk5NDAzMDAxOTQzJnA9aW1laSZxPTAmdXNlcm5hbWU9MTgyMTAyNzUzNTYmaWw9Y26ZaxpXUlY8yRj4oH6iDDBf")
                .params("configVer", "36")
                .params("appConfigVer", "1")
                .params("isNewSdkFlag", "true")
                .params("cashierVer", "4.0")
                .toJson("body").execute(new HiCallBack<UserInfo>(UserInfo.class) {
            @Override
            void onSuccess(Response response) {
                super.onSuccess(response);

                HiLog.i(HiHttp.TAG, response.body().toString());

//                UserInfo userInfo = HiJson.jsonObject(UserInfo.class, (String) response.body());

                UserInfo userInfo = (UserInfo) response.body();

                HiLog.i(HiHttp.TAG, userInfo.body.channelList.get(0).getChannelName());
                HiLog.i(HiHttp.TAG, HiJson.objectJson(userInfo));
                HiViewModel.init(HiHttp.instance.mApplication).post(userInfo);
            }

            @Override
            void onError(Response response) {
                super.onError(response);
            }
        });
    }

    private void testFormUplaod(){
//        //表单上传
//        HiHttp.<String>post("").params("","").multipart("",new File("")).execute(
//                new HiCallBack<String>(String.class) {
//            @Override
//            void onSuccess(Response response) {
//                super.onSuccess(response);
//            }
//
//            @Override
//            void onError(Response response) {
//                super.onError(response);
//            }
//
//            @Override
//            void uploadProgress(long progress, long totalLength) {
//                super.uploadProgress(progress, totalLength);
//            }
//        });

        String value ="LenovoAuth LPSUST=“ZAgAAAAAAAGE9MTAwMTc0NjE2ODMmYj0yJmM9NCZkPTEwMTQxJmU9MDI3ODdDNzdCOTc2M0I5ODNFODc3OTZCNTZGMzkxMjMxJmg9MTU2MjMxMzQxMzI0OSZpPTYwNDgwMCZvPTg2NTI3MjA0MDA1MTMxOSZwPWltZWkmcT0wJnVzZXJuYW1lPTE4OTExNzc1Mjc3JmlsPWNuYXUCQLLscIwHiFglwtC5Bg”";
//        HiHttp.<String>post("http://test.uc.zui.lenovomm.com/calapi/v3/synchecksum?ys=false")
//                .headers("Authorization", value)
//                .headers("X-Lenovows-Authorization",value)
//               .paramsJson("{\"data\":[]}").execute(new HiCallBack<String>(String.class) {
//            @Override
//            void onSuccess(Response response) {
//                super.onSuccess(response);
//                HiLog.i(HiHttp.TAG, (String) response.body());
//            }
//
//            @Override
//            void onError(Response response) {
//                super.onError(response);
//            }
//        });


//        OkGo.<String>post("http://test.uc.zui.lenovomm.com/calapi/v3/synchecksum?ys=false")
//                .headers("Authorization", value)
//                .headers("X-Lenovows-Authorization", value)
//                .upJson("{\"data\":[]}")
//                .execute(new HttpCallBack<String>(String.class){
//            @Override
//            public void onSuccess(com.lzy.okgo.model.Response<String> response) {
//                super.onSuccess(response);
//                HiLog.i(HiHttp.TAG, (String) response.body());
//            }
//            @Override
//            public void onError(com.lzy.okgo.model.Response<String> response) {
//                super.onError(response);
//            }
//        });


    }



}
