package com.dec.dstar.module.wallet.ethereum.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.dec.dstar.base.BasePresenter;
import com.dec.dstar.module.wallet.ethereum.iview.ITransferRecordView;
import com.dec.dstar.module.wallet.ethereum.model.TransferRecordsModel;
import com.dec.dstar.network.loader.LoopringApiLoader;
import com.dec.dstar.network.subscriber.ProgressDialogSubscriber;
import com.dec.dstar.utils.DLog;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/19 16:43
 * 文件描述: 转账记录Presenter
 */
public class TransferRecordsPresenter extends BasePresenter<ITransferRecordView> {

    public TransferRecordsPresenter(Context context, ITransferRecordView view, Activity activity, Fragment fragment) {
        super(context, view, activity, fragment);
    }

    public void getTransferRecords(String symbol, String status, String txType, int pageIndex, int pageSize) {

        sendRequest(LoopringApiLoader.getInstance().getTransferRecords(symbol, status, txType, pageIndex, pageSize), new ProgressDialogSubscriber<TransferRecordsModel>(mView) {
            @Override
            public void onNext(TransferRecordsModel result) {

                mView.getTransferRecords(result.result);
            }
        });
    }
}
