package com.dec.dstar.base;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.dec.dstar.R;

import butterknife.ButterKnife;

/**
 * Created by cleverdou on 17/7/4.
 */

public abstract class BaseDialog extends Dialog {

    protected Context mContext;

    public BaseDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        final Window window = getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.y = 20;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        ButterKnife.bind(this);
        initView();
        loadData();
    }

    protected void initView() {

    }

    protected void loadData() {

    }

    protected abstract int getLayoutId();


}
