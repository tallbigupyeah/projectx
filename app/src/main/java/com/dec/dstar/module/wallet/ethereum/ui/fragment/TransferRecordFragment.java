package com.dec.dstar.module.wallet.ethereum.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.dec.dstar.R;
import com.dec.dstar.base.BaseFragment;
import com.dec.dstar.module.wallet.ethereum.iview.ITransferRecordView;
import com.dec.dstar.module.wallet.ethereum.model.TransferRecordsModel;
import com.dec.dstar.module.wallet.ethereum.presenter.TransferRecordsPresenter;
import com.dec.dstar.utils.DLog;
import com.dec.dstar.utils.Utils;
import com.dec.dstar.widget.DividerDecoration;
import com.dec.dstar.widget.recyclerview.CommonAdapter;
import com.dec.dstar.widget.recyclerview.CommonViewHolder;
import com.dec.dstar.widget.recyclerview.MultiItemTypeAdapter;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/19 15:14
 * 文件描述: 转账记录中的fragment
 */
public class TransferRecordFragment extends BaseFragment<TransferRecordsPresenter> implements ITransferRecordView {

    private static final String ARG_TRANSFER_TYPE = "ARG_TRANSFER_TYPE";
    @BindView(R.id.transferrecord_rv)
    RecyclerView mRecyclerview;
    //0: 全部；1：转出；2：转入；3：失败
    private int mType = 0;
    private String mSymbol = "";
    private CommonAdapter commonAdapter;

    private boolean flag = true;
    private List<TransferRecordsModel.Result.DataBean> list = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 1:
                    String status = "";
                    String txType = "";
                    if (mType == 0) {
                    } else if (mType == 1) {

                        txType = "send";
                    } else if (mType == 2) {

                        txType = "receive";
                    } else if (mType == 3) {

                        status = "failed";
                    }
                    mPresenter.getTransferRecords(mSymbol, status, txType, 1, 20);
                    break;
            }
        }
    };

    /**
     * 获取布局文件资源id
     */
    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_transferrecord;
    }


    /**
     * 初始化控件
     *
     * @param view
     */
    @Override
    protected void initializeView(View view) {

        initRecyclerView();


    }

    private void setParam(String symbol, int type) {

        mSymbol = symbol;
        mType = type;
    }

    @Override
    protected TransferRecordsPresenter createPresenter() {
        return new TransferRecordsPresenter(mContext, this, null, this);
    }

    public static TransferRecordFragment newInstance(String symbol, int type) {

        Bundle args = new Bundle();
        args.putInt(ARG_TRANSFER_TYPE, type);
        TransferRecordFragment fragment = new TransferRecordFragment();
        fragment.setParam(symbol, type);
        fragment.setArguments(args);
        return fragment;
    }

    private void initRecyclerView() {

        mRecyclerview.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerview.addItemDecoration(new DividerDecoration(mContext));
        commonAdapter = new CommonAdapter<TransferRecordsModel.Result.DataBean>(mContext, R.layout.item_transferrecord, list, new LinearLayoutHelper()) {
            @Override
            protected void convert(CommonViewHolder holder, TransferRecordsModel.Result.DataBean model, int position) {

                holder.setText(R.id.transferrecord_tv_walletaddr, model.getFrom());
                holder.setText(R.id.transferrecord_tv_time, Utils.date2TimeStamp(model.getCreateTime() + ""));
                holder.setText(R.id.transferrecord_tv_count, TextUtils.isEmpty(model.getValue()) ? "0.0000" : Convert.fromWei(model.getValue(), Convert.Unit.ETHER).setScale(4, BigDecimal.ROUND_HALF_DOWN).toString());
                holder.setText(R.id.transferrecord_tv_status, model.getStatus());
            }
        };
        commonAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, final int position) {


            }

            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });
        mRecyclerview.setAdapter(commonAdapter);
    }

    public void requestNetwork() {
        if (flag) {
            handler.sendEmptyMessageDelayed(1, 500);
        }
        flag = false;
    }

    @Override
    public void getTransferRecords(TransferRecordsModel.Result result) {

        if (result != null && result.getData() != null && result.getData().size() != 0) {
            list.clear();
            list.addAll(result.getData());
            commonAdapter.notifyDataSetChanged();
        }
    }
}
