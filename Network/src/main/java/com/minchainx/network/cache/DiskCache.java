package com.minchainx.network.cache;

import android.content.Context;

import com.minchainx.network.BaseApiManager;
import com.minchainx.network.entity.RequestEntity;
import com.minchainx.network.entity.ResponseEntity;
import com.minchainx.network.utils.LogUtils;
import com.minchainx.network.utils.NetworkUtils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DiskCache implements ICache {

    private static final String NAME_SUFFIX = ".db";

    private Context mContext;
    private File mFileDir;

    public DiskCache(Context context) {
        this.mContext = context;
        this.mFileDir = context.getFilesDir();
    }

    @Override
    public <T> Observable<T> get(final RequestEntity requestEntity) {
        return Observable
                .create(new Observable.OnSubscribe<T>() {
                    @Override
                    public void call(Subscriber<? super T> subscriber) {
                        T t = (T) getDiskData(getFileName(requestEntity.getKey()));
                        if (subscriber.isUnsubscribed()) {
                            return;
                        }
                        if (t != null && !requestEntity.isHaveMemoryCache()) {
                            subscriber.onNext(t);
                            if (t instanceof ResponseEntity) {
                                long timestamp = ((ResponseEntity) t).timestamp;
                                requestEntity.setMemoryCacheTimestamp(timestamp);
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
                        boolean result = saveData(getFileName(key), t);
                        if (!subscriber.isUnsubscribed() && result) {
                            subscriber.onNext(t);
                        }
                        subscriber.onCompleted();
                    }
                })
                .compose(BaseApiManager.<T>applySchedulers())
                .subscribe();
    }

    @Override
    public void clear(String key) {
        File file = new File(mFileDir, getFileName(key));
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public void clearAll() {
        if (mFileDir != null) {
            File[] files = mFileDir.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    if (file != null && file.exists()) {
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * 保存数据
     */
    private <T> boolean saveData(String fileName, T t) {
        File file = new File(mFileDir, fileName);
        ObjectOutputStream objectOut = null;
        boolean isSuccess = false;
        try {
            FileOutputStream out = new FileOutputStream(file);
            objectOut = new ObjectOutputStream(out);
            objectOut.writeObject(t);
            objectOut.flush();
            isSuccess = true;
        } catch (IOException e) {
            LogUtils.e("写入缓存错误" + e.getMessage());
        } catch (Exception e) {
            LogUtils.e("写入缓存错误" + e.getMessage());
        } finally {
            closeSilently(objectOut);
        }
        return isSuccess;
    }

    /**
     * 获取保存的数据
     */
    private Object getDiskData(String fileName) {
        File file = new File(mFileDir, fileName);
        if (!file.exists()) {
            return null;
        }

//        if (isCacheDataFailure(file)) {
//            return null;
//        }

        Object o = null;
        ObjectInputStream read = null;
        try {
            read = new ObjectInputStream(new FileInputStream(file));
            o = read.readObject();
        } catch (StreamCorruptedException e) {
            LogUtils.e("读取错误" + e.getMessage());
        } catch (IOException e) {
            LogUtils.e("读取错误" + e.getMessage());
        } catch (ClassNotFoundException e) {
            LogUtils.e("错误" + e.getMessage());
        } finally {
            closeSilently(read);
        }
        return o;
    }

    private void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 判断缓存是否已经失效
     */
    private boolean isCacheDataFailure(File dataFile) {
        if (!dataFile.exists()) {
            return true;
        }
        long existTime = System.currentTimeMillis() - dataFile.lastModified();
        boolean failure = false;
        if (!NetworkUtils.isNetworkConnected(mContext)) {
            return failure;
        }
        failure = existTime > CacheManager.getInstance(mContext).getCacheTime() ? true : false;
        return failure;
    }

    private String getFileName(String key) {
        return new Md5Helper().getMD5String(key) + NAME_SUFFIX;
    }

    private static class Md5Helper {

        private final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        private MessageDigest messageDigest = null;

        public Md5Helper() {
            try {
                messageDigest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }

        public String getMD5String(String str) {
            return getMD5String(str.getBytes());
        }

        public String getMD5String(byte[] bytes) {
            if (messageDigest == null) {
                return new String(bytes);
            }
            messageDigest.update(bytes);
            return bytesToHex(messageDigest.digest());
        }

        public String bytesToHex(byte bytes[]) {
            return bytesToHex(bytes, 0, bytes.length);
        }

        public String bytesToHex(byte bytes[], int start, int len) {
            StringBuilder sb = new StringBuilder();
            for (int i = start; i < start + len; i++) {
                sb.append(byteToHex(bytes[i]));
            }
            return sb.toString();
        }

        public String byteToHex(byte bt) {
            return HEX_DIGITS[(bt & 0xf0) >> 4] + "" + HEX_DIGITS[bt & 0xf];
        }
    }
}
