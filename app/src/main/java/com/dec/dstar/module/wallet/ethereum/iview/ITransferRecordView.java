package com.dec.dstar.module.wallet.ethereum.iview;

import com.dec.dstar.base.IBaseView;
import com.dec.dstar.module.wallet.ethereum.model.TransferRecordsModel;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/19 16:25
 * 文件描述: 转账记录view接口
 */
public interface ITransferRecordView extends IBaseView {

    void getTransferRecords(TransferRecordsModel.Result result);
}
