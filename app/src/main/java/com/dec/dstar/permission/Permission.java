package com.dec.dstar.permission;

import android.support.annotation.NonNull;

public interface Permission {

    // 打电话requestCode
    int REQUEST_CODE_CALL_PHONE = 1001;
    // 电话读取requestCode
    int REQUEST_CODE_PHONE_STATE = 1002;
    // 存储requestCode
    int REQUEST_CODE_WRITE_STORAGE = 2001;
    // 读取联系人requestCode
    int REQUEST_CODE_READ_CONTACTS = 3001;
    // 拍照requestCode
    int REQUEST_CODE_CAMERA = 4001;

    /**
     * 请求权限名称
     */
    @NonNull
    Permission permission(String... permissions);

    /**
     * 请求权限requestCode
     */
    @NonNull
    Permission requestCode(int requestCode);

    /**
     * 请求权限永远禁止处理
     */
    @NonNull
    Permission rationale(RationaleListener listener);

    /**
     * 请求权限
     */
    void send();

}
