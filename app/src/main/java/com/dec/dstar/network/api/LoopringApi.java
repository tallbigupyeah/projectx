package com.dec.dstar.network.api;

import com.dec.dstar.module.home.model.TickerListResponse;
import com.dec.dstar.module.wallet.ethereum.model.EthTransferModel;
import com.dec.dstar.module.home.model.GetBalanceModel;
import com.dec.dstar.module.wallet.ethereum.model.SupportTokens;
import com.dec.dstar.module.wallet.ethereum.model.TransferRecordsModel;
import com.dec.dstar.network.request.loopring.BaseRequest;
import com.dec.dstar.network.request.loopring.NotifyTransactionSubmitedRequest;
import com.dec.dstar.network.request.loopring.TransferRecordsRequest;
import com.dec.dstar.network.request.loopring.WalletBalanceRequest;
import com.dec.dstar.network.response.loopring.BaseResponse;


import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/3 09:33
 * 文件描述: 路印API，由于路印请求的接口地址是放在body里，所以这里所有的接口地址都是一致的
 */
public interface LoopringApi {

    /**
     * 获取钱包代币余额
     */
    @POST ("rpc/v2/")
    Observable<GetBalanceModel> getWalletBalance(@Body WalletBalanceRequest request);

    /**
     * 获取代币合约地址列表
     */
    @POST ("rpc/v2/")
    Observable<SupportTokens> getContractAddressList(@Body BaseRequest request);

    /**
     * 转账记录列表
     */
    @POST ("rpc/v2/")
    Observable<TransferRecordsModel> getTranferRecords(@Body TransferRecordsRequest request);

    /**
     * 获取交易对列表
     */
    @POST ("rpc/v2/")
    Observable<TickerListResponse> getTickerList(@Body BaseRequest request);

    /**
     * 通知中继交易已成功
     */
    @POST ("rpc/v2/")
    Observable<BaseResponse> notifyTransactionSubmitted(@Body NotifyTransactionSubmitedRequest request);
}
