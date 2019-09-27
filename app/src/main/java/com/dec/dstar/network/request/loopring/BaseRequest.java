package com.dec.dstar.network.request.loopring;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/12 14:59
 * 文件描述:
 */
public class BaseRequest {

    public String jsonrpc = "2.0";
    public String method = "loopring_getSupportedTokens";
    public int id = 64;
    public Param[] params = new Param[1];

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

    }
}
