package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dec.dstar.BuildConfig;
import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.config.Constant;
import com.dec.dstar.module.wallet.ethereum.model.EthereumWalletModel;
import com.dec.dstar.utils.DLog;
import com.dec.dstar.utils.EthereumWallet;
import com.dec.dstar.utils.ToastCustomUtils;
import com.dec.dstar.utils.WalletUtil;
import com.dec.dstar.widget.dialog.CompleteInfoDialog;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * 作者：gulincheng
 * 日期:2018/08/12 16:04
 * 文件描述:
 */
public class PasswordChangeActivity extends BaseActivity {

    @BindView(R.id.pwdchange_bt_save)
    Button mBtnConfirm;
    @BindView(R.id.pwdchange_et_walletpwd_old)
    EditText mEtPwdOld;
    @BindView(R.id.pwdchange_et_walletpwd_new)
    EditText mEtPwdNew;
    @BindView(R.id.pwdchange_et_walletpwd_repeat)
    EditText mEtPwdRepeat;


    EthereumWalletModel mEthereumWalletModel;
    private String mWalletName;
    private final Realm mRealm = Realm.getDefaultInstance();
    private static final String TAG = "PasswordChangeActivity";

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_pwdchange;
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();
        mTitle.setText(R.string.wallet_pwd_change_title);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) {
            mEtPwdOld.setText("test123AA");
            mEtPwdNew.setText("test123AA");
            mEtPwdRepeat.setText("test123AA");
        }
        mWalletName = getIntent().getStringExtra(Constant.WALLET_NAME_STRING);
        mEthereumWalletModel = mRealm.where(EthereumWalletModel.class)
                .equalTo("walletName", mWalletName).findFirst();
    }

    @OnClick(R.id.pwdchange_bt_save)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.pwdchange_bt_save:
                if (isLegal()) {

                    new CompleteInfoDialog(this, new CompleteInfoDialog
                            .CompleteInfoDialogListener() {

                        @Override
                        public void Cancel() {
                        }

                        @Override
                        public void confirm() {
                            EthereumWallet ethereumWallet = WalletUtil.reCreateWallet(mWalletName,
                                    mEtPwdNew.getText().toString().trim());

                            if (ethereumWallet != null && mEthereumWalletModel != null) {
                                DLog.d(TAG, "钱包地址-->" + ethereumWallet.getCredentials().getAddress());
                                DLog.d(TAG, "私钥-->" + ethereumWallet.getCredentials().getEcKeyPair()
                                        .getPrivateKey());
                                DLog.d(TAG, "keystore-->" + ethereumWallet.getKeyStore());
                                Realm mRealm = Realm.getDefaultInstance();
                                try {
                                    mRealm.beginTransaction();
                                    mEthereumWalletModel.setKeystore(ethereumWallet.getKeyStore());
                                    mRealm.commitTransaction();
                                } finally {
                                    mRealm.close();
                                }
                                ToastCustomUtils.showLong(R.string.wallet_pwd_set_done);
                                finish();
                            }
                        }
                    }).show();
                }
                break;
        }
    }

    private boolean isLegal() {
        if (mEtPwdOld.getText().toString().trim().isEmpty()) {
            ToastCustomUtils.showLong(R.string.walletcreate_tip_walletpwd);
            return false;
        }
        if (WalletUtil.getCredentials(mWalletName, mEtPwdOld.getText().toString().trim()) == null) {
            ToastCustomUtils.showLong(R.string.wallet_pwd_wrong);
            return false;
        }
        if (mEtPwdNew.getText().toString().trim().isEmpty()) {
            ToastCustomUtils.showLong(R.string.walletcreate_tip_walletpwd);
            return false;
        }
        if (mEtPwdNew.getText().toString().trim().length() < 8) {
            ToastCustomUtils.showLong(R.string.wallet_pwd_hint);
            return false;
        }
        if (!mEtPwdNew.getText().toString().trim().equals(mEtPwdRepeat.getText().toString().trim
                ())) {
            ToastCustomUtils.showLong(R.string.walletcreate_tip_compare);
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }
}
