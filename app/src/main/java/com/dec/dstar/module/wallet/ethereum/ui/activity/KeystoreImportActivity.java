package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.module.home.ui.activity.MainActivity;
import com.dec.dstar.module.wallet.ethereum.model.EthereumWalletModel;
import com.dec.dstar.utils.ActivityCollector;
import com.dec.dstar.utils.AppFilePath;
import com.dec.dstar.utils.DLog;
import com.dec.dstar.utils.EthereumWallet;
import com.dec.dstar.utils.FileUtils;
import com.dec.dstar.utils.ToastCustomUtils;
import com.dec.dstar.utils.WalletUtil;
import com.dec.dstar.widget.dialog.CompleteInfoDialog;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.ObjectMapperFactory;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.dec.dstar.utils.WalletUtil.createParentDir;

/**
 * 作者：gulincheng
 * 日期:2018/08/01 15:15
 * 文件描述: 通过keystore导入
 */
public class KeystoreImportActivity extends BaseActivity {


    private static final String TAG = "PriKeyImportActivity";

    @BindView(R.id.keystoreimport_btn_confirm)
    Button mBtnConfirm;
    @BindView(R.id.keystoreimport_et_keystore)
    EditText mEtWalletkeystore;
    @BindView(R.id.keystoreimport_et_walletpwd)
    EditText mEtWalletpwd;
    @BindView(R.id.keystoreimport_et_walletname)
    EditText mEtWalletname;
    @BindView(R.id.keystoreimport_cb)
    CheckBox mCb;

    static ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_keystoreimport;
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();

        mTitle.setText(getString(R.string.walletimportway_tip_keystore));

    }

    @OnClick(R.id.keystoreimport_btn_confirm)
    public void onViewClicked() {
        if (isLegal()) {

            new CompleteInfoDialog(this, new CompleteInfoDialog.CompleteInfoDialogListener() {
                @Override
                public void Cancel() {
                }

                @Override
                public void confirm() {
                    EthereumWallet ethereumWallet = importWallet();
                    if (ethereumWallet != null) {
                        DLog.d(TAG, "钱包地址-->" + ethereumWallet.getCredentials().getAddress());
                        DLog.d(TAG, "私钥-->" + ethereumWallet.getCredentials().getEcKeyPair()
                                .getPrivateKey());
                        DLog.d(TAG, "keystore-->" + ethereumWallet.getKeyStore());

                        Realm mRealm = Realm.getDefaultInstance();
                        try {
                            mRealm.beginTransaction();
                            RealmResults<EthereumWalletModel> models = mRealm.where
                                    (EthereumWalletModel.class)
                                    .equalTo("isDefault", true).findAll();

                            EthereumWalletModel ethereumWalletModel = mRealm.createObject
                                    (EthereumWalletModel.class); // Create a new object
                            ethereumWalletModel.setKeystore(ethereumWallet.getKeyStore());
                            ethereumWalletModel.setWalletAddress(ethereumWallet.getCredentials()
                                    .getAddress());
                            ethereumWalletModel.setPrivateKey(ethereumWallet.getCredentials()
                                    .getEcKeyPair().getPrivateKey().toString());
                            ethereumWalletModel.setWalletName(mEtWalletname.getText().toString()
                                    .trim());
                            ethereumWalletModel.setDefault(models.size() == 0 ? true : false);
                            mRealm.commitTransaction();

                        } finally {
                            mRealm.close();
                        }
                        ToastCustomUtils.showLong(R.string.walletcreate_success);
                        startActivity(new Intent(mContext, MainActivity.class));
                        ActivityCollector.getInstance().finishAllExcept(MainActivity.class);
                        //导入私钥钱包,无需提示用户备份,本就来自用户的备份
                        finish();
                    }
                }
            }).show();

        }
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
        if (!mCb.isChecked()) {
            ToastCustomUtils.showLong(R.string.walletcreate_tip_agreement);
            return false;
        }
        return true;
    }

    /**
     * 导入钱包  Keystore
     */
    public EthereumWallet importWallet() {

        String walletName = mEtWalletname.getText().toString().trim();
        File destination = new File(AppFilePath.Wallet_DIR, walletName + ".tmp");

        if (destination.exists()) {
            ToastCustomUtils.showLong(R.string.wallet_namesake);
            return null;
        }

        //目录不存在则创建目录，创建不了则报错
        if (!createParentDir(destination)) {
            return null;
        }
        String password = mEtWalletpwd.getText().toString().trim();
        String keystore = mEtWalletkeystore.getText().toString().trim();
        FileUtils.writeFileFromString(destination, keystore, false);
        Credentials credentials = null;

        if (!password.equals("") && !keystore.equals("")) {
           credentials = WalletUtil.getCredentials(walletName, password);
        } else {
            Toast.makeText(this, "请输入密码和keystore", Toast.LENGTH_SHORT).show();
        }
        if (null == credentials) {
            ToastCustomUtils.showLong(R.string.keystore_import_notvalid);
            return null;
        } else {
            EthereumWallet ethereumWallet = new EthereumWallet();
            ethereumWallet.setKeyStore(keystore);
            ethereumWallet.setCredentials(credentials);
            return ethereumWallet;
        }
    }
}
