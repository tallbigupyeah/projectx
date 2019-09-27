package com.minchainx.networklite.request;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

public class GetRequest extends OkHttpRequest {

    public GetRequest(String url, Map<String, String> params, Map<String, String> headers, Object tag, int id) {
        super(url, params, headers, tag, id);
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return mBuilder.get().build();
    }
}
