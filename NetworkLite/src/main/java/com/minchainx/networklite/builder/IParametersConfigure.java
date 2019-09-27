package com.minchainx.networklite.builder;

import java.util.Map;

public interface IParametersConfigure {

    OkHttpRequestBuilder setParams(Map<String, String> params);

    OkHttpRequestBuilder addParam(String key, String value);
}
