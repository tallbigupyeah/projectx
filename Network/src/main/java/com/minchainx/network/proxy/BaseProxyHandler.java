package com.minchainx.network.proxy;

import android.text.TextUtils;

import com.minchainx.network.entity.BaseRequestEntity;
import com.minchainx.network.exception.SessionTimeoutException;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public abstract class BaseProxyHandler implements InvocationHandler {

    //重试次数
    protected int mRetryCount = 0;
    //最大刷新次数
    private int mMaxCount = 1;
    private Object mObject;
    protected boolean mIsSessionIdNeedRefresh;
    protected String mSessionId;

    public BaseProxyHandler(Object obj) {
        this.mObject = obj;
    }

    @Override
    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        return Observable
                .just(null)
                .flatMap(new Func1<Object, Observable<?>>() {
                    @Override
                    public Observable<?> call(Object o) {
                        try {
                            if (mIsSessionIdNeedRefresh) {
                                updateSessionId(method, args);
                            }
                            reSignature(method, args);
                            return (Observable<?>) method.invoke(mObject, args);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Throwable> observable) {
                        return observable
                                .zipWith(Observable.range(1, mMaxCount + 1), new Func2<Throwable, Integer, Wrapper>() {
                                    @Override
                                    public Wrapper call(Throwable throwable, Integer integer) {
                                        return new Wrapper(throwable, integer);
                                    }
                                })
                                .flatMap(new Func1<Wrapper, Observable<?>>() {
                                    @Override
                                    public Observable<?> call(Wrapper wrapper) {
                                        if (wrapper.throwable instanceof SessionTimeoutException
                                                && wrapper.index < mMaxCount + 1
                                                && mRetryCount < mMaxCount + 1) {
                                            mRetryCount++;
                                            return requestSessionId();
                                        }
                                        mRetryCount = 0;
                                        return Observable.error(wrapper.throwable);
                                    }
                                });
                    }
                });
    }

    private void updateSessionId(Method method, Object[] args) {
        mRetryCount = 0;
        if (mIsSessionIdNeedRefresh && !TextUtils.isEmpty(mSessionId)) {
            Annotation[][] annotationsArray = method.getParameterAnnotations();
            Annotation[] annotations;
            if (annotationsArray != null && annotationsArray.length > 0) {
                for (int i = 0; i < annotationsArray.length; i++) {
                    annotations = annotationsArray[i];
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Field) {
                            if (getSessionIdKey().equals(((Field) annotation).value())) {
                                args[i] = mSessionId;
                            }
                        }
                        if (annotation instanceof FieldMap || annotation instanceof QueryMap || annotation instanceof PartMap) {
                            Map<String, String> map = (Map<String, String>) args[i];
                            if (map != null) {
                                boolean containSessionId = false;
                                for (Map.Entry<String, String> entry : map.entrySet()) {
                                    if (entry != null && getSessionIdKey().equals(entry.getKey())) {
                                        containSessionId = true;
                                        break;
                                    }
                                }
                                if (containSessionId) {
                                    map.put(getSessionIdKey(), mSessionId);
                                }
                            }
                        }
                        if (annotation instanceof Query) {
                            if (getSessionIdKey().equals(((Query) annotation).value())) {
                                args[i] = mSessionId;
                            }
                        }
                    }
                }
            }
            mIsSessionIdNeedRefresh = false;
        }
    }

    private void reSignature(Method method, Object[] args) {
        Annotation[][] annotationsArray = method.getParameterAnnotations();
        Annotation[] annotations;
        if (annotationsArray != null && annotationsArray.length > 0) {
            for (int i = 0; i < annotationsArray.length; i++) {
                annotations = annotationsArray[i];
                for (Annotation annotation : annotations) {
                    if (annotation instanceof FieldMap || annotation instanceof QueryMap || annotation instanceof PartMap) {
                        Map<String, String> map = null;
                        try {
                            map = (Map<String, String>) args[i];
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (map != null) {
                            reSignature(map);
                        }
                    }
                    if (annotation instanceof Body) {
                        if (args[i] != null && args[i] instanceof BaseRequestEntity) {
                            BaseRequestEntity requestEntity = null;
                            try {
                                requestEntity = (BaseRequestEntity) args[i];
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (requestEntity != null) {
                                reSignature(requestEntity);
                            }
                        }
                    }
                }
            }
        }
    }

    protected abstract Observable<?> requestSessionId();

    protected abstract String getSessionIdKey();

    protected abstract void reSignature(Map<String, String> map);

    protected abstract void reSignature(BaseRequestEntity requestEntity);

    private class Wrapper {

        private int index;
        private Throwable throwable;

        public Wrapper(Throwable throwable, int index) {
            this.index = index;
            this.throwable = throwable;
        }
    }
}
