package com.dec.dstar.module.home.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseFragment;
import com.dec.dstar.module.helpcenter.HelpCenterActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * 作者：luoxiaohui
 * 日期:2018/7/29 16:10
 * 文件描述: 我的fragment
 */
public class MyFragmemt extends BaseFragment {

    @BindView(R.id.my_tv_account)
    TextView mTvAccount;
    @BindView(R.id.my_layout_helpcenter)
    RelativeLayout mLayoutHelpcenter;
    @BindView(R.id.my_layout_setting)
    RelativeLayout mLayoutSetting;

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_my;
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    @Override
    protected void initializeView(View view) {

    }


    @OnClick({R.id.my_layout_helpcenter, R.id.my_layout_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.my_layout_helpcenter:

                startActivity(new Intent(mContext, HelpCenterActivity.class));
                break;
            case R.id.my_layout_setting:
                break;
        }
    }
}
