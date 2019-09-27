package com.dec.dstar.network;

import android.text.TextUtils;

import com.dec.dstar.BuildConfig;
import com.dec.dstar.DstarApplication;
import com.dec.dstar.config.EnvironmentConfig;
import com.dec.dstar.network.response.loopring.BaseResponse;
import com.dec.dstar.utils.CommonToast;
import com.minchainx.network.BaseApiManager;
import com.minchainx.networklite.interceptor.HeaderInterceptor;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/3 10:33
 * 文件描述: 网络API
 */
public abstract class DstarApiManager<T> extends BaseApiManager {

    private final Class<T> dstarApiClass;
    private static String REGEX_HEADER_DEFAULT = ".*";

    private HeaderInterceptor headerInterceptor;
    private static List<DstarApiManager> managers = new ArrayList<>();

    protected DstarApiManager(Class<T> apiService, String baseUrl) {
        super(DstarApplication.getInstance(), baseUrl);
        initOkHttpClient();
        dstarApiClass = apiService;
    }

    protected T getApiService() {
        return getApiService(dstarApiClass);
    }

    /**
     * 添加头部
     */
    public void addInterceptor() {
        addNetworkInterceptor(createDefaultHeaderInterceptor());
        //后面根据需要加
//        addInterceptor(new TimeCalibrationInterceptor(new TimeCalibrationInterceptor.Callback() {
//            @Override
//            public void requestServerTime() {
//
//            }
//
//            @Override
//            public boolean getLogOutputState() {
//                return false;
//            }
//
//            @Override
//            public String generateLogTag() {
//                return "";
//            }
//        }));
    }

    private HeaderInterceptor createDefaultHeaderInterceptor() {
        headerInterceptor = new HeaderInterceptor(new HeaderInterceptor.Callback() {
            @Override
            public boolean getLogOutputState() {
                return BuildConfig.DEBUG;
            }

            @Override
            public String generateLogTag() {
                return "okhttpHeader";
            }
        });
        headerInterceptor.addHeader(REGEX_HEADER_DEFAULT, createDefaultHeader());
        return headerInterceptor;
    }

    public void updateJWT(String jwt) {
        if (!TextUtils.isEmpty(jwt) && headerInterceptor != null && headerInterceptor.getHeader(REGEX_HEADER_DEFAULT) != null) {
            headerInterceptor.getHeader(REGEX_HEADER_DEFAULT).put("jwt", jwt);
        }
    }

    /**
     * 自定义头部信息
     *
     * @return map
     */
    private HashMap<String, String> createDefaultHeader() {
        HashMap<String, String> headers = new HashMap<>();
        String str = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        headers.put("content-type", "application/json");
        headers.put("accept", "application/json");

        return headers;
    }

    /**
     * 安全证书初始化
     */
    private void initOkHttpClient() {

        if (EnvironmentConfig.getInstance().getEnvironment().equals(EnvironmentConfig.ENVIRONMENT.TEST)){

            initOkHttpClientBuilder(false, false);
        }else if(EnvironmentConfig.getInstance().getEnvironment().equals(EnvironmentConfig.ENVIRONMENT.PRODUCTION)){

            initOkHttpClientBuilder(false, true);
        }

        addInterceptor();
        getOkHttpClient();
    }

    @Override
    protected InputStream[] getCertInputStreamArray(boolean useCertificate, boolean isProduct) {
        InputStream[] iss = null;
        if (useCertificate) {
            //https的后面再设置
            iss = new InputStream[4];
//            iss[0] = new ByteArrayInputStream(CRT.PRD.getBytes());
            iss[1] = new ByteArrayInputStream(getCertProduct().getBytes());
//            iss[2] = new ByteArrayInputStream(CRT.PRE.getBytes());
            iss[3] = new ByteArrayInputStream(getCertPre().getBytes());
        }
        return iss;
    }

    /**
     * Transformer转换
     */
    protected <T> Observable.Transformer<? extends BaseResponse<T>, T> transformerDefault() {
        return new Observable.Transformer<BaseResponse<T>, T>() {
            @Override
            public Observable<T> call(Observable<BaseResponse<T>> responseObservable) {
                return responseObservable.map(new Func1<BaseResponse<T>, T>() {
                    @Override
                    public T call(BaseResponse<T> response) {

                        if (response != null && response.id == 64) {
                            return response.result;
                        } else {
                            CommonToast.showErrorMessage(response.error + "");
                            return null;
                        }
                    }
                });
            }
        };
    }

    /**
     * 设置主线程和io线程
     */
    protected <T> Observable.Transformer<T, T> observe() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 默认 transformerDefault + observe 结合
     */
    protected Observable loaderComposeDefault(Observable observable) {
        //订阅操作
        return observable.compose(observe()).compose(transformerDefault());
    }

    /**
     * 通用observe
     */
    protected Observable loaderComposeCommon(Observable observable) {
        //订阅操作
        return observable.compose(observe());
    }

    /**
     * 通用observe同步处理
     */
    protected Observable syncLoaderComposeCommon(Observable observable) {
        //订阅操作
        return observable;
    }

    /**
     * 环境切换,更新baseUrl
     *
     * @param baseUrl
     */
    public void switchEnvironment(String baseUrl) {
        this.mBaseUrl = baseUrl;
        reset();
    }

    /**
     * 重置
     */
    public void reset() {
        getInterceptorList().clear();
        getNetworkInterceptorList().clear();
        resetOkHttpClient();
        clearApiService();
        initOkHttpClient();
    }

    protected abstract String getBaseUrl();
}
