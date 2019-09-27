package com.minchainx.networklite.callback.transform;

import com.minchainx.networklite.util.GsonUtils;

public class JsonGenericTransform implements IGenericTransform {

    @Override
    public <T> T transform(String responseText, Class<T> clazz) {
        return GsonUtils.getGsonInstance().fromJson(responseText, clazz);
    }
}
