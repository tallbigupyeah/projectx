package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.config.User;
import com.dec.dstar.module.wallet.ethereum.model.EthereumWalletModel;
import com.dec.dstar.utils.AppUtils;
import com.dec.dstar.utils.DLog;
import com.dec.dstar.utils.EthereumWallet;
import com.dec.dstar.utils.StringUtils;
import com.dec.dstar.utils.ToastCustomUtils;
import com.dec.dstar.utils.WalletUtil;
import com.dec.dstar.widget.dialog.CompleteInfoDialog;

import java.math.BigInteger;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/24 13:39
 * 文件描述: 创建钱包页面
 */
public class WalletCreateActivity extends BaseActivity {

    private static final String TAG = "WalletCreateActivity";
    @BindView(R.id.walletcreate_btn_create)
    Button mBtnCreate;
    @BindView(R.id.walletcreate_et_walletname)
    EditText mEtWalletname;
    @BindView(R.id.walletcreate_et_walletpwd)
    EditText mEtWalletpwd;
    @BindView(R.id.walletcreate_et_walletpwd_repeat)
    EditText mEtWalletpwdRepeat;
    @BindView(R.id.walletcreate_cb)
    CheckBox mCb;


    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_walletcreate;
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();

        mTitle.setText(getString(R.string.createwallet));
        if (AppUtils.isAppDebug(this)) {
            mEtWalletname.setText("test");
            mEtWalletpwd.setText("test123AA");
            mEtWalletpwdRepeat.setText("test123AA");
            mCb.setChecked(true);
        }
    }


    @OnClick(R.id.walletcreate_btn_create)
    public void onViewClicked() {

        if (isLegal()) {

            new CompleteInfoDialog(this, new CompleteInfoDialog.CompleteInfoDialogListener() {
                @Override
                public void Cancel() {
                }

                @Override
                public void confirm() {

                    EthereumWallet ethereumWallet = WalletUtil.createWallet(mEtWalletname.getText().toString().trim(), mEtWalletpwd.getText().toString().trim());
                    if (ethereumWallet != null) {

                        DLog.d(TAG, "钱包地址-->" + ethereumWallet.getCredentials().getAddress());
                        DLog.d(TAG, "私钥-->" + ethereumWallet.getCredentials().getEcKeyPair().getPrivateKey());
                        DLog.d(TAG, "keystore-->" + ethereumWallet.getKeyStore());

                        Realm mRealm = Realm.getDefaultInstance();
                        try {
                            mRealm.beginTransaction();
                            RealmResults<EthereumWalletModel> models = mRealm.where(EthereumWalletModel.class)
                                    .equalTo("isDefault", true).findAll();

                            EthereumWalletModel ethereumWalletModel = mRealm.createObject(EthereumWalletModel.class); // Create a new object
                            ethereumWalletModel.setKeystore(ethereumWallet.getKeyStore());
                            ethereumWalletModel.setWalletAddress(ethereumWallet.getCredentials().getAddress());
                            ethereumWalletModel.setPrivateKey(ethereumWallet.getCredentials().getEcKeyPair().getPrivateKey().toString());
                            ethereumWalletModel.setWalletName(mEtWalletname.getText().toString().trim());
                            ethereumWalletModel.setDefault(models.size() == 0 ? true : false);
                            mRealm.commitTransaction();

                            //如果之前没有钱包，这时候设置钱包地址和名称为当前的
                            if (models.size() == 0) {
                                User.getInstance().setWalletAddress(ethereumWallet.getCredentials().getAddress());
                                User.getInstance().setWalletName(mEtWalletname.getText().toString().trim());
                            }
                        } finally {
                            mRealm.close();
                        }
                        ToastCustomUtils.showLong(R.string.walletcreate_success);
                        Intent intent = new Intent(mContext, BackupPriKeyActivity.class);
                        intent.putExtra("privateKey", new BigInteger(ethereumWallet.getCredentials().getEcKeyPair().getPrivateKey().toString(), 10).toString(16));
                        startActivity(intent);
                        mContext.finishActivity();
                    }
                }
            }).show();
        }


    }

    @Override
    protected void initializeView() {
        super.initializeView();


    }

    /*
     * 判断参数属否合法
     */
    private boolean isLegal() {

        if (mEtWalletname.getText().toString().trim().isEmpty()) {

            ToastCustomUtils.showLong(R.string.walletcreate_tip_walletname);
            return false;
        }
        if (mEtWalletpwd.getText().toString().trim().isEmpty()) {
            ToastCustomUtils.showLong(R.string.walletcreate_tip_walletpwd);
            return false;
        }
        if (mEtWalletpwd.getText().toString().trim().length() < 8 || !StringUtils.checkPassword(mEtWalletpwd.getText().toString().trim())) {
            ToastCustomUtils.showLong(R.string.wallet_pwd_hint);
            return false;
        }
        if (!mEtWalletpwd.getText().toString().trim().equals(mEtWalletpwdRepeat.getText().toString().trim())) {
            ToastCustomUtils.showLong(R.string.walletcreate_tip_compare);
            return false;
        }
        if (!mCb.isChecked()) {
            ToastCustomUtils.showLong(R.string.walletcreate_tip_agreement);
            return false;
        }
        return true;
    }
}










