package com.minchainx.network.cache;

import android.content.Context;
import android.text.TextUtils;

import com.minchainx.network.BaseApiManager;
import com.minchainx.network.entity.RealmResponseCache;
import com.minchainx.network.entity.RequestEntity;
import com.minchainx.network.entity.ResponseEntity;
import com.minchainx.network.realm.NetworkModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DatabaseCache implements ICache {

    private static final String DB_PRIMARY_KEY = "key";
    private RealmConfiguration mRealmConfiguration;

    public DatabaseCache(Context context) {
        Realm.init(context);
        mRealmConfiguration = new RealmConfiguration.Builder()
                .name("NetworkLibrary.realm")
                .modules(new NetworkModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    @Override
    public <T> Observable<T> get(final RequestEntity requestEntity) {
        return Observable
                .create(new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        Realm realm = null;
                        try {
                            realm = Realm.getInstance(mRealmConfiguration);
                            RealmResponseCache realmResponseCache = realm.where(RealmResponseCache.class)
                                    .equalTo(DB_PRIMARY_KEY, requestEntity.getKey())
                                    .findFirst();
                            if (realmResponseCache != null) {
                                ResponseEntity responseEntity = realmResponseCache.convertToResponseEntity(requestEntity.getType());
                                if (subscriber.isUnsubscribed()) {
                                    return;
                                }
                                if (responseEntity != null) {
                                    T t = (T) responseEntity;
                                    if (t != null && !requestEntity.isHaveMemoryCache()) {
                                        subscriber.onNext(t);
                                        requestEntity.setMemoryCacheTimestamp(responseEntity.timestamp);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (realm != null) {
                                realm.close();
                            }
                        }
                        subscriber.onCompleted();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public <T> void put(final String key, final T t) {
        Observable
                .create(new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        if (t != null) {
                            Realm realm = null;
                            try {
                                realm = Realm.getInstance(mRealmConfiguration);
                                if (t instanceof ResponseEntity) {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            realm.copyToRealmOrUpdate(RealmResponseCache.convertToRealmResponeCache(key, (ResponseEntity) t));
                                        }
                                    });
                                } else if (t instanceof RealmResponseCache) {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            RealmResponseCache realmResponseCache = (RealmResponseCache) t;
                                            realmResponseCache.setKey(key);
                                            realm.copyToRealmOrUpdate(realmResponseCache);
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                if (realm != null) {
                                    realm.close();
                                }
                            }
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(t);
                            }
                        }
                        subscriber.onCompleted();
                    }
                })
                .compose(BaseApiManager.<T>applySchedulers())
                .subscribe();
    }

    @Override
    public void clear(final String key) {
        Realm realm = null;
        try {
            realm = Realm.getInstance(mRealmConfiguration);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (!TextUtils.isEmpty(key)) {
                        RealmResponseCache realmResponseCache = realm.where(RealmResponseCache.class)
                                .equalTo(DB_PRIMARY_KEY, key)
                                .findFirst();
                        if (realmResponseCache != null) {
                            realmResponseCache.deleteFromRealm();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    @Override
    public void clearAll() {
        Realm realm = null;
        try {
            realm = Realm.getInstance(mRealmConfiguration);
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<RealmResponseCache> results = realm.where(RealmResponseCache.class).findAll();
                    if (results != null) {
                        results.deleteAllFromRealm();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }
}
