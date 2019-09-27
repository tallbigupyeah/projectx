package com.minchainx.networklite.callback.transform;

public interface IGenericTransform {

    <T> T transform(String responseText, Class<T> clazz);
}
