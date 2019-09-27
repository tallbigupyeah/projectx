package com.dec.dstar.widget.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/7 19:19
 * 文件描述:
 */
public final class PasswordDialog extends BaseDialog {

    public interface PasswordDialogListener{
        void confirm(String password);
    }

    @BindView(R.id.dialog_password_cancel)
    Button mIvClose;
    @BindView(R.id.dialog_password_et)
    EditText mEt;
    @BindView(R.id.dialog_password_confirm)
    Button mBtnNext;

    private PasswordDialogListener mPasswordDialogListener;

    public PasswordDialog(@NonNull Context context, PasswordDialogListener mPasswordDialogListener) {
        super(context);
        this.mPasswordDialogListener = mPasswordDialogListener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_password;
    }


    @OnClick({R.id.dialog_password_cancel, R.id.dialog_password_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialog_password_cancel:
                dismiss();
                break;
            case R.id.dialog_password_confirm:

                mPasswordDialogListener.confirm(mEt.getText().toString().trim());
                dismiss();
                break;
        }
    }


}
