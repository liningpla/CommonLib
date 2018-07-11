package com.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.common.permission.PermissionUtils;


public class BaseAcivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /**
     * 获取权限
     */
    public void requestPermission(String[] requestPermissions) {
        PermissionUtils.requestMultiPermissions(this, requestPermissions, mPermissionGrant,
                PermissionUtils.CODE_MULTI_PERMISSION);
    }
    /**
     * 获取权限后执行操作
     */
    public void doPermission(int requestCode, int code) {
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }
    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode, int code) {
            switch (requestCode) {
                case PermissionUtils.CODE_MULTI_PERMISSION:
                    doPermission(requestCode, code);
                    break;
                default:
                    break;
            }
        }
    };
}
