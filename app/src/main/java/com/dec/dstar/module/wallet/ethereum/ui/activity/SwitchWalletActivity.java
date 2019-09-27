package com.dec.dstar.module.wallet.ethereum.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dec.dstar.R;
import com.dec.dstar.base.BaseActivity;
import com.dec.dstar.config.Constant;
import com.dec.dstar.config.EventBusCode;
import com.dec.dstar.config.SuccessdEvent;
import com.dec.dstar.module.wallet.ethereum.model.EthereumWalletModel;
import com.dec.dstar.widget.DividerDecoration;
import com.dec.dstar.widget.recyclerview.CommonAdapter;
import com.dec.dstar.widget.recyclerview.CommonViewHolder;
import com.dec.dstar.widget.recyclerview.MultiItemTypeAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/30 13:42
 * 文件描述: 切换以太坊钱包
 */
public class SwitchWalletActivity extends BaseActivity {

    @BindView(R.id.switchwallet_recyclerview)
    RecyclerView mRecyclerview;
    @BindView(R.id.switchwallet_iv_add)
    ImageView mIvAdd;

    private List<EthereumWalletModel> list = null;
    private CommonAdapter<EthereumWalletModel> commonAdapter;

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_switchwallet;
    }

    @Override
    protected void initializeTitleBar() {
        super.initializeTitleBar();

        mTitle.setText(R.string.swith_wallet);
    }

    @Override
    protected void initializeView() {
        super.initializeView();

        EventBus.getDefault().register(this);
        initRecyclerView();
    }

    /*
     * 从realm数据库中读取钱包数据
     */
    private void initRecyclerView() {

        final Realm mRealm = Realm.getDefaultInstance();
        list = mRealm.where(EthereumWalletModel.class).findAll();
        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerview.addItemDecoration(new DividerDecoration(mContext));
        commonAdapter = new CommonAdapter<EthereumWalletModel>(mContext, R.layout.item_switchwallet, list, new LinearLayoutHelper()) {
            @Override
            protected void convert(CommonViewHolder holder, final EthereumWalletModel model, int position) {

                holder.setText(R.id.item_switchwallet_tv_walletname, model.getWalletName());
                holder.setImageResource(R.id.item_switchwallet_iv, model.isDefault() == true
                        ? R.drawable.switchwallet_selected : R.drawable.switchwallet_unselected);
                holder.getView(R.id.item_switchwallet_dot_iv).setOnClickListener(new View
                        .OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.item_switchwallet_dot_iv:
                                Intent intent = new Intent(mContext, WalletManagerActivity.class);
                                intent.putExtra(Constant.WALLET_NAME_STRING, model.getWalletName());
                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                    }
                });
            }
        };

        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder,final int position) {

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        //先查找后得到User对象
                        EthereumWalletModel previousModel = mRealm.where(EthereumWalletModel.class).equalTo("isDefault", true).findFirst();
                        if(previousModel != null) {
                            previousModel.setDefault(false);
                        }

                        EthereumWalletModel model = list.get(position);
                        model.setDefault(true);
                    }
                });
                commonAdapter.notifyDataSetChanged();
                EventBus.getDefault().post(new SuccessdEvent(EventBusCode.UPDATE_WALLET));
                mContext.finishActivity();
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mRecyclerview.setAdapter(commonAdapter);
    }

    @Subscribe
    public void onEvent(SuccessdEvent event) {
        if (EventBusCode.UPDATE_WALLET.equals(event.getCode())) {
            updateWallet();
        }
    }

    @Override
    protected void onDestroyFirstLogic() {
        super.onDestroyFirstLogic();
        EventBus.getDefault().unregister(this);
    }

    private void updateWallet() {
        mRecyclerview.getAdapter().notifyDataSetChanged();
    }

    @OnClick(R.id.switchwallet_iv_add)
    public void onViewClicked() {
        startActivity(new Intent(mContext, WalletGuideActivity.class));
    }
}
