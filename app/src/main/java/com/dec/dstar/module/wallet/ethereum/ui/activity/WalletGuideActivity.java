package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.content.Intent;
import android.view.View;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/23 13:37
 * 文件描述: 钱包导航页面(创建钱包/导入钱包)
 */
public class WalletGuideActivity extends BaseActivity {

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_walletguide;
    }

    @Override
    protected void initializeView() {
        super.initializeView();

        findViewById(R.id.walletguide_btn_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, WalletCreateActivity.class));
            }
        });
        findViewById(R.id.walletguide_btn_import).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, WalletImportWayActivity.class));
            }
        });
    }
}
