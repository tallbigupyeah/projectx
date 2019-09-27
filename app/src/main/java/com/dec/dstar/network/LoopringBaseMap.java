package com.dec.dstar.network;

import java.util.HashMap;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/3 16:38
 * 文件描述: 一些公共请求参数放到这里
 */
public class LoopringBaseMap extends HashMap {

    public LoopringBaseMap(){
        put("id", 64);
        put("jsonrpc", "2.0");
    }
}
