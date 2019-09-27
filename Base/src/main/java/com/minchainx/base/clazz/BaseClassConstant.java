package com.minchainx.base.clazz;

public interface BaseClassConstant {

    /**
     * 强制退出
     */
    int STATUS_FORCE_KILLED = -1;
    /**
     * 未登录
     */
    int STATUS_LOGGED_OUT = 0;
    /**
     * 已登录
     */
    int STATUS_LOGGED_IN = 1;
    /**
     * 账号下线
     */
    int STATUS_OFFLINE = 2;
}
