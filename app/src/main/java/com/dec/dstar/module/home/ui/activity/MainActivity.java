package com.dec.dstar.module.home.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.module.home.ui.fragment.MarketFragment;
import com.dec.dstar.module.home.ui.fragment.MyFragmemt;
import com.dec.dstar.module.home.ui.fragment.WalletFragment;
import com.dec.dstar.module.wallet.ethereum.model.EthereumWalletModel;
import com.dec.dstar.module.wallet.ethereum.ui.activity.WalletGuideActivity;
import com.dec.dstar.utils.CommonToast;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/15 19:11
 * 文件描述: 主activity
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.rb_market)
    RadioButton mBtnMarket;
    @BindView(R.id.rb_wallet)
    RadioButton mBtnWallet;
    @BindView(R.id.rb_my)
    RadioButton mBtnMy;

    private int lastIndex;
    private Fragment[] fragments;

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreateFirstLogic() {
        super.onCreateFirstLogic();

        Realm mRealm = Realm.getDefaultInstance();
        RealmResults<EthereumWalletModel> ethereumWalletModels = mRealm.where(EthereumWalletModel.class).findAll();

        if (ethereumWalletModels == null || ethereumWalletModels.size() == 0) {
            startActivity(new Intent(mContext, WalletIntroGuideActivity.class));
            finish();
        }
    }

    @Override
    protected void initializeView() {
        super.initializeView();

        MarketFragment marketFragment = new MarketFragment();
        WalletFragment walletFragment = new WalletFragment();
        MyFragmemt myFragment = new MyFragmemt();
        fragments = new Fragment[]{marketFragment ,walletFragment ,myFragment};
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_home_content, marketFragment).commitAllowingStateLoss();
    }

    public void switchFragment(int currentIndex) {
        if (fragments[currentIndex].isAdded()) {
            getSupportFragmentManager().beginTransaction()
                    .hide(fragments[lastIndex])
                    .show(fragments[currentIndex])
                    .commitAllowingStateLoss();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .hide(fragments[lastIndex])
                    .add(R.id.fl_home_content, fragments[currentIndex])
                    .commitAllowingStateLoss();
        }
        if (currentIndex == 0) {

            mBtnMarket.setChecked(true);
        } else if(currentIndex == 1){

            mBtnWallet.setChecked(true);
        } else if(currentIndex == 2){

            mBtnMy.setChecked(true);
        }
        lastIndex = currentIndex;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick({R.id.rb_market, R.id.rb_wallet, R.id.rb_my})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rb_market:

                switchFragment(0);
                break;
            case R.id.rb_wallet:

                switchFragment(1);
                break;
            case R.id.rb_my:

                switchFragment(2);
                break;
        }
    }

    private long firstTime = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        long secondTime = System.currentTimeMillis();
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ( secondTime - firstTime < 2000) {
                finish();
            } else {
                CommonToast.showMessage("再按一次退出程序");
                firstTime = System.currentTimeMillis();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
