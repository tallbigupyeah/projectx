package com.dec.dstar.permission;

import java.util.List;

/**
 * Permission监听
 */
public interface PermissionListener {

    void onSucceed(int requestCode, List<String> grantPermissions);

    void onFailed(int requestCode, List<String> deniedPermissions);

}
