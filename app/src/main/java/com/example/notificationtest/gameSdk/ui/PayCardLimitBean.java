package com.example.notificationtest.gameSdk.ui;

import java.io.Serializable;
import java.util.Objects;

/**封装手机充值金额数据*/
public class PayCardLimitBean implements Serializable {

    public String cardLimit;
    public boolean isSelected;

    public PayCardLimitBean(String cardLimit) {
        this.cardLimit = cardLimit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayCardLimitBean that = (PayCardLimitBean) o;
        return Objects.equals(cardLimit, that.cardLimit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardLimit);
    }
}
