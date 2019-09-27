package com.dec.dstar.config;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/5 16:33
 * 文件描述: 用户单例类
 */
public class User {

    private static User INSTANCE = null;
    private String walletAddress;//当前钱包地址
    private String walletName;//当前钱包名称

    public static User getInstance(){
        if (INSTANCE == null){
            INSTANCE = new User();
        }
        return INSTANCE;
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
}
