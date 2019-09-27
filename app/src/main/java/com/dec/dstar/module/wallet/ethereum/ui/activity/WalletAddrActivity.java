package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.config.User;
import com.dec.dstar.utils.CommonToast;
import com.dec.dstar.utils.Utils;
import com.dec.dstar.utils.ZXingUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/18 10:32
 * 文件描述: 钱包地址页面
 */
public class WalletAddrActivity extends BaseActivity {

    @BindView(R.id.walletaddr_tv_addr)
    TextView mTvAddr;
    @BindView(R.id.walletaddr_btn)
    TextView mBtn;
    @BindView(R.id.walletaddr_iv_qrcode)
    ImageView mIvQrcode;

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_walletaddr;
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();
        mTitle.setText("钱包地址");
    }

    @Override
    protected void initializeView() {
        super.initializeView();
        mTvAddr.setText(User.getInstance().getWalletAddress());

        mIvQrcode.post(() -> {

            mIvQrcode.setImageBitmap(ZXingUtils.createQRImage(User.getInstance().getWalletAddress(), mIvQrcode.getWidth(), mIvQrcode.getHeight()));
        });
    }

    @OnClick(R.id.walletaddr_btn)
    public void onViewClicked() {

        Utils.copy2Clipboard(User.getInstance().getWalletAddress());
        CommonToast.showMessage(getString(R.string.copy2clipboard));
    }

    @Override
    protected void onDestroy() {
        mIvQrcode.setImageBitmap(null);
        super.onDestroy();
    }
}
