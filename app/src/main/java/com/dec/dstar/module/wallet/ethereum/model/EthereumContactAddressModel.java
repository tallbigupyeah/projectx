package com.dec.dstar.module.wallet.ethereum.model;

import io.realm.RealmObject;

/**
 * 作者：gulincheng
 * 日期:2018/08/04 15:15
 * 文件描述: 以太坊地址表操作类
 */

public class EthereumContactAddressModel extends RealmObject {

    private String contactAddress;//联系人地址
    private String contactName;//联系人名称
    private boolean isImportant  = false;//是否星标联系人

    public String getContactAddress() {
        return contactAddress;
    }

    public String getContactName() {
        return contactName;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public void setContactAddress(String contactAddress) {
        this.contactAddress = contactAddress;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }
}
