package com.dec.dstar.module.wallet.ethereum.ui.activity;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;

/**
 * 作者：luoxiaohui
 * 日期:2018/9/22 17:02
 * 文件描述: 路印买入交易
 */
public class BuyActivity extends BaseActivity {

    private String market = null;

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_buy;
    }

    @Override
    protected void initializeView() {
        super.initializeView();

        market = getIntent().getStringExtra("market");
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();
        mTitle.setText("买入");
    }
}
