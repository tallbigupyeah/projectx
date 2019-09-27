package com.minchainx.network.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class ParamsMapBuilder {

    private Map<String, String> mMap;

    public ParamsMapBuilder() {
        mMap = new HashMap<>();
    }

    public ParamsMapBuilder put(@Nullable String key, int value) {
        if (!isEmpty(key)) {
            mMap.put(key, value + "");
        }
        return this;
    }

    public ParamsMapBuilder put(@Nullable String key, long value) {
        if (!isEmpty(key)) {
            mMap.put(key, value + "");
        }
        return this;
    }

    public ParamsMapBuilder put(@Nullable String key, double value) {
        if (!isEmpty(key)) {
            mMap.put(key, value + "");
        }
        return this;
    }

    public ParamsMapBuilder put(@Nullable String key, CharSequence value) {
        if (!isEmpty(key)) {
            String text = isEmpty(value) ? "" : value.toString();
            mMap.put(key, text);
        }
        return this;
    }

    public Map<String, String> build() {
        return mMap;
    }

    private boolean isEmpty(CharSequence str) {
        return TextUtils.isEmpty(str);
    }

}
