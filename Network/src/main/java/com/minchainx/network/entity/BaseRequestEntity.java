package com.minchainx.network.entity;

import java.io.Serializable;

public class BaseRequestEntity implements Serializable {

    private String appId;
    private String timestamp;
    private String noncestr;
    private String signature;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return ", appId='" + appId + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", noncestr='" + noncestr + '\'' +
                ", signature='" + signature + '\'';
    }
}
