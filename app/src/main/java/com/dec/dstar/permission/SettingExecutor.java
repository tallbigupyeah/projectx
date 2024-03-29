package com.dec.dstar.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 */
public class SettingExecutor implements SettingService {

    private Object object;
    private int requestCode;

    public SettingExecutor(@NonNull Object mObject, int mRequestCode) {
        object = mObject;
        requestCode = mRequestCode;
    }

    @Override
    public void execute() {
        Context context = PermissionUtil.getContext(object);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        startForResult(object, intent, requestCode);
    }

    @Override
    public void cancel() {
    }

    private static void startForResult(Object object, Intent intent, int requestCode) {
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).startActivityForResult(intent, requestCode);
        }
    }
}