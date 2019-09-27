package com.dec.dstar.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/15 20:08
 * 文件描述:
 */
public class ToastCustom {
    private static final String TAG = "ToastCustom";
    public static final int LENGTH_ALWAYS = 0;
    public static final int LENGTH_SHORT = 2;
    public static final int LENGTH_LONG = 5;
    private Toast toast;
    private Context mContext;
    private int mDuration = 2;
    private int animations = -1;
    private boolean isShow = false;
    private Object mTN;
    private Method show;
    private Method hide;
    private Handler handler = new Handler();
    private Runnable hideRunnable = new Runnable() {
        public void run() {
            ToastCustom.this.hide();
        }
    };

    public ToastCustom(Context context) {
        this.mContext = context;
        if (this.toast == null) {
            this.toast = Toast.makeText(this.mContext, "", Toast.LENGTH_SHORT);
        }

    }

    public void show() {
        if (!this.isShow) {
            try {
                this.initTN();
                this.show.invoke(this.mTN, new Object[0]);
                this.isShow = true;
                if (this.mDuration > 0) {
                    this.handler.postDelayed(this.hideRunnable, (long) (this.mDuration * 1000));
                }
            } catch (Exception e) {
                toast.show();
            }
        }
    }

    public void hide() {
        if (this.isShow) {
            try {
                this.hide.invoke(this.mTN, new Object[0]);
                this.isShow = false;
            } catch (Exception e) {

            }
        }
    }

    public void setView(View view) {
        this.toast.setView(view);
    }

    public View getView() {
        return this.toast.getView();
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }

    public int getDuration() {
        return this.mDuration;
    }

    public void setMargin(float horizontalMargin, float verticalMargin) {
        this.toast.setMargin(horizontalMargin, verticalMargin);
    }

    public float getHorizontalMargin() {
        return this.toast.getHorizontalMargin();
    }

    public float getVerticalMargin() {
        return this.toast.getVerticalMargin();
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        this.toast.setGravity(gravity, xOffset, yOffset);
    }

    public int getGravity() {
        return this.toast.getGravity();
    }

    public int getXOffset() {
        return this.toast.getXOffset();
    }

    public int getYOffset() {
        return this.toast.getYOffset();
    }

    public static ToastCustom makeText(Context context, CharSequence text, int duration) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        ToastCustom cusToast = new ToastCustom(context);
        cusToast.toast = toast;
        cusToast.mDuration = duration;
        return cusToast;
    }

    public static ToastCustom makeText(Context context, int resId, int duration) throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    public void setText(int resId) {
        this.setText(this.mContext.getText(resId));
    }

    public void setText(CharSequence s) {
        this.toast.setText(s);
    }

    public int getAnimations() {
        return this.animations;
    }

    public void setAnimations(int animations) {
        this.animations = animations;
    }

    private void initTN() throws Exception {
        Field e = this.toast.getClass().getDeclaredField("mTN");
        e.setAccessible(true);
        this.mTN = e.get(this.toast);
        this.show = this.mTN.getClass().getMethod("show", new Class[0]);
        this.hide = this.mTN.getClass().getMethod("hide", new Class[0]);
        Field tnNextViewField;
        if (this.animations != -1) {
            tnNextViewField = this.mTN.getClass().getDeclaredField("mParams");
            tnNextViewField.setAccessible(true);
            WindowManager.LayoutParams params = (WindowManager.LayoutParams) tnNextViewField.get(this.mTN);
            params.windowAnimations = this.animations;
        }

        tnNextViewField = this.mTN.getClass().getDeclaredField("mNextView");
        tnNextViewField.setAccessible(true);
        tnNextViewField.set(this.mTN, this.toast.getView());
    }
}
