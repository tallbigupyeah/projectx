package com.dec.dstar.base;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dec.dstar.utils.CommonToast;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<P extends BasePresenter>  extends com.minchainx.base.clazz.fragment.BaseFragment<P> implements IBaseView{

    Unbinder unbinder;

    /**
     * onViewCreated中在super.onViewCreated之后，正式加载布局和请求数据之前调用
     *
     * @param view
     */
    @Override
    protected void onViewCreatedFirstLogic(View view) {

    }

    /**
     * onDestroyView中在super.onDestroyView之后，其他回收逻辑之前调用
     */
    @Override
    protected void onDestroyViewFirstLogic() {

    }

    /**
     * onDestroy中在super.onDetroy之后，其他回收逻辑之前调用
     */
    @Override
    protected void onDestroyFirstLogic() {

    }

    /**
     * 加载数据并更新UI
     *
     * @param hasRequestData 是否已请求数据    true直接使用数据更新UI；false先请求数据再使用数据更新UI
     */
    @Override
    protected void loadData(boolean hasRequestData) {

    }

    /**
     * 日志输出：info
     *
     * @param text
     */
    @Override
    public void i(String text) {

    }

    /**
     * 日志输出：error
     *
     * @param text
     */
    @Override
    public void e(String text) {

    }

    /**
     * 显示进度提示
     */
    @Override
    public void showProgressBar() {

    }

    /**
     * 隐藏进度提示
     */
    @Override
    public void hideProgressBar() {

    }

    /**
     * 显示Toast
     *
     * @param text
     */
    @Override
    public void showToast(String text) {

    }

    @Override
    public void showErrorToast(String msg) {
        CommonToast.showErrorMessage(msg);
    }

    @Override
    public void isProgress(boolean isProgress) {
        BaseActivity activity = (BaseActivity) getActivity();
        activity.isProgress(isProgress);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(getLayoutResId(), container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
