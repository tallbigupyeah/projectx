package com.dec.dstar.network.request.loopring;

import com.dec.dstar.config.EnvironmentConfig;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/19 16:29
 * 文件描述:
 */
public class TransferRecordsRequest {

    public String jsonrpc = "2.0";
    public String method = "loopring_getTransactions";
    public int id = 64;
    public Param[] params = new Param[1];

    public TransferRecordsRequest(){
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
//        public String thxHash = "";
        public String symbol = "";
        public String status = ""; //pengding, success, fail
        public String txType = "";//receive, enable, convert
        public int pageIndex = 1;
        public int pageSize = 20;

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

//        public String getThxHash() {
//            return thxHash;
//        }
//
//        public void setThxHash(String thxHash) {
//            this.thxHash = thxHash;
//        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTxType() {
            return txType;
        }

        public void setTxType(String txType) {
            this.txType = txType;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }
    }
}




















