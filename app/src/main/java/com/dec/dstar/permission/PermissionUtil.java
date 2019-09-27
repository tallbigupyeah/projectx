package com.dec.dstar.permission;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

/**
 * util
 */
public class PermissionUtil {

    public static Context getContext(Object o) {
        if (o instanceof Activity) {
            return (Activity) o;
        } else if (o instanceof Fragment) {
            return ((Fragment) o).getActivity();
        }
        throw new IllegalArgumentException("The " + o.getClass().getName() + " is not support.");
    }

    public static boolean shouldShowRationalePermissions(Object o, String... permissions) {
        if (Build.VERSION.SDK_INT < 23) {
            return false;
        }
        boolean rationale = false;
        for (String permission : permissions) {
            if (o instanceof Activity) {
                rationale = ActivityCompat.shouldShowRequestPermissionRationale((Activity) o, permission);
            } else if (o instanceof Fragment) {
                rationale = ((Fragment) o).shouldShowRequestPermissionRationale(permission);
            }
            if (rationale) {
                return true;
            }
        }
        return false;
    }

}
