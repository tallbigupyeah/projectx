package com.minchainx.networklite.request;

import com.minchainx.networklite.NetworkLite;
import com.minchainx.networklite.callback.Callback;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestCall {

    private long mReadTimeout;
    private long mWriteTimeout;
    private long mConnectTimeout;
    private OkHttpRequest mOkHttpRequest;
    private Request mRequest;
    private Call mCall;
    private OkHttpClient mOkHttpClient;

    public RequestCall(OkHttpRequest request) {
        if (request == null) {
            throw new RuntimeException("OkHttpRequest can not be null!");
        }
        this.mOkHttpRequest = request;
    }

    public RequestCall readTimeout(long readTimeout) {
        this.mReadTimeout = readTimeout;
        return this;
    }

    public RequestCall writeTimeout(long writeTimeout) {
        this.mWriteTimeout = writeTimeout;
        return this;
    }

    public RequestCall connectTimeout(long connectTimeout) {
        this.mConnectTimeout = connectTimeout;
        return this;
    }

    public Call getCall() {
        return mCall;
    }

    public Request getRequest() {
        return mRequest;
    }

    public OkHttpRequest getOkHttpRequest() {
        return mOkHttpRequest;
    }

    public Call buildCall(NetworkLite helper, Callback callback) {
        mRequest = mOkHttpRequest.generateRequest(callback);
        if (mReadTimeout > 0 || mWriteTimeout > 0 || mConnectTimeout > 0) {
            mReadTimeout = mReadTimeout > 0 ? mReadTimeout : NetworkLite.DEFAULT_MILLISECONDS;
            mWriteTimeout = mWriteTimeout > 0 ? mWriteTimeout : NetworkLite.DEFAULT_MILLISECONDS;
            mConnectTimeout = mConnectTimeout > 0 ? mConnectTimeout : NetworkLite.DEFAULT_MILLISECONDS;

            mOkHttpClient = helper.getOkHttpClient()
                    .newBuilder()
                    .readTimeout(mReadTimeout, TimeUnit.MILLISECONDS)
                    .writeTimeout(mWriteTimeout, TimeUnit.MILLISECONDS)
                    .connectTimeout(mConnectTimeout, TimeUnit.MILLISECONDS)
                    .build();

            mCall = mOkHttpClient.newCall(mRequest);
        } else {
            mCall = helper.getOkHttpClient().newCall(mRequest);
        }
        return mCall;
    }

    public void execute(NetworkLite helper, Callback callback) {
        buildCall(helper, callback);
        if (callback != null) {
            callback.onPreExecute(getOkHttpRequest().getId());
        }
        helper.execute(this, callback);
    }

    public Response execute(NetworkLite helper) throws IOException {
        buildCall(helper, null);
        return mCall.execute();
    }

    public void cancel() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
}
