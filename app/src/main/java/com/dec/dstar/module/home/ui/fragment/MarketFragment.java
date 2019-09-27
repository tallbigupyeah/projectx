package com.dec.dstar.module.home.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dec.dstar.R;
import com.dec.dstar.base.BaseFragment;
import com.dec.dstar.module.home.iview.IMarketView;
import com.dec.dstar.module.home.model.TickerListResponse;
import com.dec.dstar.module.home.presenter.MarketPresenter;
import com.dec.dstar.module.wallet.ethereum.ui.activity.BuyActivity;
import com.dec.dstar.widget.DividerDecoration;
import com.dec.dstar.widget.recyclerview.CommonAdapter;
import com.dec.dstar.widget.recyclerview.CommonViewHolder;
import com.dec.dstar.widget.recyclerview.MultiItemTypeAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 作者：luoxiaohui
 * 日期:2018/7/29 16:10
 * 文件描述: 行情fragment
 */
public class MarketFragment extends BaseFragment<MarketPresenter> implements IMarketView {

    @BindView(R.id.market_tv_choose)
    TextView marketTvChoose;
    @BindView(R.id.market_tv_all)
    TextView marketTvAll;
    @BindView(R.id.market_rv)
    RecyclerView mRecyclerview;
    private CommonAdapter commonAdapter;
    private List<TickerListResponse.Ticker> list = new ArrayList<>();

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_market;
    }

    /**
     * 初始化控件
     *
     * @param view
     */
    @Override
    protected void initializeView(View view) {

        initRecyclerView();
        mPresenter.getTickerList();
    }

    @Override
    protected MarketPresenter createPresenter() {
        return new MarketPresenter(mContext, this, null, this);
    }

    private void initRecyclerView() {

        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerview.addItemDecoration(new DividerDecoration(mContext));
        commonAdapter = new CommonAdapter<TickerListResponse.Ticker>(mContext, R.layout.item_market, list, new LinearLayoutHelper()) {
            @Override
            protected void convert(CommonViewHolder holder, TickerListResponse.Ticker model, int position) {

                holder.setText(R.id.market_tv_price, model.getAmount() + "");
                holder.setText(R.id.market_tv_tokenname, model.getMarket());
                holder.setText(R.id.market_tv_rate, TextUtils.isEmpty(model.getChange()) ? "0.00%" : model.getChange() + "%");

            }
        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, final int position) {

                Intent intent = new Intent(mContext, BuyActivity.class);
                intent.putExtra("market", list.get(position).getMarket());
                mContext.startActivity(intent);
            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mRecyclerview.setAdapter(commonAdapter);
    }

    @OnClick({R.id.market_tv_choose, R.id.market_tv_all})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.market_tv_choose:

                switchMarket(1);
                break;
            case R.id.market_tv_all:

                switchMarket(2);
                break;
        }
    }

    /**
     * 切换自选和全部
     */
    private void switchMarket(int type) {

        if (type == 1) {

            marketTvChoose.setBackgroundColor(getResources().getColor(R.color.theme_color));
            marketTvChoose.setTextColor(getResources().getColor(R.color.white));
            marketTvAll.setBackgroundColor(getResources().getColor(R.color.white));
            marketTvAll.setTextColor(getResources().getColor(R.color.black));
        } else if (type == 2) {

            marketTvChoose.setBackgroundColor(getResources().getColor(R.color.white));
            marketTvChoose.setTextColor(getResources().getColor(R.color.black));
            marketTvAll.setBackgroundColor(getResources().getColor(R.color.theme_color));
            marketTvAll.setTextColor(getResources().getColor(R.color.white));
        }
    }


    /**
     * 交易对列表
     *
     * @param list
     */
    @Override
    public void getTickerList(List<TickerListResponse.Ticker> list) {

        this.list.clear();
        this.list.addAll(list);
        commonAdapter.notifyDataSetChanged();
    }
}
