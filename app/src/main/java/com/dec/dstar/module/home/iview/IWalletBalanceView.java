package com.dec.dstar.module.home.iview;

import com.dec.dstar.base.IBaseView;
import com.dec.dstar.module.home.model.GetBalanceModel;

import java.util.ArrayList;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/3 14:49
 * 文件描述: 钱包代币余额
 */
public interface IWalletBalanceView extends IBaseView {

    /**
     * 处理后的token列表
     * @param tokens
     */
    void getWalletBalance(ArrayList<GetBalanceModel.Token> tokens);

    /**
     * 未处理的token列表
     */
    void getOriginalTokenList(ArrayList<GetBalanceModel.Token> tokens);
}
