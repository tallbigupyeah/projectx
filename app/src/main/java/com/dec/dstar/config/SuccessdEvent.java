package com.dec.dstar.config;

/**
 * 作者：luoxiaohui
 * 日期:2017/5/22 20:46
 * 文件描述: 成功之后的事件处理
 */
public class SuccessdEvent {

    private String code;
    private int what;

    public SuccessdEvent(String code) {//事件传递参数
        this.code = code;
    }

    public SuccessdEvent(String code, int what) {//事件传递参数,添加重载方法
        this.code = code;
        this.what = what;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public String getCode() {//取出事件参数
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}