package com.dec.dstar.network.response.loopring;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/3 09:40
 * 文件描述: 路印网络请求响应的基类
 */
public class BaseResponse<T> {

    /**
     * jsonrpc版本
     */
    public String jsonrpc;

    /**
     * id 不知道干嘛用的，数据请求成功id默认是64，否则没有id
     */
    public int id;

    /**
     * 数据响应成功
     */
    public T result;

    /**
     * 数据响应失败
     */
    public String error;
}
