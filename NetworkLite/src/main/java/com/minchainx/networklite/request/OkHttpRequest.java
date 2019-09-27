package com.minchainx.networklite.request;

import com.minchainx.networklite.callback.Callback;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

public abstract class OkHttpRequest {

    protected int mId;
    protected String mUrl;
    protected Object mTag;
    protected Map<String, String> mParams;
    protected Map<String, String> mHeaders;
    protected Request.Builder mBuilder = new Request.Builder();

    protected OkHttpRequest(String url, Map<String, String> params, Map<String, String> headers,
                            Object tag, int id) {
        this.mUrl = url;
        this.mParams = params;
        this.mHeaders = headers;
        this.mTag = tag;
        this.mId = id;
        if (url == null) {
            throw new IllegalArgumentException("url can not be null.");
        }
        initializeBuilder();
    }

    /**
     * 初始化一些基本参数 url , tag , headers
     */
    private void initializeBuilder() {
        mBuilder.url(mUrl).tag(mTag);
        appendHeaders();
    }

    protected void appendHeaders() {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (mHeaders == null || mHeaders.isEmpty()) {
            return;
        }
        for (String key : mHeaders.keySet()) {
            headerBuilder.add(key, mHeaders.get(key));
        }
        mBuilder.headers(headerBuilder.build());
    }

    protected RequestBody buildRequestBody() {
        return null;
    }

    protected RequestBody wrapRequestBody(RequestBody requestBody, Callback callback) {
        return requestBody;
    }

    protected abstract Request buildRequest(RequestBody requestBody);

    public RequestCall build() {
        return new RequestCall(this);
    }

    public Request generateRequest(Callback callback) {
        RequestBody requestBody = buildRequestBody();
        RequestBody wrappedRequestBody = wrapRequestBody(requestBody, callback);
        Request request = buildRequest(wrappedRequestBody);
        return request;
    }

    public int getId() {
        return mId;
    }
}
