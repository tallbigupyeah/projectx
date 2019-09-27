package com.dec.dstar.base;


/**
 * 作者：luoxiaohui
 * 日期:2018/8/2 19:45
 * 文件描述: baseview基类
 */
public interface IBaseView extends com.minchainx.base.clazz.iview.IBaseView {

    void showErrorToast(String msg);

    void isProgress(boolean isProgress);
}
