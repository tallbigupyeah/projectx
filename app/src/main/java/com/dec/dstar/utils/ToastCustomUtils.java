package com.dec.dstar.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/15 20:08
 * 文件描述:
 */
public class ToastCustomUtils {

    private static ToastCustom sToast;
    private static int gravity = 51;
    private static int xOffset = 0;
    private static int yOffset = 0;
    private static float horizontalMargin = 0.0F;
    private static float verticalMargin = 0.0F;
    private static int animation = -1;
    @SuppressLint({"StaticFieldLeak"})
    private static View customView;
    private static Handler sHandler = new Handler(Looper.getMainLooper());

    private ToastCustomUtils() {
        throw new UnsupportedOperationException("u can\'t instantiate me...");
    }

    public static void setGravity(int gravity, int xOffset, int yOffset) {
        ToastCustomUtils.gravity = gravity;
        ToastCustomUtils.xOffset = xOffset;
        ToastCustomUtils.yOffset = yOffset;
    }

    public static void setMargin(float horizontalMargin, float verticalMargin) {
        ToastCustomUtils.horizontalMargin = horizontalMargin;
        ToastCustomUtils.verticalMargin = verticalMargin;
    }

    public static void setAnimation(int animations) {
        animation = animations;
    }

    public static void setView(@LayoutRes int layoutId) {
        LayoutInflater inflate = (LayoutInflater) Utils.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        customView = inflate.inflate(layoutId, null);
    }

    public static void setView(View view) {
        customView = view;
    }

    public static View getView() {
        return customView != null ? customView : (sToast != null ? sToast.getView() : null);
    }

    public static void showShortSafe(final CharSequence text) {
        sHandler.post(new Runnable() {
            public void run() {
                ToastCustomUtils.show(text, ToastCustom.LENGTH_SHORT);
            }
        });
    }

    public static void showShortSafe(@StringRes final int resId) {
        sHandler.post(new Runnable() {
            public void run() {
                ToastCustomUtils.show(resId, ToastCustom.LENGTH_SHORT);
            }
        });
    }

    public static void showShortSafe(@StringRes final int resId, final Object... args) {
        sHandler.post(new Runnable() {
            public void run() {
                ToastCustomUtils.show(resId, ToastCustom.LENGTH_SHORT, args);
            }
        });
    }

    public static void showShortSafe(final String format, final Object... args) {
        sHandler.post(new Runnable() {
            public void run() {
                ToastCustomUtils.show(format, ToastCustom.LENGTH_SHORT, args);
            }
        });
    }

    public static void showLongSafe(final CharSequence text) {
        sHandler.post(new Runnable() {
            public void run() {
                ToastCustomUtils.show(text, ToastCustom.LENGTH_LONG);
            }
        });
    }

    public static void showLongSafe(@StringRes final int resId) {
        sHandler.post(new Runnable() {
            public void run() {
                ToastCustomUtils.show(resId, ToastCustom.LENGTH_LONG);
            }
        });
    }

    public static void showLongSafe(@StringRes final int resId, final Object... args) {
        sHandler.post(new Runnable() {
            public void run() {
                ToastCustomUtils.show(resId, ToastCustom.LENGTH_LONG, args);
            }
        });
    }

    public static void showLongSafe(final String format, final Object... args) {
        sHandler.post(new Runnable() {
            public void run() {
                ToastCustomUtils.show(format, ToastCustom.LENGTH_LONG, args);
            }
        });
    }

    public static void showShort(CharSequence text) {
        show(text, ToastCustom.LENGTH_SHORT);
    }

    public static void showShort(@StringRes int resId) {
        show(resId, ToastCustom.LENGTH_SHORT);
    }

    public static void showShort(@StringRes int resId, Object... args) {
        show(resId, ToastCustom.LENGTH_SHORT, args);
    }

    public static void showShort(String format, Object... args) {
        show(format, ToastCustom.LENGTH_SHORT, args);
    }

    public static void showLong(CharSequence text) {
        show(text, ToastCustom.LENGTH_LONG);
    }

    public static void showLong(@StringRes int resId) {
        show(resId, ToastCustom.LENGTH_LONG);
    }

    public static void showLong(@StringRes int resId, Object... args) {
        show(resId, ToastCustom.LENGTH_LONG, args);
    }

    public static void showLong(String format, Object... args) {
        show(format, ToastCustom.LENGTH_LONG, args);
    }

    private static void show(@StringRes int resId, int duration) {
        show(Utils.getContext().getResources().getText(resId).toString(), duration);
    }

    private static void show(@StringRes int resId, int duration, Object... args) {
        show(String.format(Utils.getContext().getResources().getString(resId), args), duration);
    }

    private static void show(String format, int duration, Object... args) {
        show(String.format(format, args), duration);
    }

    private static void show(CharSequence text, int duration) {
        cancel();
        if (customView != null) {
            sToast = new ToastCustom(Utils.getContext());
            sToast.setView(customView);
            sToast.setDuration(duration);
        } else {
            sToast = ToastCustom.makeText(Utils.getContext(), text, duration);
        }

//        sToast.setGravity(gravity, xOffset, yOffset);
//        sToast.setMargin(horizontalMargin, verticalMargin);
        sToast.setAnimations(animation);
        sToast.show();
    }

    public static void cancel() {
        if (sToast != null) {
            sToast.hide();
            sToast = null;
        }

    }
}