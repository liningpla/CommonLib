package com.example.notificationtest.gameSdk.ui;

import com.example.notificationtest.httplib.SerializedName;

import java.util.List;

/**神州付配置信息*/
public class SzfRechargeBean {
    /**
     * code : 100000
     * msg : SUCCESS
     * data : [{"cardTypeCombine":"0","cardTypeCombineName":"移动","denomination":["10","30","50","100","300","500"]},{"cardTypeCombine":"1","cardTypeCombineName":"联通","denomination":["20","30","50","100","300","500"]},{"cardTypeCombine":"2","cardTypeCombineName":"电信","denomination":["10","20","30","50","100","200","500"]}]
     */

    @SerializedName("code")
    public String code;
    @SerializedName("msg")
    public String msg;
    @SerializedName("data")
    public List<RechargeBean> rechargeBeans;
}
