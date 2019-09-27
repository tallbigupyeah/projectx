package com.dec.dstar.module.wallet.ethereum.model;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/28 18:52
 * 文件描述: 在代币列表更改状态后，将此实体list返回到WalletFragment
 */
public final class TokenStatusModel {

    private String symbol;
    private boolean isShow;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }
}
