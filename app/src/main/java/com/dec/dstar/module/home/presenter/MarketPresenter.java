package com.dec.dstar.module.home.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.dec.dstar.base.BasePresenter;
import com.dec.dstar.module.home.iview.IMarketView;
import com.dec.dstar.module.home.model.TickerListResponse;
import com.dec.dstar.network.loader.LoopringApiLoader;
import com.dec.dstar.network.subscriber.ProgressDialogSubscriber;
import com.dec.dstar.utils.DLog;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/22 19:16
 * 文件描述: 市场行情列表页面
 */
public class MarketPresenter extends BasePresenter<IMarketView> {


    public MarketPresenter(Context context, IMarketView view, Activity activity, Fragment fragment) {
        super(context, view, activity, fragment);
    }

    public void getTickerList(){
        sendRequest(LoopringApiLoader.getInstance().getTickerList(), new ProgressDialogSubscriber<TickerListResponse>(mView) {
            @Override
            public void onNext(TickerListResponse getBalanceModel) {

                mView.getTickerList(getBalanceModel.result);
            }
        });
    }
}
