package com.dec.dstar.network.subscriber;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.dec.dstar.DstarApplication;
import com.dec.dstar.utils.CommonToast;
import com.dec.dstar.utils.NetworkUtils;
import com.minchainx.network.exception.SessionTimeoutException;
import com.minchainx.network.utils.ErrorMessageUtils;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/2 20:03
 * 文件描述: 错误信息统一处理
 */
public abstract class ErrorSubscriber<T> extends Subscriber<T> {

    private Object mTag;

    public ErrorSubscriber setTag(Object tag) {
        mTag = tag;
        return this;
    }

    public Object getTag() {
        return mTag;
    }


    @Override
    public void onError(Throwable e) {
        if (!NetworkUtils.isNetworkConnected(DstarApplication.getInstance().getApplicationContext())) {
            CommonToast.showErrorMessage("网络请求出现异常,请重试");
        }
        //网络错误
        if (e instanceof HttpException) {
            dealWithHttpCode((HttpException) e);
        }
        //session id处理
        if (e instanceof SessionTimeoutException) {


        } else {
            CommonToast.showErrorMessage("网络请求出现异常,请重试");
            e.printStackTrace();
        }
    }

    //处理http code
    public void dealWithHttpCode(HttpException ex) {
        String errorMessage = ErrorMessageUtils.createErrorMessage(ex);
        onNetWorkException(ex, errorMessage);
    }

    public abstract void onNetWorkException(HttpException ex, String errorMessage);
}
