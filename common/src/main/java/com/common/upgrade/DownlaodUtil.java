package com.common.upgrade;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 */

public class DownlaodUtil {
    private static final String TAG = Downer.TAG;

    /**
     * 外部存储卡权限
     */
    public static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 0x1024;

    /**
     * 判断申请外部存储所需权限
     *
     * @param context
     * @param isActivate
     * @return
     */
    public static boolean mayRequestExternalStorage(Context context, boolean isActivate) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (isActivate) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
        return false;
    }

    /**
     * 获取应用程序图标
     *
     * @param context
     * @return
     */
    public static Bitmap getAppIcon(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            Drawable drawable = applicationInfo.loadIcon(packageManager);
            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
            Bitmap bitmap = Bitmap.createBitmap(width, height, config);
            bitmap.setHasAlpha(true);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            drawable.setBounds(0, 0, width, height);
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用程序名称
     *
     * @param context
     * @return
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return context.getResources().getString(packageInfo.applicationInfo.labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取序列号
     *
     * @return
     */
    public static String getSerial() {
        try {
            Field field = Build.class.getField("SERIAL");
            return (String) field.get(null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 格式化字节单位
     *
     * @param size
     * @return
     */
    public static String formatByte(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "Byte";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB";
    }

    /**
     * 服务是否运行
     *
     * @param context
     * @param serviceName
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : list) {
            if (info.service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断当前活动是否为栈顶
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isActivityTop(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            if (cn != null && cn.getPackageName() != null) {
                return packageName.equals(cn.getPackageName());
            }
        }
        return false;
    }

    /**
     * 安装程序
     *
     * @param context
     * @param path
     */
    public static void installApk(Context context, String path) {
        Log.i(Downer.TAG, "DownlaodUtil:installApk:path："+path);
        File file = new File(path);
        if (!file.exists()) {
            Log.i(Downer.TAG, "DownlaodUtil:installApk：file is not exists");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.i(Downer.TAG, "DownlaodUtil:installApk：context:"+context+" srt:"+String.format(DownlaodFileProvider.AUTHORITY, context.getPackageName()));
            Uri uri = DownlaodFileProvider.getUriForFile(context,
                    String.format(DownlaodFileProvider.AUTHORITY, context.getPackageName()), file);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
            context.startActivity(intent);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + file.toString()),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 安装程序
     *
     * @param path
     */
    public static boolean installApk(String path) {
        if (path == null || !path.endsWith(".apk")) {
            return false;
        }

        File apk = new File(path);
        if (!apk.exists()) {
            return false;
        }
        int result = cmd("chmod 777 " + apk.getPath() + " \n" +
                "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r " + apk.getPath() + " \n");
        if (result == 0) {
            Log.d(TAG, "Install apk：Install successfully");
            return true;
        } else if (result == 1) {
            Log.d(TAG, "Install apk：Installation failed");
            return false;
        } else {
            Log.d(TAG, "Install apk：Unknown");
            return false;
        }
    }

    /**
     * 启动程序
     */
    public static void launch(Context context, String className) {
        cmd("am start -S  " + context.getPackageName() + "/" + className + " \n");
    }

    /**
     * 是否有Root权限
     *
     * @return
     */
    public static boolean isRooted() {
        boolean result = false;
        try {
            result = new File("/system/bin/su").exists()
                    || new File("/system/xbin/su").exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 执行命令
     */
    public static int cmd(String cmd) {
        if (cmd == null || cmd.length() == 0) {
            return -1;
        }
        Process process = null;
        DataOutputStream dos = null;
        try {
            Runtime runtime = Runtime.getRuntime();
            process = runtime.exec("su");
            OutputStream os = process.getOutputStream();
            dos = new DataOutputStream(os);
            dos.writeBytes(cmd);
            dos.writeBytes("exit \n");
            dos.flush();
            return process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (process != null) {
                try {
                    process.destroy();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        return -1;
    }

    /**解析apk文件，获取相关信息*/
    public static HashMap<String, Object> getApkInfo(Context context, String apkPath) {
        HashMap<String, Object> hashMap = new HashMap<>();
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            String packageName = appInfo.packageName;  //得到安装包名称
            String version = info.versionName;//获取安装包的版本号
            hashMap.put("packageName", packageName);
            hashMap.put("version", version);
            try {
                hashMap.put("icon", appInfo.loadIcon(pm));
            } catch (OutOfMemoryError e) {
                Log.i(TAG, "GetApkInfo: " + e);
            }
        }
        return hashMap;
    }
}
