package com.dec.dstar.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.minchainx.base.clazz.iview.IBaseView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/2 19:35
 * 文件描述: mvp中的presenter基类
 */
public class BasePresenter<T extends IBaseView> extends com.minchainx.base.clazz.presenter.BasePresenter<T> {

    private List<Subscription> subscriberList = new ArrayList<>();

    public BasePresenter(Context context, T view, Activity activity,
                         Fragment fragment) {
        super(context, view, activity, fragment);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (Subscription errorSubscriber : subscriberList) {
            if (!errorSubscriber.isUnsubscribed()) {
                errorSubscriber.unsubscribe();
            }
        }
    }

    public void addSubscriber(Subscription subscription) {
        if (!subscriberList.contains(subscription)) {
            subscriberList.add(subscription);
        }
    }

    public void sendRequest(Observable observable, Subscriber subscriber) {
        addSubscriber(observable.subscribe(subscriber));
    }

    public void i(String s) {
        ((BaseActivity) mActivity).i(s);
    }

    public void e(String s) {
        ((BaseActivity) mActivity).e(s);
    }

    public void d(String s) {
        ((BaseActivity) mActivity).d(s);
    }

}
