package com.dec.dstar.base;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dec.dstar.R;
import com.dec.dstar.utils.ActivityCollector;
import com.dec.dstar.utils.BarUtils;
import com.dec.dstar.utils.CommonToast;
import com.dec.dstar.utils.LogUtils;
import com.dec.dstar.widget.dialog.LoadingDialog;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 作者：luoxiaohui
 * 日期:2018/7/15 19:31
 * 文件描述: BaseActivity
 */
public abstract class BaseActivity<P extends BasePresenter> extends com.minchainx.base.clazz.activity.BaseActivity<P> implements IBaseView {

    protected boolean isFirstIn;
    protected int activityOpenEnterAnimation;
    protected int activityOpenExitAnimation;
    protected int activityCloseEnterAnimation;
    protected int activityCloseExitAnimation;

    protected RelativeLayout mBackItem;
    protected FrameLayout mMenuItem;
    protected RelativeLayout mTitleBar;
    protected TextView mTitle;
    protected View mTitleUnderLine;
    protected ImageView mBack;
    protected ImageView mMenuIcon;
    protected TextView mMenuText;
    private Unbinder unbinder;
    private AlertDialog alertDialog;

    private boolean isProgress;
    private LoadingDialog loadingDialog;


    @Override
    protected void initializeTitleBar() {
        TypedArray activityStyle = getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowAnimationStyle});
        int windowAnimationStyleResId = activityStyle.getResourceId(0, 0);
        activityStyle.recycle();
        activityStyle = getTheme().obtainStyledAttributes(windowAnimationStyleResId,
                new int[]{
                        android.R.attr.activityOpenEnterAnimation,
                        android.R.attr.activityOpenExitAnimation,
                        android.R.attr.activityCloseEnterAnimation,
                        android.R.attr.activityCloseExitAnimation
                });
        activityOpenEnterAnimation = activityStyle.getResourceId(0, 0);
        activityOpenExitAnimation = activityStyle.getResourceId(1, 0);
        activityCloseEnterAnimation = activityStyle.getResourceId(2, 0);
        activityCloseExitAnimation = activityStyle.getResourceId(3, 0);
        activityStyle.recycle();
        overridePendingTransition(activityOpenEnterAnimation, activityOpenExitAnimation);

        mBackItem = ButterKnife.findById(this, R.id.rl_back);
        mMenuItem = ButterKnife.findById(this, R.id.fl_menu);
        mTitleBar = ButterKnife.findById(this, R.id.rl_titlebar);
        mTitle = ButterKnife.findById(this, R.id.title);
        mTitleUnderLine = ButterKnife.findById(this, R.id.title_underline);
        mBack = ButterKnife.findById(this, R.id.iv_back);
        mMenuIcon = ButterKnife.findById(this, R.id.iv_menu);
        mMenuText = ButterKnife.findById(this, R.id.tv_menu);

        if (mBackItem != null) {
            mBackItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseActivity.this.finish();
                }
            });
        }
        setStatusBar();
    }


    @Override
    protected void initializeView() {
        isFirstIn = true;
        unbinder = ButterKnife.bind(this);
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onCreateFirstLogic() {
        ActivityCollector.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroyFirstLogic() {
        ActivityCollector.getInstance().removeActivity(this);
        hideProgressBar();
        unbinder.unbind();
    }

    @Override
    public void i(String s) {

//        LogUtils.i(s + "::" + this.getClass().getSimpleName());
    }

    @Override
    public void e(String s) {
        LogUtils.e(s + "::" + this.getClass().getSimpleName());
    }

    public void d(String s) {
        LogUtils.d(s + "::" + this.getClass().getSimpleName());
    }

    @Override
    public void showProgressBar() {
//        if(this.isDestroyed()) return ;
//        if (loadingDialog == null) {
//            loadingDialog = new LoadingDialog(this);
//            loadingDialog.setOwnerActivity(this);
//        }
//        try {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    loadingDialog.show();
//                }
//            });
//        } catch (Exception e) {
//            e(e.getMessage());
//        }
    }

    @Override
    public void hideProgressBar() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (loadingDialog != null) {
                        Activity ownerActivity = loadingDialog.getOwnerActivity();
                        if (ownerActivity != null && !ownerActivity.isFinishing()) {
                            loadingDialog.dismiss();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e(e.getMessage());
        }
    }

    @Override
    public void showToast(String s) {
        CommonToast.showMessage(s);
    }


    public void showDialogMsg(String msg) {
        if (this.isFinishing()) {
            return;
        }
        if (alertDialog == null) {
            alertDialog = new AlertDialog.Builder(this).setTitle(msg).create();
        }
        alertDialog.show();
    }

    protected void setStatusBar() {
        BarUtils.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        BarUtils.setLightMode(this);
    }


    public void showErrorToast(String msg) {
        CommonToast.showErrorMessage(msg);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (isProgress) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void isProgress(boolean isProgress) {
        this.isProgress = isProgress;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(activityCloseEnterAnimation, activityCloseExitAnimation);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFirstIn = false;
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
