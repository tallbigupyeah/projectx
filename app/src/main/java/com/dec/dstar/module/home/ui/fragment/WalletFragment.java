package com.dec.dstar.module.home.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dec.dstar.R;
import com.dec.dstar.base.BaseFragment;
import com.dec.dstar.config.EventBusCode;
import com.dec.dstar.config.SuccessdEvent;
import com.dec.dstar.config.User;
import com.dec.dstar.module.home.iview.IWalletBalanceView;
import com.dec.dstar.module.home.model.GetBalanceModel;
import com.dec.dstar.module.home.presenter.WalletBalancePresenter;
import com.dec.dstar.module.wallet.ethereum.model.EthereumWalletModel;
import com.dec.dstar.module.wallet.ethereum.model.TokenStatusModel;
import com.dec.dstar.module.wallet.ethereum.ui.activity.SwitchWalletActivity;
import com.dec.dstar.module.wallet.ethereum.ui.activity.TokenListActivity;
import com.dec.dstar.module.wallet.ethereum.ui.activity.TransferRecordsActivity;
import com.dec.dstar.module.wallet.ethereum.ui.activity.WalletContactsActivity;
import com.dec.dstar.utils.CommonToast;
import com.dec.dstar.utils.Utils;
import com.dec.dstar.widget.DividerDecoration;
import com.dec.dstar.widget.recyclerview.CommonAdapter;
import com.dec.dstar.widget.recyclerview.CommonViewHolder;
import com.dec.dstar.widget.recyclerview.MultiItemTypeAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.web3j.utils.Convert;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;


/**
 * 作者：luoxiaohui
 * 日期:2018/7/26 16:22
 * 文件描述: 钱包Fragment
 */

public class WalletFragment extends BaseFragment<WalletBalancePresenter> implements IWalletBalanceView {


    @BindView(R.id.wallet_btn_choose)
    LinearLayout mBtnChoose;
    @BindView(R.id.walletName)
    TextView mTvName;
    @BindView(R.id.wallet_tv_address)
    TextView mTvAddress;
    @BindView(R.id.wallet_copy)
    ImageView mTvCopy;
    @BindView(R.id.wallet_recycleverview)
    RecyclerView mRecyclerview;
    @BindView(R.id.wallet_layout_wallet)
    RelativeLayout mLayoutWallet;
    @BindView(R.id.wallet_iv_add)
    ImageView mIvAdd;

    private Realm mRealm;
    private CommonAdapter commonAdapter;
    ArrayList<GetBalanceModel.Token> tokens;
    private boolean isNotEmpty;
    private ArrayList<GetBalanceModel.Token> originTokenList = new ArrayList<>();//原始的token列表

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_wallet;
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    @Override
    protected void initializeView(View view) {

        EventBus.getDefault().register(this);
        updateWallet();
        initRecyclerView();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isNotEmpty) {
            updateWallet();
        }
    }

    @OnClick({R.id.wallet_iv_add, R.id.wallet_btn_choose, R.id.wallet_copy, R.id.wallet_layout_wallet, R.id.wallet_bar_contacts, R.id.wallet_bar_scan})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.wallet_btn_choose:
                startActivity(new Intent(mContext, SwitchWalletActivity.class));
                break;
            case R.id.wallet_copy:

                Utils.copy2Clipboard(User.getInstance().getWalletAddress());
                CommonToast.showMessage(getString(R.string.copy2clipboard));
                break;
            case R.id.wallet_layout_wallet:


                break;
            case R.id.wallet_bar_contacts:
                startActivity(new Intent(mContext, WalletContactsActivity.class));
                break;
            case R.id.wallet_bar_scan:
                break;
            case R.id.wallet_iv_add:

                Intent tokenListIntent = new Intent(mContext, TokenListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("tokenList", tokens);
                bundle.putSerializable("originTokenList", originTokenList);
                tokenListIntent.putExtras(bundle);
                startActivity(tokenListIntent);
                break;
        }
    }

    /**
     * 钱包资产相关recyclerView
     */
    private void initRecyclerView() {

        tokens = new ArrayList<>();
        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerview.addItemDecoration(new DividerDecoration(mContext));
        commonAdapter = new CommonAdapter<GetBalanceModel.Token>(mContext, R.layout.item_walletfragment_token, tokens, new LinearLayoutHelper()) {
            @Override
            protected void convert(CommonViewHolder holder, GetBalanceModel.Token token, int position) {

                holder.setText(R.id.item_wallet_token_tv_count, TextUtils.isEmpty(tokens.get(position).getBalance()) ? "0.0000" : Convert.fromWei(tokens.get(position).getBalance(), Convert.Unit.ETHER).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString());
                holder.setText(R.id.item_wallet_token_tv_name, token.getSymbol());
            }
        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, final int position) {

                Intent intent = new Intent(mContext, TransferRecordsActivity.class);
                intent.putExtra("tokenName", tokens.get(position).getSymbol());
                intent.putExtra("tokenCount", TextUtils.isEmpty(tokens.get(position).getBalance()) ? "0.0000" : Convert.fromWei(tokens.get(position).getBalance(), Convert.Unit.ETHER).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString());
                startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mRecyclerview.setAdapter(commonAdapter);
    }

    @Override
    protected void onDestroyFirstLogic() {
        super.onDestroyFirstLogic();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(SuccessdEvent event) {
        if (EventBusCode.UPDATE_WALLET.equals(event.getCode())) {

            updateWallet();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getTokenStatusChange(ArrayList<TokenStatusModel> tokenStatusModels) {

        for (TokenStatusModel tokenStatusModel : tokenStatusModels) {
            if (tokenStatusModel.isShow()) {
                for (GetBalanceModel.Token token : originTokenList) {
                    if (tokenStatusModel.getSymbol().equalsIgnoreCase(token.getSymbol())) {

                        tokens.add(token);
                    }
                }
            } else {
                for (GetBalanceModel.Token token : originTokenList) {
                    if (tokenStatusModel.getSymbol().equalsIgnoreCase(token.getSymbol())) {

                        tokens.remove(token);
                    }
                }
            }
        }
        Collections.sort(tokens, new WalletBalancePresenter.MySortByValue());
        commonAdapter.notifyDataSetChanged();
    }

    /*
     * 更新钱包状态
     */
    private void updateWallet() {
        EthereumWalletModel model = getDefaultEthereumWalletModel();

        if (model != null) {
            isNotEmpty = true;
            User.getInstance().setWalletAddress(model.getWalletAddress());
            User.getInstance().setWalletName(model.getWalletName());
            mTvName.setText(model.getWalletName());
            mTvAddress.setText(model.getWalletAddress());

            mPresenter.getWalletBalance(User.getInstance().getWalletAddress());
        } else {
            isNotEmpty = false;
            mTvName.setText(R.string.wallet_name);
            mTvAddress.setText(R.string.wallet_address);
            tokens.clear();
            commonAdapter.notifyDataSetChanged();
        }
    }

    private EthereumWalletModel getDefaultEthereumWalletModel() {
        mRealm = Realm.getDefaultInstance();
        return mRealm.where(EthereumWalletModel.class)
                .equalTo("isDefault", true).findFirst();
    }

    @Override
    protected WalletBalancePresenter createPresenter() {
        return new WalletBalancePresenter(mContext, this, getActivity(), this);
    }

    @Override
    public void getWalletBalance(ArrayList<GetBalanceModel.Token> tokenList) {

        if (tokenList != null && tokenList.size() != 0) {

            tokens.clear();
            tokens.addAll(tokenList);
            commonAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 未处理的token列表
     *
     * @param tokens
     */
    @Override
    public void getOriginalTokenList(ArrayList<GetBalanceModel.Token> tokens) {

        originTokenList.clear();
        originTokenList.addAll(tokens);
    }

}
