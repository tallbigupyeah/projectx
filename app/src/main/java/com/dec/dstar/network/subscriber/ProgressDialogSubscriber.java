package com.dec.dstar.network.subscriber;

import com.dec.dstar.base.IBaseView;
import com.dec.dstar.utils.CommonToast;

import retrofit2.adapter.rxjava.HttpException;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/3 14:33
 * 文件描述: 网络加载进度条处理
 */
public abstract class ProgressDialogSubscriber<T> extends ErrorSubscriber<T> {

    private IBaseView mIBaseView;

    public ProgressDialogSubscriber(IBaseView iBaseView) {
        mIBaseView = iBaseView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mIBaseView != null) {
            mIBaseView.showProgressBar();
        }
    }

    @Override
    public void onCompleted() {
        if (mIBaseView != null) {
            mIBaseView.hideProgressBar();
        }
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
        if (mIBaseView != null) {
            mIBaseView.hideProgressBar();
        }
    }

    @Override
    public void onNetWorkException(HttpException ex, String errorMessage) {
        if (mIBaseView != null) {
            mIBaseView.hideProgressBar();
        }
        CommonToast.showErrorMessage(errorMessage + "");
    }
}
