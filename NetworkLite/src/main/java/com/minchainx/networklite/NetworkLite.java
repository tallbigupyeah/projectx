package com.minchainx.networklite;

import android.os.Handler;
import android.os.Looper;

import com.minchainx.networklite.callback.Callback;
import com.minchainx.networklite.interceptor.HttpLoggerInterceptor;
import com.minchainx.networklite.request.RequestCall;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class NetworkLite {

    public static final long DEFAULT_MILLISECONDS = 10_000L;

    private OkHttpClient mOkHttpClient;
    private Handler mMainThreadHandler;
    private boolean mLogOutput;

    public NetworkLite() {
        this(null);
    }

    public NetworkLite(OkHttpClient client) {
        if (client == null) {
            mOkHttpClient = createDefaultOkHttpClient();
        } else {
            mOkHttpClient = client;
        }
        mMainThreadHandler = new Handler(Looper.getMainLooper());
        mLogOutput = false;
    }

    public NetworkLite replaceClient(OkHttpClient client) {
        if (client != null) {
            this.mOkHttpClient = client;
        }
        return this;
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    public boolean getLogOutput() {
        return mLogOutput;
    }

    public void setLogOutput(boolean enable) {
        this.mLogOutput = enable;
    }

    /**
     * 创建默认OkHttpClient
     */
    private OkHttpClient createDefaultOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .addInterceptor(new HttpLoggerInterceptor(new HttpLoggerInterceptor.Callback() {
                    @Override
                    public boolean getLogOutputState() {
                        return mLogOutput;
                    }

                    @Override
                    public String generateLogTag() {
                        return "okhttp";
                    }
                }))
                .hostnameVerifier(new HttpsHelper.UnSafeHostnameVerifier())
                .build();
    }

    /**
     * 执行请求
     *
     * @param requestCall
     * @param callback
     */
    public void execute(RequestCall requestCall, Callback callback) {
        if (callback == null) {
            callback = Callback.DEFAULT;
        }
        final Callback finalCallback = callback;
        final int id = requestCall.getOkHttpRequest().getId();
        requestCall.getCall().enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onFailureCallback(call, e, finalCallback, id);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (call.isCanceled()) {
                    onFailureCallback(call, new Exception("request cancelled"), finalCallback, id);
                    return;
                }
                try {
                    Object responseObject = finalCallback.parseResponse(response, id);
                    onSuccessCallback(call, responseObject, finalCallback, id);
                } catch (Exception e) {
                    onFailureCallback(call, e, finalCallback, id);
                } finally {
                    if (response.body() != null) {
                        response.body().close();
                    }
                }
            }
        });
    }

    /**
     * 请求失败回调
     *
     * @param call
     * @param exception
     * @param callback
     * @param id
     */
    public void onFailureCallback(final Call call, final Exception exception, final Callback callback, final int id) {
        getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onFailure(call, exception, id);
                    callback.onPostExecute(false, id);
                }
            }
        });
    }

    /**
     * 请求成功回调
     *
     * @param call
     * @param response
     * @param callback
     * @param id
     */
    public void onSuccessCallback(final Call call, final Object response, final Callback callback, final int id) {
        getMainThreadHandler().post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onSuccess(call, response, id);
                    callback.onPostExecute(true, id);
                }
            }
        });
    }

    /**
     * 根据tag取消请求
     *
     * @param tag
     */
    public void cancelByTag(Object tag) {
        if (tag != null) {
            for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
            for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
        }
    }
}
