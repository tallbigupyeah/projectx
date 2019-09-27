package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.module.home.model.GetBalanceModel;
import com.dec.dstar.module.wallet.ethereum.iview.ITokenListView;
import com.dec.dstar.module.wallet.ethereum.model.SupportTokens;
import com.dec.dstar.module.wallet.ethereum.model.TokenStatusModel;
import com.dec.dstar.module.wallet.ethereum.presenter.TokenListPresenter;
import com.dec.dstar.widget.DividerDecoration;
import com.dec.dstar.widget.recyclerview.CommonAdapter;
import com.dec.dstar.widget.recyclerview.CommonViewHolder;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/27 18:26
 * 文件描述: 添加代币列表页面
 */
public class TokenListActivity extends BaseActivity<TokenListPresenter> implements ITokenListView {

    @BindView(R.id.tokenlist_rv)
    RecyclerView mRecyclerview;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    private List<GetBalanceModel.Token> tokenList = new ArrayList<>();//从WalletFragment传过来的显示了的token
    private List<GetBalanceModel.Token> originTokenList = new ArrayList<>();//从WalletFragment传过来的全部的token
    private ArrayList<SupportTokens.Token> originalTokenList = new ArrayList<>();//本页面网络请求获取的token列表
    private CommonAdapter commonAdapter;
    private ArrayList<TokenStatusModel> tokenStatusModels = new ArrayList<>();//状态更改后的token列表

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_tokenlist;
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();
        mTitle.setText("代币列表");

    }

    @Override
    protected TokenListPresenter createPresenter() {
        return new TokenListPresenter(mContext, this, this, null);
    }

    @Override
    protected void initializeView() {
        super.initializeView();
        tokenList = (List<GetBalanceModel.Token>) getIntent().getSerializableExtra("tokenList");
        originTokenList = (List<GetBalanceModel.Token>) getIntent().getSerializableExtra("originTokenList");

        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerview.addItemDecoration(new DividerDecoration(mContext));
        commonAdapter = new CommonAdapter<SupportTokens.Token>(mContext, R.layout.item_tokenlist, originalTokenList, new LinearLayoutHelper()) {
            @Override
            protected void convert(CommonViewHolder holder, final SupportTokens.Token model, int position) {

                holder.setText(R.id.item_tokenlist_tv_contractaddr, model.getProtocol());
                holder.setText(R.id.item_tokenlist_tv_token, model.getSymbol());
                holder.setText(R.id.item_tokenlist_tv_source, model.getSource());
                ((SwitchCompat) holder.getView(R.id.item_tokenlist_switch)).setChecked(false);
                for (GetBalanceModel.Token token : tokenList) {
                    if (token.getSymbol().equalsIgnoreCase(model.getSymbol())) {
                        ((SwitchCompat) holder.getView(R.id.item_tokenlist_switch)).setChecked(true);
                    }
                }
                ((SwitchCompat) holder.getView(R.id.item_tokenlist_switch)).setOnCheckedChangeListener((buttonView, isChecked) -> {

                    for (GetBalanceModel.Token token : originTokenList) {
                        if (token.getSymbol().equalsIgnoreCase(model.getSymbol())) {
                            for (int i = 0; i < tokenStatusModels.size(); i++) {
                                if (tokenStatusModels.get(i).getSymbol().equalsIgnoreCase(model.getSymbol())) {
                                    tokenStatusModels.get(i).setShow(isChecked);
                                    break;
                                }
                            }
                            TokenStatusModel tokenStatusModel = new TokenStatusModel();
                            tokenStatusModel.setShow(isChecked);
                            tokenStatusModel.setSymbol(model.getSymbol());
                            tokenStatusModels.add(tokenStatusModel);
                        }
                    }
                });
            }
        };
        mRecyclerview.setAdapter(commonAdapter);
        mPresenter.getTokenList();
    }

    /**
     * 获取token列表
     *
     * @param list
     */
    @Override
    public void getTokenList(List<SupportTokens.Token> list) {

        originalTokenList.clear();
        originalTokenList.addAll(list);
        Collections.sort(originalTokenList, new SortByValue());
        commonAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {

        if (tokenStatusModels.size() != 0)
            EventBus.getDefault().post(tokenStatusModels);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (tokenStatusModels.size() != 0)
                EventBus.getDefault().post(tokenStatusModels);
        }
        return super.onKeyDown(keyCode, event);
    }

    private class SortByValue implements Comparator {

        public int compare(Object o1, Object o2) {

            SupportTokens.Token token1 = (SupportTokens.Token) o1;
            SupportTokens.Token token2 = (SupportTokens.Token) o2;
            if (token1.getSymbol().compareToIgnoreCase(token2.getSymbol()) < 0) {
                return -1;
            }
            return 1;
        }
    }
}
