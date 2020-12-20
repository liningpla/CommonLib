package com.example.notificationtest.oldmutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemProperties {

    private static Method getLongMethod = null;
    private static Method getStringMethod = null;
    private static Method getIntMethod = null;
    private static Method getBooleanMethod = null;
    public static long getLong(final String key, final long def) {
        try {
            if (getLongMethod == null) {
                getLongMethod = Class.forName("android.os.SystemProperties").getMethod("getLong", String.class, long.class);
            }
            return ((Long) getLongMethod.invoke(null, key, def)).longValue();
        } catch (Exception e) {
            return def;
        }
    }

    public static String getStringMethod(final String key, final String def) {
        try {
            if (getStringMethod == null) {
                getStringMethod = Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class);
            }
            return ((String) getStringMethod.invoke(null, key, def)).toString();
        } catch (Exception e) {
            return def;
        }
    }

    public int getIntMethod(final String key, final int def) {
        try {
            if (getIntMethod == null) {
                getIntMethod = Class.forName("android.os.SystemProperties")
                        .getMethod("getInt", String.class, int.class);
            }
            return ((Integer) getIntMethod.invoke(null, key, def)).intValue();
        } catch (Exception e) {
            return def;
        }
    }

    public boolean getBooleanMethod(final String key, final boolean def) {
        try {
            if (getBooleanMethod == null) {
                getBooleanMethod = Class.forName("android.os.SystemProperties").getMethod("getBoolean", String.class, boolean.class);
            }
            return ((Boolean) getBooleanMethod.invoke(null, key, def)).booleanValue();
        } catch (Exception e) {
            return def;
        }
    }
    //通过反射设置系统属性
    public static void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Bitmap screenshot(int widthPixels, int heightPixels) {
        String surfaceClassName = "";
        if (Build.VERSION.SDK_INT <= 17) {
            surfaceClassName = "android.view.Surface";
        } else {
            surfaceClassName = "android.view.SurfaceControl";
        }
        try {
            Class<?> c = Class.forName(surfaceClassName);
            Method method = c.getMethod("screenshot", new Class[]{int.class, int.class});
            method.setAccessible(true);
            return (Bitmap) method.invoke(null, widthPixels, heightPixels);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static float getDegreesForRotation(int value) {
        switch (value) {
            case Surface.ROTATION_90:
                return 360f - 90f;
            case Surface.ROTATION_180:
                return 360f - 180f;
            case Surface.ROTATION_270:
                return 360f - 270f;
        }
        return 0f;
    }

    public static void takeScreenshot(Context context) {
        WindowManager mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display mDisplay = mWindowManager.getDefaultDisplay();
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        mDisplay.getRealMetrics(mDisplayMetrics);
        Matrix mDisplayMatrix = new Matrix();
        float[] dims = { mDisplayMetrics.widthPixels,
                mDisplayMetrics.heightPixels };

        int value = mDisplay.getRotation();
        String hwRotation = SystemProperties.getStringMethod("ro.sf.hwrotation", "0");
        if (hwRotation.equals("270") || hwRotation.equals("90")) {
            value = (value + 3) % 4;
        }
        float degrees = getDegreesForRotation(value);

        boolean requiresRotation = (degrees > 0);
        if (requiresRotation) {
            // Get the dimensions of the device in its native orientation
            mDisplayMatrix.reset();
            mDisplayMatrix.preRotate(-degrees);
            mDisplayMatrix.mapPoints(dims);

            dims[0] = Math.abs(dims[0]);
            dims[1] = Math.abs(dims[1]);
        }

        Bitmap mScreenBitmap = SystemProperties.screenshot((int) dims[0], (int) dims[1]);

        if (requiresRotation) {
            // Rotate the screenshot to the current orientation
            Bitmap ss = Bitmap.createBitmap(mDisplayMetrics.widthPixels,
                    mDisplayMetrics.heightPixels, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(ss);
            c.translate(ss.getWidth() / 2, ss.getHeight() / 2);
            c.rotate(degrees);
            c.translate(-dims[0] / 2, -dims[1] / 2);
            c.drawBitmap(mScreenBitmap, 0, 0, null);
            c.setBitmap(null);
            mScreenBitmap = ss;
        }
        if (mScreenBitmap == null) {
            return;
        }
        mScreenBitmap.setHasAlpha(false);
        mScreenBitmap.prepareToDraw();
        try {
            saveBitmap(mScreenBitmap);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void saveBitmap(Bitmap bitmap) throws IOException {
        String imageDate = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss")
                .format(new Date(System.currentTimeMillis()));
        File file = new File("/mnt/sdcard/Pictures/"+imageDate+".png");
        if(!file.exists()){
            file.createNewFile();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 70, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
