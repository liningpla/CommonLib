package com.common.permission;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lining on 2016/10/8.
 * 适配6.0权限动态申请
 */
public class PermissionUtils {

    private static final String TAG = PermissionUtils.class.getSimpleName();
    public static final int CODE_MULTI_PERMISSION = 100;//多权限申请
    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_RECEIVE_SMS = Manifest.permission.RECEIVE_SMS;
    public static final String PERMISSION_SEND_SMS = Manifest.permission.SEND_SMS;


    public static final String[] requestPermissions = {
            PERMISSION_GET_ACCOUNTS,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE,
            PERMISSION_RECORD_AUDIO,
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_CAMERA,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_ACCESS_COARSE_LOCATION,
            PERMISSION_RECEIVE_SMS,
            PERMISSION_SEND_SMS
    };
    /**获取权限*/
    public static final String[] mainActivity2Permissions = {PERMISSION_READ_PHONE_STATE};
    /**申请权限结果返回，回调接口*/
   public interface PermissionGrant {
        void onPermissionGranted(int requestCode, int code);
    }
    /**
     * 申请单个权限
     * Requests permission.
     * @param activity
     * @param requestCode 权限申请操作返回码
     * @param permissionGrant 申请返回后续操作回调接口
     */
    public static void requestPermission(final Activity activity, final int requestCode, PermissionGrant permissionGrant) {
        //判断activity是否为空
        if (activity == null) {
            return;
        }
        //判断返回码是否有效
        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            Log.w(TAG, "requestPermission illegal requestCode:" + requestCode);
            return;
        }
        final String requestPermission = requestPermissions[requestCode];
        if (Build.VERSION.SDK_INT < 23) {
            return;
        }
        int checkSelfPermission;
        try {
            checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
        } catch (RuntimeException e) {
            Toast.makeText(activity, "please open this permission", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "RuntimeException:" + e.getMessage());
            return;
        }
        //检查权限是否开启
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                /*上次申请用户没有通过授权，再次提醒用户进行授权，如果有必要可以弹出对话框提醒用户，没有必要可以直接申请权限，这里采用直接申请权限*/
                ActivityCompat.requestPermissions(activity,new String[]{requestPermission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            }
            return;
        } else {
            permissionGrant.onPermissionGranted(requestCode, 0);
        }
    }

    /**处理多权限申请返回结果*/
    private static void requestMultiResult(Activity activity, int requestCode, String[] permissions, int[] grantResults, PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }
        Map<String, Integer> perms = new HashMap<>();
        ArrayList<String> notGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                notGranted.add(permissions[i]);
            }
        }
        if (notGranted.size() == 0) {
            permissionGrant.onPermissionGranted(requestCode, 0);
        } else {
            openSettingActivity(activity, "设置");
        }

    }
    /**
     * 一次申请多个权限
     */
    public static void requestMultiPermissions(final Activity activity, String[] requestPermissions, PermissionGrant grant, int requestCode) {
        if (requestPermissions.length > 0) {
            boolean isRequest = true;
            for(int i = 0; i < requestPermissions.length; i ++){
                int checkSelfPermission;
                try {
                    checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermissions[i]);
                } catch (RuntimeException e) {
                    Toast.makeText(activity, "please open this permission", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "RuntimeException:" + e.getMessage());
                    return;
                }
                //检查权限是否开启
                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                    isRequest = false;
                }
            }
            if(isRequest == false){
                ActivityCompat.requestPermissions(activity, requestPermissions, requestCode);
                return;
            }
            grant.onPermissionGranted(requestCode, 0);
        } else {
            grant.onPermissionGranted(requestCode, 0);
        }
    }

    public static boolean checkMultiPermissions(Activity activity, String[] requestPermissions){
        boolean isRequest = true;
        for(int i = 0; i < requestPermissions.length; i ++){
            int checkSelfPermission;
            try {
                checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermissions[i]);
            } catch (RuntimeException e) {
                Toast.makeText(activity, "please open this permission", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "RuntimeException:" + e.getMessage());
                return false;
            }
            //检查权限是否开启
            if (checkSelfPermission != PackageManager.PERMISSION_DENIED) {
                isRequest = false;
            }
        }
        return isRequest;
    }

    /**
     * 一次申请多个权限
     */
    private static void requestMultiPermissions(final Activity activity, PermissionGrant grant) {

        /*获取没有授权的权限且没有申请过的权限*/
        final List<String> permissionsList = getNoGrantedPermission(activity, false);
        /*获取没有授权的权限且申请过的权限*/
        final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, true);
        if (permissionsList == null || shouldRationalePermissionsList == null) {
            return;
        }
        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
                    CODE_MULTI_PERMISSION);
        } else if (shouldRationalePermissionsList.size() > 0) {
            /*上次申请用户没有通过授权，再次提醒用户进行授权，如果有必要可以弹出对话框提醒用户，没有必要可以直接申请权限，这里采用直接申请权限*/
            ActivityCompat.requestPermissions(activity, shouldRationalePermissionsList.toArray(new String[shouldRationalePermissionsList.size()]),
                    CODE_MULTI_PERMISSION);
        } else {
            grant.onPermissionGranted(CODE_MULTI_PERMISSION, 0);
        }
    }
    /**显示再次申请提现对话框*/
    private static void showMessageOKCancel(final Activity activity, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("ok", okListener)
                .setNegativeButton( "cancle", null)
                .create()
                .show();

    }
    /**
     * 处理权限申请返回结果，重写AppCompatActivity里的onRequestPermissionsResult()调用
     * @param activity
     * @param requestCode  Need consistent with requestPermission
     * @param permissions
     * @param grantResults
     */
    public static void requestPermissionsResult(final Activity activity, final int requestCode, @NonNull String[] permissions,
                                                @NonNull int[] grantResults, PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }
        //处理多权限申请返回结果
        if (permissions != null && permissions.length > 1) {
            requestMultiResult(activity, requestCode, permissions, grantResults, permissionGrant);
        }
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGrant.onPermissionGranted(requestCode, 0);
        }else{
            permissionGrant.onPermissionGranted(requestCode, 1);
        }
    }

    /**打开当前应用程序详解信息界面*/
    private static void openSettingActivity(final Activity activity, String message) {
        showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Log.d(TAG, "getPackageName(): " + activity.getPackageName());
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
    }


    /**
     * 获取当前没有授权的权限
     * @param activity
     * @param isShouldRationale true: 返回没有授予且需要再次申请的权限, false:返回没有授予且没有申请授权过的权限
     * @return
     */
    public static ArrayList<String> getNoGrantedPermission(Activity activity, boolean isShouldRationale) {

        ArrayList<String> permissions = new ArrayList<>();

        for (int i = 0; i < requestPermissions.length; i++) {
            String requestPermission = requestPermissions[i];
            int checkSelfPermission = -1;
            try {
                checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
            } catch (RuntimeException e) {
                Toast.makeText(activity, "please open those permission", Toast.LENGTH_SHORT).show();
                return null;
            }
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                    if (isShouldRationale) {
                        permissions.add(requestPermission);
                    }

                } else {
                    if (!isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                }
            }
        }
        return permissions;
    }
}
