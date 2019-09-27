package com.dec.dstar.utils;

import org.web3j.crypto.Credentials;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/26 13:22
 * 文件描述: 以太坊钱包
 */
public class EthereumWallet {

    private Credentials credentials;
    private String keyStore;

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }
}
