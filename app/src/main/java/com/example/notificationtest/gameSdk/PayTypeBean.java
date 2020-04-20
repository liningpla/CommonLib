package com.example.notificationtest.gameSdk;

import java.io.Serializable;
import java.util.Objects;

public class PayTypeBean implements Serializable {
    public String text;
    public int drawableId;

    public PayTypeBean(String text, int drawableId) {
        this.text = text;
        this.drawableId = drawableId;
    }
}
