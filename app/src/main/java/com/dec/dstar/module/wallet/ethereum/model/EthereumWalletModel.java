package com.dec.dstar.module.wallet.ethereum.model;

import io.realm.RealmObject;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/26 13:43
 * 文件描述: 以太坊钱包表操作类
 */
public class EthereumWalletModel extends RealmObject {

    private String walletAddress;//钱包地址
    private String walletName;//钱包名称
    private String privateKey;//钱包私钥
    private String keystore;//钱包的keystore
    private boolean isDefault = false;//是否为默认钱包地址

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getKeystore() {
        return keystore;
    }

    public void setKeystore(String keystore) {
        this.keystore = keystore;
    }
}
