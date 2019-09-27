package com.dec.dstar.module.wallet.ethereum.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.dec.dstar.base.BasePresenter;
import com.dec.dstar.module.wallet.ethereum.iview.ITransferView;
import com.dec.dstar.module.wallet.ethereum.model.EthTransferModel;
import com.dec.dstar.module.wallet.ethereum.model.SupportTokens;
import com.dec.dstar.network.loader.LoopringApiLoader;
import com.dec.dstar.network.request.loopring.NotifyTransactionSubmitedRequest;
import com.dec.dstar.network.response.loopring.BaseResponse;
import com.dec.dstar.network.subscriber.ProgressDialogSubscriber;
import com.dec.dstar.utils.AsyncUtils;
import com.dec.dstar.utils.CommonToast;
import com.dec.dstar.utils.DLog;
import com.dec.dstar.utils.ToastCustomUtils;
import com.dec.dstar.utils.WalletUtil;
import com.dec.dstar.utils.ethereum.TransactionManager;

import org.loopring.looprsdk.services.EthereumService;
import org.loopring.looprsdk.services.SendEth;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.loopring.looprsdk.services.SendEth.Companion;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/2 19:52
 * 文件描述: 以太坊转账页面的presenter
 */
public class TransferPresenter extends BasePresenter<ITransferView> {

    public TransferPresenter(Context context, ITransferView view, Activity activity, Fragment fragment) {
        super(context, view, activity, fragment);
    }

    /**
     * 获取代币合约地址列表
     */
    public void getContractAddressList(final String symbol) {
        sendRequest(LoopringApiLoader.getInstance().getContractAddressList(), new ProgressDialogSubscriber<SupportTokens>(mView) {
            @Override
            public void onNext(SupportTokens result) {

                boolean flag = false;
                for (SupportTokens.Token token : result.result) {
                    if (symbol.equalsIgnoreCase(token.getSymbol())) {

                        mView.getContractAddr(token.getProtocol());
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    mView.getContractAddr("");
                }
            }
        });
    }



    /**
     * 通知中继转账已成功
     */
    public void notifyTransactionSubmitted(NotifyTransactionSubmitedRequest request) {
        sendRequest(LoopringApiLoader.getInstance().notifyTransactionSubmitted(request), new ProgressDialogSubscriber<BaseResponse>(mView) {
            @Override
            public void onNext(BaseResponse response) {

                mView.transferSuccess();
            }
        });

    }
}