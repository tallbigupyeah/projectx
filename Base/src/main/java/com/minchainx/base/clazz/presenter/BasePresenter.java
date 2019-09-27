package com.minchainx.base.clazz.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.minchainx.base.clazz.iview.IBaseView;

public class BasePresenter<T extends IBaseView> {

    protected Context mContext;
    protected T mView;
    protected Activity mActivity;
    protected Fragment mFragment;

    public BasePresenter(Context context, T view, Activity activity, Fragment fragment) {
        this.mContext = context;
        this.mView = view;
        this.mActivity = activity;
        this.mFragment = fragment;
    }

    public void onDestroy() {
        mContext = null;
        mView = null;
        mActivity = null;
        mFragment = null;
    }

    public void initializeData(Object object) {
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void onSaveInstanceState(Bundle outState) {
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
