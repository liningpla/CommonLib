package com.example.notificationtest.gameSdk;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.DisplayMetrics;

public class Uitils {

    /**
     * 判断是否存在光传感器来判断是否为模拟器
     * 部分真机也不存在温度和压力传感器。其余传感器模拟器也存在。
     * @return true 为模拟器
     */
    public static Boolean isSimulator(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor8 = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); //光
        if (null == sensor8) {
            return true;
        } else {
            return false;
        }
    }

    public static int getDensityDimen(Context context, int dimen) {
        final DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (Math.round(dimen * dm.density));
    }
}
