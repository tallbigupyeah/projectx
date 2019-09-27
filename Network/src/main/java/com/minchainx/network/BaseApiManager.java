package com.minchainx.network;

import android.content.Context;

import com.minchainx.network.cache.CacheManager;
import com.minchainx.network.entity.RequestEntity;
import com.minchainx.network.exception.NetworkException;
import com.minchainx.network.factory.RetrofitFactory;
import com.minchainx.network.utils.LogUtils;
import com.minchainx.network.utils.NetworkUtils;
import com.minchainx.networklite.NetworkLiteHelper;
import com.minchainx.networklite.interceptor.HttpLoggerInterceptor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public abstract class BaseApiManager {

    private static final String CERT_PRE = "";
    private static final String CERT_PRODUCT = "";

    protected Context mContext;
    protected String mBaseUrl;
    private OkHttpClient mOkHttpClient;
    private final Map<String, Object> mRetrofitMap = new HashMap<String, Object>();
    private final List<Interceptor> mNetworkInterceptorList = new ArrayList<Interceptor>();
    private final List<Interceptor> mInterceptorList = new ArrayList<Interceptor>();
    private OkHttpClient.Builder mBuilder;

    protected BaseApiManager(Context context, String baseUrl) {
        this.mContext = context;
        this.mBaseUrl = baseUrl;
    }

    protected synchronized OkHttpClient.Builder initOkHttpClientBuilder(boolean useCertificate, boolean isProduct) {
        InputStream[] iss = getCertInputStreamArray(useCertificate, isProduct);
        mBuilder = RetrofitFactory.getDefaultClientBuilder(mContext, iss);
        return mBuilder;
    }

    protected InputStream[] getCertInputStreamArray(boolean useCertificate, boolean isProduct) {
        InputStream[] iss = null;
        if (useCertificate) {
            iss = new InputStream[1];
            if (isProduct) {
                iss[0] = new ByteArrayInputStream(getCertProduct().getBytes());
            } else {
                iss[0] = new ByteArrayInputStream(getCertPre().getBytes());
            }
        }
        return iss;
    }

    protected String getCertPre() {
        return CERT_PRE;
    }

    protected String getCertProduct() {
        return CERT_PRODUCT;
    }

    public synchronized OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = createOkHttpClient();
        }
        return mOkHttpClient;
    }

    private OkHttpClient createOkHttpClient() {
        if (mBuilder == null) {
            throw new RuntimeException("you need to call initOkHttpClientBuilder first");
        }
        if (mNetworkInterceptorList != null && mNetworkInterceptorList.size() > 0) {
            for (Interceptor interceptor : mNetworkInterceptorList) {
                mBuilder.addNetworkInterceptor(interceptor);
            }
        }
        if (mInterceptorList != null && mInterceptorList.size() > 0) {
            for (Interceptor interceptor : mInterceptorList) {
                mBuilder.addInterceptor(interceptor);
            }
        }
        //网络请求日志
        mBuilder.addInterceptor(new HttpLoggerInterceptor(new HttpLoggerInterceptor.Callback() {
            @Override
            public boolean getLogOutputState() {
                return LogUtils.isDebug();
            }

            @Override
            public String generateLogTag() {
                return "okhttp";
            }
        }));
        OkHttpClient okHttpClient = mBuilder.build();
        NetworkLiteHelper.replaceClient(okHttpClient);
        return okHttpClient;
    }

    public void resetOkHttpClient() {
        mOkHttpClient = null;
    }

    public List<Interceptor> getNetworkInterceptorList() {
        return mNetworkInterceptorList;
    }

    public void addNetworkInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            mNetworkInterceptorList.add(interceptor);
        }
    }

    public List<Interceptor> getInterceptorList() {
        return mInterceptorList;
    }

    public void addInterceptor(Interceptor interceptor) {
        if (interceptor != null) {
            mInterceptorList.add(interceptor);
        }
    }

    private Retrofit createDefaultRetrofit() {
        return RetrofitFactory.createDefaultRetrofit(mBaseUrl, getOkHttpClient());
    }

    public <T> T getApiService(Class<T> clazz) {
        return getApiService(clazz, clazz.getSimpleName());
    }

    public <T> T getApiService(Class<T> clazz, String key) {
        if (!mRetrofitMap.containsKey(key) || mRetrofitMap.get(key) == null) {
            synchronized (BaseApiManager.class) {
                if (!mRetrofitMap.containsKey(key) || mRetrofitMap.get(key) == null) {
                    T t = createDefaultApiService(clazz);
                    mRetrofitMap.put(key, t);
                    return t;
                }
            }
        }
        return clazz.cast(mRetrofitMap.get(key));
    }

    public void clearApiService() {
        mRetrofitMap.clear();
    }

    private <T> T createDefaultApiService(Class<T> clazz) {
        final Retrofit retrofit = createDefaultRetrofit();
        if (retrofit != null) {
            return retrofit.create(clazz);
        }
        return null;
    }

    public Subscription performRequest(final RequestEntity requestEntity) {
        if (requestEntity == null || requestEntity.getObservable() == null) {
            return null;
        }
        Subscription subscription;
        if (requestEntity.isEnableCache()) {
            subscription = CacheManager.getInstance(mContext).performRequest(requestEntity);
        } else {
            Observable observable = requestEntity.getObservable();
//            if (requestEntity.isRetry()) {
//                observable = observable.retryWhen(new RetryWhenException());
//            }
            subscription = observable
                    .compose(applySchedulers())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            if (!NetworkUtils.isNetworkConnected(mContext)) {
                                throw new NetworkException(NetworkException.ErrorType.NO_NETWORK_CONNECTED, "no network conntected");
                            }
                        }
                    })
                    .subscribe(requestEntity.getSubscriber());
        }
        return subscription;
    }

    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
