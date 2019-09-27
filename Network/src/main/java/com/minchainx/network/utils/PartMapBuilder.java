package com.minchainx.network.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PartMapBuilder {

    private Map<String, RequestBody> mParams;

    public PartMapBuilder() {
        mParams = new HashMap<>();
    }

    public PartMapBuilder put(@Nullable String key, int value) {
        if (!isEmpty(key)) {
            mParams.put(key, createRequestBody(value + ""));
        }
        return this;
    }

    public PartMapBuilder put(@Nullable String key, long value) {
        if (!isEmpty(key)) {
            mParams.put(key, createRequestBody(value + ""));
        }
        return this;
    }

    public PartMapBuilder put(@Nullable String key, double value) {
        if (!isEmpty(key)) {
            mParams.put(key, createRequestBody(value + ""));
        }
        return this;
    }

    public PartMapBuilder put(@Nullable String key, CharSequence value) {
        if (!isEmpty(key)) {
            String text = isEmpty(value) ? "" : value.toString();
            mParams.put(key, createRequestBody(text));
        }
        return this;
    }

    public Map<String, RequestBody> build() {
        return mParams;
    }

    private RequestBody createRequestBody(String text) {
        return RequestBody.create(MediaType.parse("multipart/form-data"), text);
    }

    private boolean isEmpty(CharSequence str) {
        return TextUtils.isEmpty(str);
    }
}
