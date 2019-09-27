package com.minchainx.base.clazz.iview;

public interface IBaseView {

    /**
     * 显示进度提示
     */
    void showProgressBar();

    /**
     * 隐藏进度提示
     */
    void hideProgressBar();

    /**
     * 关闭当前Activity
     */
    void finishActivity();

    /**
     * 显示Toast
     *
     * @param text
     */
    void showToast(String text);

    /**
     * 显示Toast
     *
     * @param resId
     */
    void showToast(int resId);
}
