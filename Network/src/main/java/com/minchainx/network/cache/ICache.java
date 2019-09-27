package com.minchainx.network.cache;

import com.minchainx.network.entity.RequestEntity;

import rx.Observable;

public interface ICache {

    <T> Observable<T> get(RequestEntity requestEntity);

    <T> void put(String key, T t);

    void clear(String key);

    void clearAll();
}
