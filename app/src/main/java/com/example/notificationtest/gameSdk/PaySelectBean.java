package com.example.notificationtest.gameSdk;

import java.io.Serializable;
import java.util.Objects;

public class PaySelectBean implements Serializable {
    public static final int PAY_WEIXIN = 1;
    public static final int PAY_ALI = 2;
    public static final int PAY_PHONE = 3;
    public int payTypeId;
    public boolean isSelected;
    public String payTypeName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaySelectBean that = (PaySelectBean) o;
        return payTypeId == that.payTypeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(payTypeId);
    }
}
