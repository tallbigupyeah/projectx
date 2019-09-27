package com.dec.dstar.network.loader;

import com.dec.dstar.config.EnvironmentConfig;
import com.dec.dstar.config.User;
import com.dec.dstar.network.DstarApiManager;
import com.dec.dstar.network.api.LoopringApi;
import com.dec.dstar.network.request.loopring.BaseRequest;
import com.dec.dstar.network.request.loopring.NotifyTransactionSubmitedRequest;
import com.dec.dstar.network.request.loopring.TransferRecordsRequest;
import com.dec.dstar.network.request.loopring.WalletBalanceRequest;


import rx.Observable;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/3 14:14
 * 文件描述: 路印API
 */
public class LoopringApiLoader extends DstarApiManager<LoopringApi> {

    private static final String TAG = "LoopringApiLoader";

    protected LoopringApiLoader(Class apiService, String baseUrl) {
        super(apiService, baseUrl);
    }

    private static LoopringApiLoader sInstance = null;

    private LoopringApiLoader() {
        super(LoopringApi.class, EnvironmentConfig.getInstance().getBaseUrl());
    }

    public static LoopringApiLoader getInstance() {
        if (sInstance == null) {
            sInstance = new LoopringApiLoader();
        }
        return sInstance;
    }

    /**
     * 获取钱包代币余额
     */
    public Observable getWalletBalance(String owner) {

        WalletBalanceRequest request = new WalletBalanceRequest();
        request.params[0].setOwner(owner);
        return loaderComposeCommon(getApiService().getWalletBalance(request));
    }

    /**
     * 获取代币合约地址列表
     */
    public Observable getContractAddressList(){

        BaseRequest request = new BaseRequest();
        request.setMethod("loopring_getSupportedTokens");
        return loaderComposeCommon(getApiService().getContractAddressList(request));
    }

    /**
     * 转账记录列表
     */
    public Observable getTransferRecords(String symbol, String status, String txType, int pageIndex, int pageSize){

        TransferRecordsRequest request = new TransferRecordsRequest();
        request.getParams()[0].setOwner(User.getInstance().getWalletAddress());
        request.getParams()[0].setSymbol(symbol);
        request.getParams()[0].setStatus(status);
        request.getParams()[0].setPageIndex(pageIndex);
        request.getParams()[0].setPageSize(pageSize);
        request.getParams()[0].setTxType(txType);
        return loaderComposeCommon(getApiService().getTranferRecords(request));
    }

    /**
     * 获取交易对列表
     */
    public Observable getTickerList(){

        BaseRequest request = new BaseRequest();
        request.setMethod("loopring_getTicker");
        return loaderComposeCommon(getApiService().getTickerList(request));
    }

    /**
     * 通知中继交易已成功
     * @return
     */
    public Observable notifyTransactionSubmitted(NotifyTransactionSubmitedRequest request){

        return loaderComposeCommon(getApiService().notifyTransactionSubmitted(request));
    }

    @Override
    protected String getBaseUrl() {
        return EnvironmentConfig.getInstance().getBaseUrl();
    }
}












