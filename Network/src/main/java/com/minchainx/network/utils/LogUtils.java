package com.minchainx.network.utils;

import android.util.Log;

import com.minchainx.networklite.NetworkLiteHelper;

public class LogUtils {

    private static final String TAG = "DstarNetwork";
    private static boolean DEBUG = true;

    public static boolean isDebug() {
        return DEBUG;
    }

    public static void setDebug(boolean enable) {
        LogUtils.DEBUG = enable;
        NetworkLiteHelper.getNetworkLite().setLogOutput(enable);
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void v(String msg, Throwable t) {
        v(msg + ": " + t.fillInStackTrace().toString());
    }

    public static void v(String tag, String msg) {
        if (DEBUG) {
            Log.v(tag, msg);
        }
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String msg, Throwable t) {
        d(msg + ": " + t.fillInStackTrace().toString());
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String msg, Throwable t) {
        i(msg + ": " + t.fillInStackTrace().toString());
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void w(String msg, Throwable t) {
        w(msg + ": " + t.fillInStackTrace().toString());
    }

    public static void w(String tag, String msg) {
        if (DEBUG) {
            Log.w(tag, msg);
        }
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String msg, Throwable t) {
        e(msg + ": " + t.fillInStackTrace().toString());
    }

    public static void e(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }
}
