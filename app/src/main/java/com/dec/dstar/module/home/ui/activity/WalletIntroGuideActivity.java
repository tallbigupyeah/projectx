package com.dec.dstar.module.home.ui.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.module.wallet.ethereum.ui.activity.WalletCreateActivity;
import com.dec.dstar.module.wallet.ethereum.ui.activity.WalletImportWayActivity;

/**
 * 作者：gulincheng
 * 日期:2018/08/08 20:48
 * 文件描述:
 */
public class WalletIntroGuideActivity extends BaseActivity {
    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_wallet_intro_guide;
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}

