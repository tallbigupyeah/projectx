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
 */
public class RationaleDialog {

    private MaterialDialog.Builder mBuilder;
    private MaterialDialog mDialog;
    private Rationale mRationale;

    private RationaleDialog() {
    }

    public RationaleDialog(@NonNull Context context, @NonNull Rationale rationale) {
        mBuilder = new MaterialDialog.Builder(context)
                .theme(Theme.LIGHT)
                .title(R.string.permission_title_permission_failed)
                .content(R.string.permission_message_permission_failed)
                .positiveText(R.string.permission_resume)
                .negativeText(R.string.permission_cancel)
                .onPositive(mClickListener)
                .onNegative(mClickListener)
                .autoDismiss(false)
                .cancelable(false);
        this.mRationale = rationale;
    }

    @NonNull
    public RationaleDialog setTitle(@NonNull String title) {
        mBuilder.title(title);
        return this;
    }

    @NonNull
    public RationaleDialog setTitle(@StringRes int title) {
        mBuilder.title(title);
        return this;
    }

    @NonNull
    public RationaleDialog setMessage(@NonNull String message) {
        mBuilder.content(message);
        return this;
    }

    @NonNull
    public RationaleDialog setMessage(@StringRes int message) {
        mBuilder.content(message);
        return this;
    }

    @NonNull
    public RationaleDialog setNegativeText(@NonNull String message) {
        mBuilder.negativeText(message);
        return this;
    }

    @NonNull
    public RationaleDialog setNegativeText(@StringRes int message) {
        mBuilder.negativeText(message);
        return this;
    }

    @NonNull
    public RationaleDialog setPositiveText(@NonNull String message) {
        mBuilder.positiveText(message);
        return this;
    }

    @NonNull
    public RationaleDialog setPositiveText(@StringRes int message) {
        mBuilder.positiveText(message);
        return this;
    }

    @NonNull
    public RationaleDialog setNegativeButton(@Nullable MaterialDialog.SingleButtonCallback listener) {
        mBuilder.onNegative(listener);
        return this;
    }

    @NonNull
    public RationaleDialog setPositiveButton() {
        mBuilder.onPositive(mClickListener);
        return this;
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
                    mRationale.cancel();
                    break;
                case POSITIVE:
                    mRationale.resume();
                    break;
            }
        }
    };
}
