package com.dec.dstar.module.wallet.ethereum.ui.activity;


import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.base.IBaseView;
import com.dec.dstar.config.Constant;
import com.dec.dstar.config.EventBusCode;
import com.dec.dstar.config.SuccessdEvent;
import com.dec.dstar.module.wallet.ethereum.model.EthereumWalletModel;
import com.dec.dstar.utils.AppFilePath;
import com.dec.dstar.utils.AppUtils;
import com.dec.dstar.utils.FileUtils;
import com.dec.dstar.utils.WalletUtil;
import com.dec.dstar.widget.dialog.PasswordDialog;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minchainx.base.clazz.presenter.BasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.ObjectMapperFactory;

import java.io.File;
import java.nio.charset.Charset;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * 作者：gulincheng
 * 日期:2018/08/09 19:41
 * 文件描述:
 */
public class WalletManagerActivity extends BaseActivity implements IBaseView {


    @BindView(R.id.walletmanage_tv_address)
    TextView mTvAddress;
    @BindView(R.id.walletmanage_et_name)
    EditText mEtName;
    @BindView(R.id.walletmanage_rl_pwd)
    RelativeLayout mRlPwd;
    @BindView(R.id.walletmanage_rl_keystore_backup)
    RelativeLayout mRlKeystoreBackup;
    @BindView(R.id.walletmanage_rl_pri_backup)
    RelativeLayout mRlPrikeyBackup;
    @BindView(R.id.walletmanage_rl_memorywords_backup)
    RelativeLayout mRlMemorywordsBackup;

    EthereumWalletModel mEthereumWalletModel;
    private String mWalletName;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_managewallet;
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();
        mTitle.setText(R.string.wallet_manage);
        mMenuText.setText(R.string.save);
    }

    @Override
    protected void onCreateFirstLogic() {
        super.onCreateFirstLogic();
        mWalletName = getIntent().getStringExtra(Constant.WALLET_NAME_STRING);
        final Realm mRealm = Realm.getDefaultInstance();
        mEthereumWalletModel = mRealm.where(EthereumWalletModel.class)
                .equalTo("walletName", mWalletName).findFirst();

    }

    @Override
    protected void initializeView() {
        super.initializeView();
        mTvAddress.setText(mEthereumWalletModel.getWalletAddress());
        mEtName.setText(mEthereumWalletModel.getWalletName());
    }

    @Override
    protected BasePresenter createPresenter() {
        return new BasePresenter(mContext, this, this, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Realm mRealm = Realm.getDefaultInstance();
        mEthereumWalletModel = mRealm.where(EthereumWalletModel.class)
                .equalTo("walletName", mWalletName).findFirst();
    }

    @OnClick({R.id.walletmanage_rl_pwd, R.id.walletmanage_rl_keystore_backup,
            R.id.walletmanage_bt_delete, R.id.tv_menu, R.id.walletmanage_rl_pri_backup,
            R.id.walletmanage_rl_memorywords_backup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.walletmanage_rl_pwd:
                Intent intent = new Intent(mContext, PasswordChangeActivity.class);
                intent.putExtra(Constant.WALLET_NAME_STRING, mWalletName);
                startActivity(intent);
                break;
            case R.id.walletmanage_bt_delete:
                new PasswordDialog(this, new PasswordDialog.PasswordDialogListener() {
                    @Override
                    public void confirm(final String password) {
                        if (AppUtils.isAppDebug(mContext)) {
                            handleDelete("test123AA");
                        } else {
                            handleDelete(password);
                        }
                    }
                }).show();
                break;
            case R.id.tv_menu:
                Realm mRealm = Realm.getDefaultInstance();
                try {
                    mRealm.beginTransaction();
                    File source = new File(AppFilePath.Wallet_DIR, mEthereumWalletModel
                            .getWalletName() + ".tmp");
                    FileUtils.rename(source, mEtName.getText()
                            .toString().trim() + ".tmp");
                    mEthereumWalletModel.setWalletName(mEtName.getText().toString().trim());
                    mRealm.commitTransaction();
                } finally {
                    mRealm.close();
                }
                EventBus.getDefault().post(new SuccessdEvent(EventBusCode.UPDATE_WALLET));
                finish();
                break;
            case R.id.walletmanage_rl_keystore_backup:
                new PasswordDialog(this, new PasswordDialog.PasswordDialogListener() {
                    @Override
                    public void confirm(final String password) {
                        if (AppUtils.isAppDebug(mContext)) {
                            hanndleKeystoreBackup("test123AA");
                        } else {
                            hanndleKeystoreBackup(password);
                        }
                    }
                }).show();
                break;
            case R.id.walletmanage_rl_pri_backup:
                new PasswordDialog(this, new PasswordDialog.PasswordDialogListener() {
                    @Override
                    public void confirm(final String password) {
                        if (AppUtils.isAppDebug(mContext)) {
                            hanndlePrivkeyBackup("test123AA");
                        } else {
                            hanndlePrivkeyBackup(password);
                        }
                    }
                }).show();
                break;
            case R.id.walletmanage_rl_memorywords_backup:
                break;
            default:
                break;
        }
    }

    private void hanndlePrivkeyBackup(String password) {
        Intent intent = new Intent(mContext, BackupPriKeyActivity.class);
        intent.putExtra("privateKey", WalletUtil.
                getCredentials(mEthereumWalletModel.getWalletName(), password)
                .toString());
        startActivity(intent);
    }

    private void hanndleKeystoreBackup(String password) {
        File source = new File(AppFilePath.Wallet_DIR, mEthereumWalletModel
                .getWalletName() + ".tmp");
        String keyStore = FileUtils.readFile2String(source, Charset.defaultCharset().displayName());
        Intent intent = new Intent(mContext, BackupKeystoreActivity.class);
        intent.putExtra("keystore", keyStore);
        startActivity(intent);
    }
    static ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    private void handleDelete(String password) {
        //导出钱包
        Credentials credentials = WalletUtil.getCredentials(mEthereumWalletModel.getWalletName(),
                password);
        if (credentials == null) {
            return;
        }
        File destination = new File(AppFilePath.Wallet_DIR, mEthereumWalletModel.getWalletName()
                + ".tmp");
        destination.delete();
        Realm mRealm = Realm.getDefaultInstance();
        try {
            mRealm.beginTransaction();
            mEthereumWalletModel.deleteFromRealm();
            EthereumWalletModel mDefaultEthereumWalletModel =
                    mRealm.where(EthereumWalletModel.class).findFirst();
            if (mDefaultEthereumWalletModel != null) {
                mDefaultEthereumWalletModel.setDefault(true);
            }
            mRealm.commitTransaction();
        } finally {
            mRealm.close();
        }

        EventBus.getDefault().post(new SuccessdEvent(EventBusCode.UPDATE_WALLET));
        finish();
    }

}
