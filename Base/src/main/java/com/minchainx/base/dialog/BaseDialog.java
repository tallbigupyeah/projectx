package com.minchainx.base.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

import com.minchainx.base.R;

public abstract class BaseDialog extends Dialog {

    protected Context mContext;

    public BaseDialog(Context context) {
        super(context, R.style.dialog_default_style);
        mContext = context;
        if (context instanceof Activity) {
            setOwnerActivity((Activity) context);
        }
    }

//    public BaseDialog(Activity activity) {
//        this((Context) activity);
//    }

    public interface OnClickListener {
        void onClick(BaseDialog dialog, View view);
    }

    @Override
    public void show() {
        Activity ownerActivity = getOwnerActivity();
        if (ownerActivity != null && !ownerActivity.isFinishing()) {
            super.show();
        }
    }

    @Override
    public void hide() {
        if (isShowing()) {
            Activity ownerActivity = getOwnerActivity();
            if (ownerActivity != null && !ownerActivity.isFinishing()) {
                super.hide();
            }
        }
    }

    @Override
    public void dismiss() {
        if (isShowing()) {
            Activity ownerActivity = getOwnerActivity();
            if (ownerActivity != null && !ownerActivity.isFinishing()) {
                super.dismiss();
            }
        }
    }

    public void showProgressBar() {
    }

    public void hideProgressBar() {
    }
}
