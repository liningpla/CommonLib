package com.example.notificationtest.gameSdk.ui;

import com.example.notificationtest.httplib.SerializedName;

import java.util.List;

public class RechargeBean {
    /**
     * cardTypeCombine : 0
     * cardTypeCombineName : 移动
     * denomination : ["10","30","50","100","300","500"]
     */

    @SerializedName("cardTypeCombine")
    public String cardTypeCombine;
    @SerializedName("cardTypeCombineName")
    public String cardTypeCombineName;
    @SerializedName("denomination")
    public List<String> denomination;
}
