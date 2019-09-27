package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.content.Intent;
import android.view.View;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/23 14:06
 * 文件描述: 钱包导入方式
 */
public class WalletImportWayActivity extends BaseActivity {

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_walletimportway;
    }

    @Override
    protected void initializeView() {
        super.initializeView();

        findViewById(R.id.walletimportway_btn_keystore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, KeystoreImportActivity.class));
            }
        });
        findViewById(R.id.walletimportway_btn_prikey).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, PriKeyImportActivity.class));
            }
        });
        findViewById(R.id.walletimportway_btn_mnemonic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();

        mTitle.setText(getString(R.string.wallet_import));
    }
}
