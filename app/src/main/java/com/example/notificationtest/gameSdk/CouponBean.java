package com.example.notificationtest.gameSdk;

import java.io.Serializable;
import java.util.Objects;

public class CouponBean implements Serializable {

    public boolean isSelected;
    public boolean isUnUse;
    public boolean isShowDetails;
    public String couponId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CouponBean that = (CouponBean) o;
        return Objects.equals(couponId, that.couponId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(couponId);
    }
}
