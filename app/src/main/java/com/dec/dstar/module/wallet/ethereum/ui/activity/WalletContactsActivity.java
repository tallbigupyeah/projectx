package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.config.EventBusCode;
import com.dec.dstar.config.SuccessdEvent;
import com.dec.dstar.module.wallet.ethereum.model.EthereumContactAddressModel;
import com.dec.dstar.widget.recyclerview.CommonAdapter;
import com.dec.dstar.widget.recyclerview.CommonViewHolder;
import com.dec.dstar.widget.recyclerview.MultiItemTypeAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * 作者：gulincheng
 * 日期:2018/08/04 15:15
 * 文件描述: 转账地址管理界面
 */

public class WalletContactsActivity extends BaseActivity {

    @BindView(R.id.wallet_contacts_recycleverview)
    RecyclerView mRecyclerview;

    private List<EthereumContactAddressModel> list = null;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_contacts;
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();
        mTitle.setText(R.string.wallet_contacts_title);
    }

    @Override
    protected void initializeView() {
        super.initializeView();

        initRecyclerView();
    }

    /*
     * 从realm数据库中读取钱包数据
     */
    private void initRecyclerView() {

        final Realm mRealm = Realm.getDefaultInstance();
        list = mRealm.where(EthereumContactAddressModel.class).findAll();
        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        final CommonAdapter commonAdapter = new CommonAdapter<EthereumContactAddressModel>(mContext, R.layout.item_address_item, list, new LinearLayoutHelper()) {
            @Override
            protected void convert(CommonViewHolder holder, EthereumContactAddressModel model, int position) {

                holder.setText(R.id.item_address_tv_walletname, model.getContactName());
//                holder.setImageResource(R.id.item_switchwallet_iv, model.isDefault() == true ? R.drawable.switchwallet_selected : R.drawable.switchwallet_unselected );
            }
        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, final int position) {
                commonAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mRecyclerview.setAdapter(commonAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();

        final Realm mRealm = Realm.getDefaultInstance();
        list = mRealm.where(EthereumContactAddressModel.class).findAll();
        CommonAdapter m = (CommonAdapter<EthereumContactAddressModel>)mRecyclerview.getAdapter();
        m.setDatas(list);
        m.notifyDataSetChanged();
    }

    @OnClick(R.id.fl_menu)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_menu:
                startActivity(new Intent(mContext, AddContactsActivity.class));
                break;

        }
    }
}
