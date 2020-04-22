package com.example.notificationtest.gameSdk.biz;

import com.example.notificationtest.gameSdk.ui.PayCardLimitBean;
import com.example.notificationtest.gameSdk.ui.RechargeBean;
import com.example.notificationtest.gameSdk.ui.SzfRechargeBean;
import com.example.notificationtest.httplib.HiJson;
import com.example.notificationtest.httplib.HiThreadManger;
import com.example.notificationtest.httplib.Priority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 手机重置卡业务类
 */
public enum PhoneCardBiz {

    INIT;

    private String jsonStr = "{\"code\":\"100000\",\"msg\":\"SUCCESS\",\"data\":[{\"cardTypeCombine\":\"0\",\"cardTypeCombineName\":\"移动\",\"denomination\":[\"10\",\"30\",\"50\",\"100\",\"300\",\"500\"]},{\"cardTypeCombine\":\"1\",\"cardTypeCombineName\":\"联通\",\"denomination\":[\"20\",\"30\",\"50\",\"100\",\"300\",\"500\"]},{\"cardTypeCombine\":\"2\",\"cardTypeCombineName\":\"电信\",\"denomination\":[\"10\",\"20\",\"30\",\"50\",\"100\",\"200\",\"500\"]}]}";

    /**合并移动，联调，电信三家充值卡金额，并按照金额大小排序
     * @param szfRechargeBean 原始数据
     * */
    public void initPhoneCardData(SzfRechargeBean szfRechargeBean, PayCardCallBack cardCallBack) {
        HiThreadManger.getInstance().execute(Priority.HIGH, new Runnable() {
            @Override
            public void run() {
                SzfRechargeBean szfRechargeBean = (SzfRechargeBean) HiJson.jsonObject(SzfRechargeBean.class, jsonStr);
                List<PayCardLimitBean> cardLimitBeans = new ArrayList<PayCardLimitBean>();
                if (szfRechargeBean != null && szfRechargeBean.rechargeBeans != null) {
                    for (RechargeBean rechargeBean : szfRechargeBean.rechargeBeans) {
                        if (rechargeBean.denomination != null) {
                            for (String carNumber : rechargeBean.denomination) {
                                PayCardLimitBean cardLimitBean = new PayCardLimitBean(carNumber);
                                if(!cardLimitBeans.contains(cardLimitBean)){
                                    cardLimitBeans.add(cardLimitBean);
                                }
                            }
                        }
                    }
                    Collections.sort(cardLimitBeans, new Comparator<PayCardLimitBean>() {
                        public int compare(PayCardLimitBean o1, PayCardLimitBean o2) {
                            try {
                                int cardLimit1 = Integer.parseInt(o1.cardLimit);
                                int cardLimit2 = Integer.parseInt(o2.cardLimit);
                                // 按照大小进行升序排列
                                if (cardLimit1 < cardLimit2) {
                                    return -1;
                                }
                                if (cardLimit1 == cardLimit2) {
                                    return 0;
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            return 1;
                        }
                    });
                    cardCallBack.onCallBack(cardLimitBeans);
                }
            }
        });
    }

    public interface PayCardCallBack{
        void onCallBack(List<PayCardLimitBean> cardLimitBeans);
    }

}
