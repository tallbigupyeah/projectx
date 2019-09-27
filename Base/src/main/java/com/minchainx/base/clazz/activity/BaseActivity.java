package com.minchainx.base.clazz.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.minchainx.base.R;
import com.minchainx.base.clazz.BaseClassConstant;
import com.minchainx.base.clazz.iview.IBaseView;
import com.minchainx.base.clazz.presenter.BasePresenter;

import rx.subscriptions.CompositeSubscription;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity implements IBaseView {

    protected FrameLayout mBaseContentContainer;
    private View mContentView;

    protected BaseActivity mContext = this;
    protected P mPresenter;
    private boolean mIsForeground;
    private CompositeSubscription mCompositeSubscription;

    /**
     * onCreate中在super.onCreate之后，正式加载布局和请求数据之前调用
     */
    protected abstract void onCreateFirstLogic();

    /**
     * onDestroy中在super.onDetroy之后，其他回收逻辑之前调用
     */
    protected abstract void onDestroyFirstLogic();

    /**
     * 获取布局文件资源id
     */
    protected abstract int getLayoutResId();

    /**
     * 初始化标题栏
     */
    protected abstract void initializeTitleBar();

    /**
     * 初始化控件
     */
    protected abstract void initializeView();

    /**
     * 加载数据并更新UI
     */
    protected abstract void loadData();

    /**
     * 日志输出：info
     */
    public abstract void i(String text);

    /**
     * 日志输出：error
     */
    public abstract void e(String text);

    /**
     *
     */
    protected void protectApp() {
    }

    /**
     * 获取APP状态 强制退出/未登录/已登录/账号下线
     */
    protected int getAppStatus() {
        return BaseClassConstant.STATUS_LOGGED_OUT;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i("------------ onCreate ------------savedInstanceState=" + savedInstanceState);
        onCreateFirstLogic();
        switch (getAppStatus()) {
            case BaseClassConstant.STATUS_FORCE_KILLED:
                protectApp();
                break;
            case BaseClassConstant.STATUS_LOGGED_OUT:
            case BaseClassConstant.STATUS_LOGGED_IN:
            case BaseClassConstant.STATUS_OFFLINE:
                setContentView(getLayoutResId());
                initializeData(savedInstanceState);
                initializeView();
                initializeTitleBar();
                loadData();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        i("------------ onStart ------------");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        i("------------ onRestoreInstanceState ------------savedInstanceState=" + savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        i("------------ onResume ------------");
        mIsForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        i("------------ onPause ------------");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        i("------------ onSaveInstanceState ------------outState=" + outState);
        if (getPresenter() != null) {
            getPresenter().onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        i("------------ onStop ------------");
        mIsForeground = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        i("------------ onDestroy ------------");
        onDestroyFirstLogic();
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
        if (getPresenter() != null) {
            getPresenter().onDestroy();
            mPresenter = null;
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        if (layoutResID > 0) {
            try {
                super.setContentView(R.layout.base_content_container);
                mBaseContentContainer = (FrameLayout) findViewById(R.id.base_content_container);
                mBaseContentContainer.removeAllViews();
                mContentView = getLayoutInflater().inflate(layoutResID, mBaseContentContainer, false);
                mBaseContentContainer.addView(mContentView);
            } catch (OutOfMemoryError e) {
                e("setContentView-->" + e.getMessage());
            }
        }
    }

    public FrameLayout getBaseContentContainer() {
        return mBaseContentContainer;
    }

    public View getContentView() {
        return mContentView;
    }

    /**
     * 创建Presenter
     */
    protected P createPresenter() {
        return null;
    }

    /**
     * 获取Presenter
     *
     * @return
     */
    protected P getPresenter() {
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        return mPresenter;
    }

    /**
     * 初始化数据
     *
     * @param savedInstanceState
     */
    protected void initializeData(Bundle savedInstanceState) {
        if (getPresenter() != null) {
            getPresenter().initializeData(this);
            getPresenter().onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * 当前Activity是否在前台
     */
    public boolean isForeground() {
        return mIsForeground;
    }

    @Override
    public void finishActivity() {
        if (!isFinishing()) {
            finish();
        }
    }

    @Override
    public void showToast(int resId) {
        showToast(mContext.getResources().getString(resId));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getPresenter() != null) {
            getPresenter().onActivityResult(requestCode, resultCode, data);
        }
    }

    public CompositeSubscription getCompositeSubscription() {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        return mCompositeSubscription;
    }
}
