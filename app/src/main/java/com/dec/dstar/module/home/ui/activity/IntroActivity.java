package com.dec.dstar.module.home.ui.activity;

import android.content.Intent;

import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.module.wallet.ethereum.model.EthereumWalletModel;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 作者：gulincheng
 * 日期:2018/08/08 16:37
 * 文件描述:引导页面
 */
public class IntroActivity extends BaseActivity {
    @Override
    protected int getLayoutResId() {
        return 0;
    }


    @Override
    protected void onCreateFirstLogic() {
        Realm mRealm = Realm.getDefaultInstance();
        RealmResults<EthereumWalletModel> ethereumWalletModels = mRealm.where(EthereumWalletModel.class).findAll();

        if (ethereumWalletModels == null || ethereumWalletModels.size() == 0) {
            startActivity(new Intent(mContext, WalletIntroGuideActivity.class));
        } else {
            startActivity(new Intent(mContext, MainActivity.class));
        }
        finish();
    }
}
