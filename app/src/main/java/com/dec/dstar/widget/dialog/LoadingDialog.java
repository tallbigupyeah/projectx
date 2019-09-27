package com.dec.dstar.widget.dialog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseDialog;

import butterknife.BindView;

public class LoadingDialog extends BaseDialog {


    @BindView(R.id.loading_img)
    ImageView ivLoading;
    private RotateAnimation mRotateAnimation;


    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_loading;
    }

    @Override
    protected void initView() {
        super.initView();
        initAnimation();
        final Window window = getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.CENTER;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        ivLoading.startAnimation(mRotateAnimation);
    }

    private void initAnimation() {
        if (null == mRotateAnimation) {
            mRotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            mRotateAnimation.setDuration(1000);
            mRotateAnimation.setRepeatCount(-1);
            mRotateAnimation.setFillAfter(false);
            mRotateAnimation.setInterpolator(new LinearInterpolator());
        }
    }

    @Override
    public void show() {
        super.show();
        mRotateAnimation.cancel();
        ivLoading.startAnimation(mRotateAnimation);
    }
}
