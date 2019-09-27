package com.dec.dstar.module.helpcenter;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/29 17:50
 * 文件描述: 帮助中心
 */
public class HelpCenterActivity extends BaseActivity {

    @BindView(R.id.my_layout_companyintro)
    RelativeLayout mLayoutCompanyintro;
    @BindView(R.id.my_layout_agreement)
    RelativeLayout mLayoutAgreement;
    @BindView(R.id.my_layout_useintruction)
    RelativeLayout mLayoutUseintruction;
    @BindView(R.id.my_layout_feedback)
    RelativeLayout mLayoutFeedback;
    @BindView(R.id.my_layout_contactus)
    RelativeLayout mLayoutContactus;

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_helpcenter;
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();

        mTitle.setText(R.string.my_helpcenter);
    }

    @OnClick({R.id.my_layout_companyintro, R.id.my_layout_agreement, R.id.my_layout_useintruction, R.id.my_layout_feedback, R.id.my_layout_contactus})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.my_layout_companyintro:

                break;
            case R.id.my_layout_agreement:

                break;
            case R.id.my_layout_useintruction:
                break;
            case R.id.my_layout_feedback:
                break;
            case R.id.my_layout_contactus:
                break;
        }
    }
}
