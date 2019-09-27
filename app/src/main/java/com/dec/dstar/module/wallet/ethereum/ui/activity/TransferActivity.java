package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.config.User;
import com.dec.dstar.module.wallet.ethereum.iview.ITransferView;
import com.dec.dstar.module.wallet.ethereum.model.EthTransferModel;
import com.dec.dstar.module.wallet.ethereum.model.SupportTokens;
import com.dec.dstar.module.wallet.ethereum.presenter.TransferPresenter;
import com.dec.dstar.network.loader.LoopringApiLoader;
import com.dec.dstar.network.request.loopring.NotifyTransactionSubmitedRequest;
import com.dec.dstar.network.subscriber.ProgressDialogSubscriber;
import com.dec.dstar.utils.AsyncUtils;
import com.dec.dstar.utils.CommonToast;
import com.dec.dstar.utils.DLog;
import com.dec.dstar.utils.ToastCustomUtils;
import com.dec.dstar.utils.ethereum.TransactionManager;
import com.dec.dstar.widget.dialog.PasswordDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import jnr.ffi.Struct;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/30 15:36
 * 文件描述: 以太坊钱包转账
 */
public class TransferActivity extends BaseActivity<TransferPresenter> implements ITransferView {

    @BindView(R.id.transfer_tv_tokenname)
    TextView mTvTokenname;
    @BindView(R.id.transfer_tv_balance)
    TextView mTvBalance;
    @BindView(R.id.transfer_et_amount)
    EditText mEtAmount;
    @BindView(R.id.transfer_et_note)
    EditText mEtNote;
    @BindView(R.id.transfer_et_address)
    EditText mEtAddress;
    @BindView(R.id.transfer_et_payaddress)
    EditText mEtPayaddress;
    @BindView(R.id.transfer_seekbarW)
    SeekBar mSeekbar;
    @BindView(R.id.transfer_tv_pether)
    TextView mTvPether;
    @BindView(R.id.transfer_layout_calculate)
    LinearLayout mLayoutCalculate;
    @BindView(R.id.transfer_layout_consumption)
    LinearLayout mLayoutConsumption;
    @BindView(R.id.transfer_iv_arrow)
    ImageView mIvArrow;
    @BindView(R.id.transfer_btn_next)
    Button mBtnNext;

    private String contractAddr = "";//合约地址
    private String tokenName;
    private String tokenCount;
    private BigDecimal ethConsumption = new BigDecimal("0.0001"); //转账需要消耗的eth
    private boolean isShowcalculateLayout = false;//是否显示滑块

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_transfer;
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();

        mTitle.setText(tokenName + getString(R.string.transfer_transfer));
    }

    @Override
    protected void initializeView() {
        super.initializeView();

        tokenName = getIntent().getStringExtra("tokenName");
        tokenCount = getIntent().getStringExtra("tokenCount");
        mTvBalance.setText(getString(R.string.balance) + ": " + tokenCount + " " + tokenName);
        mTvTokenname.setText(tokenName);
        mEtPayaddress.setText(User.getInstance().getWalletAddress());
        initSeekbar();

        //当代币不是eth时，才需要获取代币合约地址
        if (!tokenName.equalsIgnoreCase("ETH")) {

            mPresenter.getContractAddressList(tokenName);
        }
    }

    private void initSeekbar() {

        mSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                calculateConsumption(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 滑动滑块，换算需要消耗的eth
     */
    private void calculateConsumption(int progress) {

        if (progress == 0) {
            progress = 1;
        }
        BigDecimal decimal = new BigDecimal(progress * 0.000012);
        ethConsumption = decimal.setScale(8, BigDecimal.ROUND_HALF_DOWN);
        mTvPether.setText(ethConsumption + "ether");
    }

    @OnClick({R.id.transfer_layout_consumption, R.id.transfer_btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.transfer_layout_consumption:

                if (isShowcalculateLayout) {

                    isShowcalculateLayout = false;
                    mLayoutCalculate.setVisibility(View.GONE);
                    mIvArrow.setBackgroundResource(R.drawable.arrow_right);
                } else {

                    isShowcalculateLayout = true;
                    mLayoutCalculate.setVisibility(View.VISIBLE);
                    mIvArrow.setBackgroundResource(R.drawable.arrow_down);
                }
                break;
            case R.id.transfer_btn_next:

                new PasswordDialog(this, new PasswordDialog.PasswordDialogListener() {
                    @Override
                    public void confirm(final String password) {

                        handleTransfer(password);

                    }
                }).show();

                break;
        }
    }

    /**
     * 区分以太坊和代币交易
     *
     * @return response
     */
    private void handleTransfer(final String password) {
        new AsyncUtils(new AsyncUtils.AsyncUtilsListener<NotifyTransactionSubmitedRequest>() {
            @Override
            public NotifyTransactionSubmitedRequest subThreadOperate() {

                NotifyTransactionSubmitedRequest response = null;
                if (tokenName.equalsIgnoreCase("ETH")) {
                    response = TransactionManager.getInstance().transferEth(User.getInstance().getWalletAddress(),
                            mEtAddress.getText().toString().trim(),
                            String.valueOf(ethConsumption),
                            mEtAmount.getText().toString().trim(),
                            User.getInstance().getWalletName(),
                            password);

                } else {

                    response = TransactionManager.getInstance().transferOtherCurrency(contractAddr,
                            User.getInstance().getWalletAddress(),
                            mEtAddress.getText().toString().trim(),
                            String.valueOf(ethConsumption),
                            mEtAmount.getText().toString().trim(),
                            User.getInstance().getWalletName(),
                            password);
                }

                return response;
            }

            @Override
            public void mainThreadCallback(NotifyTransactionSubmitedRequest request) {

                if (request == null){

                    CommonToast.showMessage(getString(R.string.network_invalid));
                }
                mPresenter.notifyTransactionSubmitted(request);
            }
        });
    }

    @Override
    protected TransferPresenter createPresenter() {
        return new TransferPresenter(mContext, this, this, null);
    }

    /**
     * 转账成功
     */
    @Override
    public void transferSuccess() {

        ToastCustomUtils.showLong(R.string.transfer_success);
        finish();
    }

    /**
     * 获取代币合约地址列表
     */
    @Override
    public void getContractAddr(String contractAddr) {

        if ("".equals(contractAddr)) {

            CommonToast.showMessage(getString(R.string.transfer_not_support));
        } else {

            this.contractAddr = contractAddr;
        }
    }
}































