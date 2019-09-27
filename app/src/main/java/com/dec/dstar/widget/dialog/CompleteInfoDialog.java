package com.dec.dstar.widget.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/25 19:09
 * 文件描述: 有左右按钮的对话框，中间显示文字的那种
 */
public class CompleteInfoDialog extends BaseDialog {

    CompleteInfoDialogListener mListener;

    @BindView(R.id.dialog_iv_close)
    ImageView mIvClose;
    @BindView(R.id.wechatpublic_btn_cancel)
    TextView mBtnCancel;
    @BindView(R.id.wechatpublic_btn_confirm)
    TextView mBtnConfirm;

    public CompleteInfoDialog(@NonNull Context context, CompleteInfoDialogListener completeInfoDialogListener) {
        super(context);
        mListener = completeInfoDialogListener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_info_complete;
    }


    @OnClick({R.id.dialog_iv_close, R.id.wechatpublic_btn_cancel, R.id.wechatpublic_btn_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialog_iv_close:
                dismiss();
                break;
            case R.id.wechatpublic_btn_cancel:
                dismiss();
                mListener.Cancel();
                break;
            case R.id.wechatpublic_btn_confirm:
                dismiss();
                mListener.confirm();
                break;
        }
    }

    public interface CompleteInfoDialogListener{
        void Cancel();
        void confirm();
    }
}
