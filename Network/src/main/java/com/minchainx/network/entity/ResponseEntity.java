package com.minchainx.network.entity;

import com.minchainx.network.cache.CacheManager;
import com.minchainx.networklite.util.GsonUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ResponseEntity<T> implements Serializable {

    /**
     * 默认
     */
    public static final int FLAG_NONE = 0;
    /**
     * 成功
     */
    public static final int FLAG_SUCCESS = 1;
    /**
     * 登录超时
     */
    public static final int FLAG_SESSION_TIMEOUT = 2;

    /**
     * 请求成功
     */
    public static final String ERROR_CODE_SUCCESS = "0000000";

    public int flag;
    public String msg;
    public String errorCode;
    public String errorMessage;
    public T data;
    /**
     * 时间戳，从网络获取的时间点
     */
    public long timestamp;
    /**
     * 数据渠道（内存/本地/网络）
     */
    public int dataChannel = CacheManager.DATA_CHANNEL_NETWORK;

    @Override
    public String toString() {
        return "ResponseEntity[dataChannel=" + dataChannel + ",timestamp=" + timestamp
                + ",\nflag=" + flag + ",msg=" + msg + ",errorCode=" + errorCode
                + ",errorMessage=" + errorMessage + ",\ndata=" + data + "]";
    }

    public String toJsonString() {
        try {
            return GsonUtils.getGsonInstance().toJson(this);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public ResponseEntity copy() {
        ResponseEntity copyResponseEntity = null;
        if (this instanceof Serializable) {
            try {
                ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(byteOut);
                out.writeObject(this);

                ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
                ObjectInputStream in = new ObjectInputStream(byteIn);
                copyResponseEntity = (ResponseEntity) in.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return copyResponseEntity;
    }
}
