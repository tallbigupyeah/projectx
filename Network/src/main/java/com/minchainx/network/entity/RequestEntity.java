package com.minchainx.network.entity;

import com.minchainx.network.cache.CacheManager;
import com.minchainx.network.utils.LogUtils;

import java.io.Serializable;
import java.lang.reflect.Type;

import rx.Observable;
import rx.Subscriber;

public class RequestEntity implements Serializable {

    //
    private Observable observable;
    //
    private Subscriber subscriber;
    //
    private boolean retry;
    //是否使用缓存
    private boolean enableCache;
    //缓存有效时间
    private long cacheAvailableDuration;
    //缓存key
    private String key;
    //类类型
    private Class clazz;
    //带泛型的类型
    private Type type;
    //有内存缓存
    private boolean haveMemoryCache;
    //内存缓存生成的时间戳
    private long memoryCacheTimestamp;

    public RequestEntity() {
        this.retry = false;
        this.clazz = ResponseEntity.class;
        this.enableCache = false;
        this.cacheAvailableDuration = CacheManager.CACHE_AVAILABLE_DURATION_NONE;
        this.haveMemoryCache = false;
        this.memoryCacheTimestamp = 0;
    }

    public Observable getObservable() {
        return observable;
    }

    public RequestEntity setObservable(Observable observable) {
        this.observable = observable;
        return this;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public RequestEntity setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
        return this;
    }

    public boolean isRetry() {
        return retry;
    }

    public RequestEntity setRetry(boolean retry) {
        this.retry = retry;
        return this;
    }

    public boolean isEnableCache() {
        return enableCache;
    }

    public RequestEntity setEnableCache(boolean enableCache) {
        this.enableCache = enableCache;
        return this;
    }

    public long getCacheAvailableDuration() {
        return cacheAvailableDuration;
    }

    public RequestEntity setCacheAvailableDuration(long cacheAvailableDuration) {
        this.cacheAvailableDuration = cacheAvailableDuration;
        return this;
    }

    public String getKey() {
        return key;
    }

    public RequestEntity setKey(String key) {
        this.key = key;
        return this;
    }

    public Class getClazz() {
        return clazz;
    }

    public RequestEntity setClazz(Class clazz) {
        this.clazz = clazz;
        return this;
    }

    public Type getType() {
        return type;
    }

    public RequestEntity setType(Type type) {
        this.type = type;
        return this;
    }

    public boolean isHaveMemoryCache() {
        return haveMemoryCache;
    }

    public RequestEntity setHaveMemoryCache(boolean haveCache) {
        this.haveMemoryCache = haveCache;
        return this;
    }

    public long getMemoryCacheTimestamp() {
        return memoryCacheTimestamp;
    }

    public RequestEntity setMemoryCacheTimestamp(long cacheTime) {
        this.memoryCacheTimestamp = cacheTime;
        return this;
    }

    public boolean isAvailable() {
        if (cacheAvailableDuration < 0) {
            return false;
        }
        long delta = System.currentTimeMillis() - memoryCacheTimestamp;
        LogUtils.e("delta=" + delta);
        return delta < cacheAvailableDuration;
    }
}
