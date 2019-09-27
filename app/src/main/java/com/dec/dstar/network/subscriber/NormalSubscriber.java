package com.dec.dstar.network.subscriber;

import com.dec.dstar.utils.CommonToast;

import retrofit2.adapter.rxjava.HttpException;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/3 09:31
 * 文件描述: 网络请求订阅者
 */
public abstract class NormalSubscriber extends ErrorSubscriber {

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);

    }

    @Override
    public void onNetWorkException(HttpException ex, String errorMessage) {
        CommonToast.showErrorMessage(errorMessage + "");
    }
}
