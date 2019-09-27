package com.dec.dstar.utils;

import android.util.Log;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/11 14:33
 * 文件描述: 使用rxjava，耗时操作放在子线程，处理完后返回主线程
 */
public class AsyncUtils<T> {

    public interface AsyncUtilsListener<T>{
        T subThreadOperate();
        void mainThreadCallback(T t);
    }

    public AsyncUtils(final AsyncUtilsListener<T> mListener){

        Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {

                subscriber.onNext(mListener.subThreadOperate());
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(T t) {
                        mListener.mainThreadCallback(t);
                    }
                });
    }
}

