package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.config.User;
import com.dec.dstar.module.home.ui.activity.MainActivity;
import com.dec.dstar.module.wallet.ethereum.model.EthereumWalletModel;
import com.dec.dstar.utils.ActivityCollector;
import com.dec.dstar.utils.AppFilePath;
import com.dec.dstar.utils.AppUtils;
import com.dec.dstar.utils.DLog;
import com.dec.dstar.utils.EthereumWallet;
import com.dec.dstar.utils.ToastCustomUtils;
import com.dec.dstar.widget.dialog.CompleteInfoDialog;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.ObjectMapperFactory;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.dec.dstar.utils.WalletUtil.createParentDir;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/23 15:15
 * 文件描述: 通过私钥导入
 */
public class PriKeyImportActivity extends BaseActivity {


    private static final String TAG = "PriKeyImportActivity";

    @BindView(R.id.prikeyimport_btn_confirm)
    Button mBtnConfirm;
    @BindView(R.id.prikeyimport_et_prikey)
    EditText mEtWalletprikey;
    @BindView(R.id.prikeyimport_et_walletname)
    EditText mEtWalletname;
    @BindView(R.id.prikeyimport_et_walletpwd)
    EditText mEtWalletpwd;
    @BindView(R.id.prikeyimport_et_walletpwd_repeat)
    EditText mEtWalletpwdRepeat;
    @BindView(R.id.prikeyimport_cb)
    CheckBox mCb;

    static ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_prikeyimport;
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();

        mTitle.setText(getString(R.string.walletimportway_tip_prikey));
        if (AppUtils.isAppDebug(this)) {
            mEtWalletname.setText("test");
            mEtWalletpwd.setText("test123AA");
            mEtWalletpwdRepeat.setText("test123AA");
            mCb.setChecked(true);
        }
    }

    @OnClick(R.id.prikeyimport_btn_confirm)
    public void onViewClicked() {
        if (isLegal()) {

            new CompleteInfoDialog(this, new CompleteInfoDialog.CompleteInfoDialogListener() {
                @Override
                public void Cancel() {
                }

                @Override
                public void confirm() {
                    EthereumWallet ethereumWallet =  importWallet2();
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
                            if(models.size() == 0){
                                User.getInstance().setWalletAddress(ethereumWallet.getCredentials().getAddress());
                                User.getInstance().setWalletName(mEtWalletname.getText().toString().trim());
                            }
                        } finally {
                            mRealm.close();
                        }
                        ToastCustomUtils.showLong(R.string.walletcreate_success);
                        //导入私钥钱包,无需提示用户备份,本就来自用户的备份
                        startActivity(new Intent(mContext, MainActivity.class));
                        ActivityCollector.getInstance().finishAllExcept(MainActivity.class);
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
        String privkey = mEtWalletprikey.getText().toString().trim();
        if(privkey.isEmpty() || !WalletUtils.isValidPrivateKey(privkey)) {
            ToastCustomUtils.showLong(R.string.privimport_tip_notvalid);
            return false;
        }
        if (mEtWalletname.getText().toString().trim().isEmpty()) {
            ToastCustomUtils.showLong(R.string.walletcreate_tip_walletname);
            return false;
        }
        if (mEtWalletpwd.getText().toString().trim().isEmpty()) {
            ToastCustomUtils.showLong(R.string.walletcreate_tip_walletpwd);
            return false;
        }
        if (mEtWalletpwd.getText().toString().trim().length() < 8 ) {
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

    /**
     * 导入钱包  私钥
     */
    public EthereumWallet  importWallet2() {
        File destination = new File(AppFilePath.Wallet_DIR, mEtWalletname.getText().toString()
                .trim() + ".tmp");

        if (destination.exists()) {
            ToastCustomUtils.showLong(R.string.wallet_namesake);
            return null;
        }
        //目录不存在则创建目录，创建不了则报错
        if (!createParentDir(destination)) {
            return null;
        }

        String password = mEtWalletpwd.getText().toString().trim();
        String privateKey = mEtWalletprikey.getText().toString().trim();
        if (!password.equals("") && !privateKey.equals("")) {

            ECKeyPair ecKeyPair = null;
            WalletFile walletFile = null;
            try {
                ecKeyPair = ECKeyPair.create(Numeric.toBigInt(privateKey));
                walletFile = Wallet.create(password, ecKeyPair, 16, 1);
                String address = walletFile.getAddress();

            } catch (Exception e) {
                e.printStackTrace();
            }

            EthereumWallet ethereumWallet = new EthereumWallet();
            try {
                /**
                 * 保存钱包到本地
                 */
                objectMapper.writeValue(destination, walletFile);
                String keyStore = objectMapper.writeValueAsString(walletFile);
                ethereumWallet.setKeyStore(keyStore);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            ethereumWallet.setCredentials(Credentials.create(ecKeyPair));
            return ethereumWallet;

        } else {
            Toast.makeText(this, "请输入密码和privateKey", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

}
