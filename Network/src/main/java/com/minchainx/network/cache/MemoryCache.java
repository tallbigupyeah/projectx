package com.minchainx.network.cache;

import android.text.TextUtils;
import android.util.LruCache;

import com.minchainx.network.entity.RequestEntity;
import com.minchainx.network.entity.ResponseEntity;
import com.minchainx.networklite.util.GsonUtils;

import java.io.UnsupportedEncodingException;

import rx.Observable;
import rx.Subscriber;

public class MemoryCache implements ICache {

    private LruCache<String, String> mCache;

    public MemoryCache() {
        final int maxMemory = (int) Runtime.getRuntime().maxMemory();
        final int cacheSize = maxMemory / 8;
        mCache = new LruCache<String, String>(cacheSize) {
            @Override
            protected int sizeOf(String key, String value) {
                try {
                    return value.getBytes("UTF-8").length;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return value.getBytes().length;
                }
            }
        };
    }

    @Override
    public <T> Observable<T> get(final RequestEntity requestEntity) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                String result = mCache.get(requestEntity.getKey());
                if (subscriber.isUnsubscribed()) {
                    return;
                }
                if (!TextUtils.isEmpty(result)) {
                    T t;
                    try {
                        if (requestEntity.getType() != null) {
                            t = GsonUtils.getGsonInstance().fromJson(result, requestEntity.getType());
                        } else {
                            t = (T) GsonUtils.getGsonInstance().fromJson(result, requestEntity.getClazz());
                        }
                        subscriber.onNext(t);
                        requestEntity.setHaveMemoryCache(true);
                        if (t != null && t instanceof ResponseEntity) {
                            long timestamp = ((ResponseEntity) t).timestamp;
                            requestEntity.setMemoryCacheTimestamp(timestamp);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                subscriber.onCompleted();
            }
        });
    }

    @Override
    public <T> void put(String key, T t) {
        if (t != null) {
            try {
                mCache.put(key, GsonUtils.getGsonInstance().toJson(t));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void clear(String key) {
        mCache.remove(key);
    }

    @Override
    public void clearAll() {
        mCache.evictAll();
    }
}
