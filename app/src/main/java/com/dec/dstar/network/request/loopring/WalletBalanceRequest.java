package com.dec.dstar.network.request.loopring;

import com.dec.dstar.config.EnvironmentConfig;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/4 17:09
 * 文件描述: 钱包代币余额请求
 */
public class WalletBalanceRequest {

    public String jsonrpc = "2.0";
    public String method = "loopring_getBalance";
    public int id = 64;
    public Param[] params = new Param[1];

    public WalletBalanceRequest(){
        params[0] = new Param();
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Param[] getParams() {
        return params;
    }

    public void setParams(Param[] params) {
        this.params = params;
    }

    public class Param{
        public String owner = "";
        public String delegateAddress = EnvironmentConfig.getInstance().getDelegateAddress();

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public String getDelegateAddress() {
            return delegateAddress;
        }

        public void setDelegateAddress(String delegateAddress) {
            this.delegateAddress = delegateAddress;
        }
    }
}
