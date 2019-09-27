package com.minchainx.base.clazz.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.minchainx.base.clazz.iview.IBaseView;
import com.minchainx.base.clazz.presenter.BasePresenter;

import rx.subscriptions.CompositeSubscription;

public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements IBaseView {

    protected Context mContext;
    protected P mPresenter;
    protected boolean mIsVisibleToUser;
    private boolean mIsViewInitialized;
    private boolean mIsDataInitialized;
    private boolean mIsLazyLoadEnabled = true;
    private CompositeSubscription mCompositeSubscription;

    /**
     * onViewCreated中在super.onViewCreated之后，正式加载布局和请求数据之前调用
     * @param view
     */
    protected abstract void onViewCreatedFirstLogic(View view);

    /**
     * onDestroyView中在super.onDestroyView之后，其他回收逻辑之前调用
     */
    protected abstract void onDestroyViewFirstLogic();

    /**
     * onDestroy中在super.onDetroy之后，其他回收逻辑之前调用
     */
    protected abstract void onDestroyFirstLogic();

    /**
     * 获取布局文件资源id
     */
    protected abstract int getLayoutResId();

    /**
     * 初始化控件
     */
    protected abstract void initializeView(View view);

    /**
     * 加载数据并更新UI
     *
     * @param hasRequestData 是否已请求数据    true直接使用数据更新UI；false先请求数据再使用数据更新UI
     */
    protected abstract void loadData(boolean hasRequestData);

    /**
     * 日志输出：info
     */
    public abstract void i(String text);

    /**
     * 日志输出：error
     */
    public abstract void e(String text);

    /**
     * 创建Presenter
     */
    protected P createPresenter() {
        return null;
    }

    /**
     * 获取Presenter
     *
     * @return
     */
    public P getPresenter() {
        if (mPresenter == null) {
            mPresenter = createPresenter();
        }
        return mPresenter;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        i("------------ onAttach ------------");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i("------------ onCreate ------------savedInstanceState=" + savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        i("------------ setUserVisibleHint ------------isVisibleToUser=" + isVisibleToUser);
        mIsVisibleToUser = isVisibleToUser;
        checkIfLoadData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        i("------------ onCreateView ------------savedInstanceState=" + savedInstanceState + ",container=" + container);
        return inflater.inflate(getLayoutResId(), container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        i("------------ onViewCreated ------------savedInstanceState=" + savedInstanceState);
        onViewCreatedFirstLogic(view);
        view.setTag(this.getClass());
        if (getPresenter() != null) {
            getPresenter().initializeData(this);
        }
        initializeView(view);
        if (!mIsLazyLoadEnabled) {
            //1.load data 2.bind data
            loadData(false);
        } else {
            mIsViewInitialized = true;
            if (savedInstanceState != null) {
                onRestoreInstanceState(savedInstanceState);
            }
            if (mIsDataInitialized) {
                //bind data
                loadData(true);
            } else {
                checkIfLoadData();
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        i("------------ onActivityCreated ------------savedInstanceState=" + savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        i("------------ onViewStateRestored ------------savedInstanceState=" + savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        i("------------ onStart ------------");
    }

    /**
     * 完成数据恢复之后调用方法 setIsDataInitialized(true) 才会直接使用恢复的数据，否则会重新请求数据
     *
     * @param savedInstanceState
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        i("------------ onRestoreInstanceState ------------savedInstanceState=" + savedInstanceState);
        if (getPresenter() != null) {
            getPresenter().onRestoreInstanceState(savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        i("------------ onResume ------------");
    }

    @Override
    public void onPause() {
        super.onPause();
        i("------------ onPause ------------");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        i("------------ onSaveInstanceState ------------outState=" + outState);
        if (getPresenter() != null) {
            getPresenter().onSaveInstanceState(outState);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        i("------------ onStop ------------");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        i("------------ onDestroyView ------------");
        onDestroyViewFirstLogic();
        mIsViewInitialized = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        i("------------ onDestroy ------------");
        onDestroyFirstLogic();
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed()) {
            mCompositeSubscription.unsubscribe();
        }
        if (getPresenter() != null) {
            getPresenter().onDestroy();
            mPresenter = null;
        }
        mContext = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        i("------------ onDetach ------------");
    }

    public void enableLazyLoad(boolean enable) {
        mIsLazyLoadEnabled = enable;
    }

    private void checkIfLoadData() {
        if (mIsVisibleToUser && mIsViewInitialized && !mIsDataInitialized) {
            mIsDataInitialized = true;
            //1.load data 2.bind data
            loadData(false);
        }
    }

    public void setIsDataInitialized(boolean isDataInitialized) {
        this.mIsDataInitialized = isDataInitialized;
    }

    @Override
    public void finishActivity() {
        if (mContext != null && mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }

    @Override
    public void showToast(int resId) {
        showToast(mContext.getResources().getString(resId));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getPresenter() != null) {
            getPresenter().onActivityResult(requestCode, resultCode, data);
        }
    }

    public CompositeSubscription getCompositeSubscription() {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        return mCompositeSubscription;
    }
}
