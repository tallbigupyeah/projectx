package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.module.home.ui.activity.MainActivity;
import com.dec.dstar.utils.ActivityCollector;
import com.dec.dstar.utils.ToastCustomUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 作者：gulincheng
 * 日期:2018/08/25 14:55
 * 文件描述:
 */
public class BackupKeystoreActivity extends BaseActivity {
    @BindView(R.id.backupkeystore_tv_prikey)
    TextView mTvPrikey;
    @BindView(R.id.backupkeystore_tv_copy)
    TextView mTvCopy;
    @BindView(R.id.backupkeystore_btn_finish)
    Button mBtnFinish;

    //剪切板Data对象
    private ClipData mClipData;
    //剪切板管理工具类
    private ClipboardManager mClipboardManager;
    private String privateKey = "";

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_backupkeystore;
    }

    @Override
    protected void initializeView() {
        super.initializeView();

        mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        privateKey = getIntent().getStringExtra("privateKey");
        mTvPrikey.setText(privateKey);

    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();

        mTitle.setText(getString(R.string.backupkeystore_title));
    }

    @OnClick({R.id.backupkeystore_tv_copy, R.id.backupkeystore_btn_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.backupkeystore_tv_copy:

                //创建一个新的文本clip对象
                mClipData = ClipData.newPlainText("privateKey", privateKey);
                //把clip对象放在剪贴板中
                mClipboardManager.setPrimaryClip(mClipData);
                ToastCustomUtils.showLong(getString(R.string.copy_success));
                break;
            case R.id.backupkeystore_btn_finish:

                startActivity(new Intent(this, MainActivity.class));
                ActivityCollector.getInstance().finishAllExcept(MainActivity.class);
                finish();
                break;
        }
    }
}
