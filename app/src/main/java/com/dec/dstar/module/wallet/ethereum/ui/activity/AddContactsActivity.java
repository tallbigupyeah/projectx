package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.module.wallet.ethereum.model.EthereumContactAddressModel;
import com.dec.dstar.utils.ToastCustomUtils;
import com.dec.dstar.widget.dialog.CompleteInfoDialog;

import org.web3j.crypto.WalletUtils;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * 作者：gulincheng
 * 日期:2018/08/04 15:15
 * 文件描述: 添加转账地址
 */
public class AddContactsActivity extends BaseActivity {
    @BindView(R.id.et_add_contact_name)
    EditText mEtAddContactName;
    @BindView(R.id.et_add_contact_address)
    EditText mEtAddContactAddress;
    @BindView(R.id.wallet_btn_contact_save)
    Button mBtnContactSave;

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_add_contacts;
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();
        mTitle.setText(R.string.wallet_add_contacts_title);
    }

    @OnClick(R.id.wallet_btn_contact_save)
    public void onViewClicked(View view) {
        if (isLegal()) {

            new CompleteInfoDialog(this, new CompleteInfoDialog.CompleteInfoDialogListener() {
                @Override
                public void Cancel() {
                }

                @Override
                public void confirm() {
                    Realm mRealm = Realm.getDefaultInstance();
                    try {
                        mRealm.beginTransaction();

                        EthereumContactAddressModel contactAddressModel = mRealm.createObject(EthereumContactAddressModel.class); // Create a new object
                        contactAddressModel.setContactAddress(mEtAddContactAddress.getText().toString().trim());
                        contactAddressModel.setContactName(mEtAddContactName.getText().toString().trim());
                        mRealm.commitTransaction();
                    } finally {
                        mRealm.close();
                    }
                    ToastCustomUtils.showLong(R.string.walletcreate_success);
                    mContext.finishActivity();

                }
            }).show();
        }

    }

    /*
     * 判断参数属否合法
     */
    private boolean isLegal() {
        if (mEtAddContactName.getText().toString().trim().isEmpty()) {
            ToastCustomUtils.showLong(R.string.wallet_address_notvalid);
            return false;
        }
        if (mEtAddContactAddress.getText().toString().trim().isEmpty()
                || !WalletUtils.isValidAddress(mEtAddContactAddress.getText().toString().trim())) {
            ToastCustomUtils.showLong(R.string.wallet_address_notvalid);
            return false;
        }
        return true;
    }
}
