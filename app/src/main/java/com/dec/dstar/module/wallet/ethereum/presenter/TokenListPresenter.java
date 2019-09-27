package com.dec.dstar.module.wallet.ethereum.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.dec.dstar.base.BasePresenter;
import com.dec.dstar.module.wallet.ethereum.iview.ITokenListView;
import com.dec.dstar.module.wallet.ethereum.model.SupportTokens;
import com.dec.dstar.network.loader.LoopringApiLoader;
import com.dec.dstar.network.subscriber.ProgressDialogSubscriber;
import com.minchainx.base.clazz.iview.IBaseView;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/27 20:16
 * 文件描述:
 */
public class TokenListPresenter extends BasePresenter<ITokenListView> {

    public TokenListPresenter(Context context, ITokenListView view, Activity activity, Fragment fragment) {
        super(context, view, activity, fragment);
    }

    public void getTokenList(){
        sendRequest(LoopringApiLoader.getInstance().getContractAddressList(), new ProgressDialogSubscriber<SupportTokens>(mView) {
            @Override
            public void onNext(SupportTokens result) {

                if(result != null && result.result != null && result.result.size() != 0){
                    mView.getTokenList(result.result);
                }
            }
        });
    }
}
