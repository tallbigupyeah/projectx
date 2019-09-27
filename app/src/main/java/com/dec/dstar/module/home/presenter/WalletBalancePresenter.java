package com.dec.dstar.module.home.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.dec.dstar.base.BasePresenter;
import com.dec.dstar.module.home.iview.IWalletBalanceView;
import com.dec.dstar.module.home.model.GetBalanceModel;
import com.dec.dstar.network.loader.LoopringApiLoader;
import com.dec.dstar.network.subscriber.ProgressDialogSubscriber;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/3 14:58
 * 文件描述: 钱包代币余额presenter
 */
public class WalletBalancePresenter extends BasePresenter<IWalletBalanceView> {


    public WalletBalancePresenter(Context context, IWalletBalanceView view, Activity activity, Fragment fragment) {
        super(context, view, activity, fragment);
    }

    public void getWalletBalance(String ownerAddress) {
        sendRequest(LoopringApiLoader.getInstance().getWalletBalance(ownerAddress), new ProgressDialogSubscriber<GetBalanceModel>(mView) {
            @Override
            public void onNext(GetBalanceModel getBalanceModel) {

                if (getBalanceModel == null || getBalanceModel.result == null ||
                        getBalanceModel.result.getTokens() == null || getBalanceModel.result.getTokens().size() == 0) {

                    mView.getWalletBalance(null);
                } else {

                    mView.getWalletBalance(handleWalletBalance(getBalanceModel.result.getTokens()));
                    mView.getOriginalTokenList(getBalanceModel.result.getTokens());
                }
            }
        });
    }

    /**
     * 处理代币余额列表
     * 包括顺序问题和显示
     */
    private ArrayList<GetBalanceModel.Token> handleWalletBalance(ArrayList<GetBalanceModel.Token> tokens) {

        ArrayList<GetBalanceModel.Token> list = new ArrayList<>();
        //首先剔除余额为0的代币
        for (GetBalanceModel.Token token : tokens) {
            if (!token.getBalance().equals("0")) {
                list.add(token);
            }
        }
        //然后，对token首字母按a-z排序
        Collections.sort(list, new MySortByValue());
        return list;
    }


    public static class MySortByValue implements Comparator {

        public int compare(Object o1, Object o2) {

            GetBalanceModel.Token token1 = (GetBalanceModel.Token) o1;
            GetBalanceModel.Token token2 = (GetBalanceModel.Token) o2;
            if (token1.getSymbol().compareToIgnoreCase(token2.getSymbol()) < 0) {
                return -1;
            }
            return 1;
        }
    }
}













