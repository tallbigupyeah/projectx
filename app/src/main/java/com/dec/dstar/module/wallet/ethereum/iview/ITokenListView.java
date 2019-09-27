package com.dec.dstar.module.wallet.ethereum.iview;

import com.dec.dstar.base.IBaseView;
import com.dec.dstar.module.wallet.ethereum.model.SupportTokens;

import java.util.List;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/27 20:16
 * 文件描述:
 */
public interface ITokenListView extends IBaseView{

    /**
     * 获取token列表
     * @param list
     */
    void getTokenList(List<SupportTokens.Token> list);
}
