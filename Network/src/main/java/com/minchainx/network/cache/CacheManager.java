package com.minchainx.network.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.text.TextUtils;

import com.minchainx.network.BaseApiManager;
import com.minchainx.network.entity.RequestEntity;
import com.minchainx.network.entity.ResponseEntity;
import com.minchainx.network.exception.NetworkException;
import com.minchainx.network.utils.LogUtils;
import com.minchainx.network.utils.NetworkUtils;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class CacheManager {

    /**
     * 不使用缓存
     */
    public static final int CACHE_AVAILABLE_DURATION_NONE = -1;
    /**
     * 默认缓存时间
     */
    public static final int CACHE_AVAILABLE_DURATION_DEFAULT = 60 * 1000;

    /**
     * 内存
     */
    public static final int DATA_CHANNEL_MEMORY = 11001;
    /**
     * 本地
     */
    public static final int DATA_CHANNEL_DISK = 11002;
    /**
     * 数据库
     */
    public static final int DATA_CHANNEL_DATABASE = 11003;
    /**
     * 网络
     */
    public static final int DATA_CHANNEL_NETWORK = 11004;

    /**
     * 保存缓存的SharedPreferences Key
     */
    private static final String KEY_SP_NAME = "CacheSP";
    /**
     * APP版本号Key
     */
    private static final String KEY_APP_VERSION = "AppVersionKey";

    private static CacheManager sInstance;
    private Context mContext;
    private ICache mMemoryCache;
    private ICache mDiskCache;
//    private ICache mDatabaseCache;
    private long mCacheAvailableDuration;

    private CacheManager(Context context) {
        this.mContext = context;
        mMemoryCache = new MemoryCache();
        mDiskCache = new DiskCache(context);
//        mDatabaseCache = new DatabaseCache(context);
        mCacheAvailableDuration = CACHE_AVAILABLE_DURATION_DEFAULT;
//        String oldVersionCode = getString(context, KEY_APP_VERSION, "", KEY_SP_NAME);
//        String versionCode = getVersionCode(context);
//        if (!TextUtils.isEmpty(versionCode) && !versionCode.equals(oldVersionCode)) {
//            putString(context, KEY_APP_VERSION, versionCode, KEY_SP_NAME);
//            clearAll();
//        }
    }

    public static CacheManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (CacheManager.class) {
                if (sInstance == null) {
                    sInstance = new CacheManager(context);
                }
            }
        }
        return sInstance;
    }

    private <T> Observable<T> memory(final RequestEntity requestEntity) {
        return mMemoryCache
                .<T>get(requestEntity)
                .doOnNext(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        LogUtils.i("memory-->onNext-->t=" + t);
                        if (t != null && t instanceof ResponseEntity) {
                            ((ResponseEntity) t).dataChannel = DATA_CHANNEL_MEMORY;
                        }
                    }
                });
    }

    private <T> Observable<T> disk(final RequestEntity requestEntity) {
        return mDiskCache
                .<T>get(requestEntity)
                .doOnNext(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        LogUtils.i("disk-->onNext-->t=" + t);
                        if (t != null && t instanceof ResponseEntity) {
                            ResponseEntity responseEntity = (ResponseEntity) t;
                            responseEntity.dataChannel = DATA_CHANNEL_DISK;

                            ResponseEntity memoryResponseEntity = responseEntity.copy();
                            if (memoryResponseEntity != null) {
                                memoryResponseEntity.dataChannel = DATA_CHANNEL_MEMORY;
                                mMemoryCache.put(requestEntity.getKey(), memoryResponseEntity);
                            }
                        }
                    }
                });
    }

//    private <T> Observable<T> database(final RequestEntity requestEntity) {
//        return mDatabaseCache
//                .<T>get(requestEntity)
//                .doOnNext(new Action1<T>() {
//                    @Override
//                    public void call(T t) {
//                        LogUtils.i("database-->onNext-->t=" + t);
//                        if (t != null && t instanceof ResponseEntity) {
//                            ResponseEntity responseEntity = (ResponseEntity) t;
//                            responseEntity.dataChannel = DATA_CHANNEL_DATABASE;
//
//                            ResponseEntity memoryResponseEntity = responseEntity.copy();
//                            if (memoryResponseEntity != null) {
//                                memoryResponseEntity.dataChannel = DATA_CHANNEL_MEMORY;
//                                mMemoryCache.put(requestEntity.getKey(), memoryResponseEntity);
//                            }
//                        }
//                    }
//                });
//    }

    private <T> Observable<T> network(final RequestEntity requestEntity) {
        setCacheAvailableDuration(requestEntity.getCacheAvailableDuration());
        Observable observable = requestEntity.getObservable();
//        if (requestEntity.isRetry()) {
//            observable = observable.retryWhen(new RetryWhenException());
//        }
        return observable
                .filter(new Func1<T, Boolean>() {
                    @Override
                    public Boolean call(T t) {
                        if (requestEntity.getCacheAvailableDuration() != CACHE_AVAILABLE_DURATION_NONE && requestEntity.isAvailable()) {
                            return false;
                        }
                        return true;
                    }
                })
                .compose(BaseApiManager.applySchedulers())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        if (!NetworkUtils.isNetworkConnected(mContext)) {
                            throw new NetworkException(NetworkException.ErrorType.NO_NETWORK_CONNECTED, "no network conntected");
                        }
                    }
                })
                .doOnNext(new Action1<T>() {
                    @Override
                    public void call(T t) {
                        LogUtils.i("network-->onNext-->t=" + t);
                        if (t != null && t instanceof ResponseEntity) {
                            ResponseEntity responseEntity = (ResponseEntity) t;
                            if (responseEntity != null && responseEntity.data != null
                                    && (ResponseEntity.ERROR_CODE_SUCCESS.equals(responseEntity.errorCode)
                                    || ResponseEntity.FLAG_SUCCESS == responseEntity.flag)) {
                                LogUtils.i("network-->onNext-->save cache");
                                long now = System.currentTimeMillis();
                                responseEntity.dataChannel = DATA_CHANNEL_NETWORK;
                                responseEntity.timestamp = now;

                                ResponseEntity memoryResponseEntity = responseEntity.copy();
                                if (memoryResponseEntity != null) {
                                    memoryResponseEntity.dataChannel = DATA_CHANNEL_MEMORY;
                                    mMemoryCache.put(requestEntity.getKey(), memoryResponseEntity);
                                }

                                ResponseEntity diskResponseEntity = responseEntity.copy();
                                if (diskResponseEntity != null) {
                                    diskResponseEntity.dataChannel = DATA_CHANNEL_DISK;
                                    mDiskCache.put(requestEntity.getKey(), diskResponseEntity);
                                }

//                                ResponseEntity databaseResponseEntity = responseEntity.copy();
//                                if (databaseResponseEntity != null) {
//                                    databaseResponseEntity.dataChannel = DATA_CHANNEL_DATABASE;
//                                    mDatabaseCache.put(requestEntity.getKey(), databaseResponseEntity);
//                                }
                            } else {
                                LogUtils.i("network-->onNext-->do nothing");
                            }
                        }
                    }
                });
    }


    public void clearMemory(String key) {
        mMemoryCache.clear(key);
    }

    public void clearDisk(String key) {
        mDiskCache.clear(key);
    }

//    public void clearDatabase(String key) {
//        mDatabaseCache.clear(key);
//    }

    public void clearAll() {
        LogUtils.i("CacheManager--->clearAll");
        mMemoryCache.clearAll();
        mDiskCache.clearAll();
//        mDatabaseCache.clearAll();
    }

    public long getCacheTime() {
        return mCacheAvailableDuration;
    }

    public CacheManager setCacheAvailableDuration(long cacheDuration) {
        this.mCacheAvailableDuration = cacheDuration;
        return this;
    }

    public Subscription performRequest(final RequestEntity requestEntity) {
        requestEntity.setHaveMemoryCache(false);
        return Observable
                .concat(
                        memory(requestEntity),
                        disk(requestEntity),
//                        database(requestEntity),
                        network(requestEntity)
                )
                .subscribe(requestEntity.getSubscriber());
    }

    private String getString(Context context, String key, String defValue, String spName) {
        if (context == null || TextUtils.isEmpty(key) || TextUtils.isEmpty(spName)) {
            return "";
        }
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    private boolean putString(Context context, String key, String value, String spName) {
        if (context == null || TextUtils.isEmpty(key) || TextUtils.isEmpty(spName)) {
            return false;
        }
        SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
        return sp.edit().putString(key, value).commit();
    }

    /**
     * 版本号
     */
    private String getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return String.valueOf(packageInfo.versionCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }
}
