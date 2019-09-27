package com.dec.dstar.permission;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.dec.dstar.R;

/**
 * 设置权限对话框
 */
public class SettingDialog {

    private MaterialDialog.Builder mBuilder;
    private MaterialDialog mDialog;
    private SettingService mSettingService;

    private SettingDialog() {
    }

    public SettingDialog(@NonNull Context context, @NonNull SettingService settingService) {
        mBuilder = new MaterialDialog.Builder(context)
                .theme(Theme.LIGHT)
                .title(R.string.permission_title_permission_failed)
                .content(R.string.permission_message_permission_failed)
                .positiveText(R.string.permission_setting)
                .onPositive(mClickListener)
                .autoDismiss(false)
                .cancelable(false);
        this.mSettingService = settingService;
    }

    @NonNull
    public SettingDialog setTitle(@NonNull String title) {
        mBuilder.title(title);
        return this;
    }

    @NonNull
    public SettingDialog setTitle(@StringRes int title) {
        mBuilder.title(title);
        return this;
    }

    @NonNull
    public SettingDialog setMessage(@NonNull String message) {
        mBuilder.content(message);
        return this;
    }

    @NonNull
    public SettingDialog setMessage(@StringRes int message) {
        mBuilder.content(message);
        return this;
    }

    @NonNull
    public SettingDialog setNegativeText(@NonNull String message) {
        mBuilder.negativeText(message);
        return this;
    }

    @NonNull
    public SettingDialog setNegativeText(@StringRes int message) {
        mBuilder.negativeText(message);
        return this;
    }

    @NonNull
    public SettingDialog setPositiveText(@NonNull String message) {
        mBuilder.positiveText(message);
        return this;
    }

    @NonNull
    public SettingDialog setPositiveText(@StringRes int message) {
        mBuilder.positiveText(message);
        return this;
    }

    @NonNull
    public SettingDialog setNegativeButton(@Nullable MaterialDialog.SingleButtonCallback listener) {
        mBuilder.onNegative(listener);
        return this;
    }

    @NonNull
    public SettingDialog setPositiveButton() {
        mBuilder.onPositive(mClickListener);
        return this;
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public void show() {
        mDialog = mBuilder.show();
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.cancel();
        }
    }

    /**
     * 点击监听处理
     */
    private MaterialDialog.SingleButtonCallback mClickListener = new MaterialDialog.SingleButtonCallback() {
        @Override
        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            switch (which) {
                case NEGATIVE:
                    mSettingService.cancel();
                    break;
                case POSITIVE:
                    mSettingService.execute();
                    break;
            }
        }
    };
}
