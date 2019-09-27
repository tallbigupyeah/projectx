package com.dec.dstar.module.wallet.ethereum.iview;

import com.dec.dstar.base.IBaseView;
import com.dec.dstar.module.wallet.ethereum.model.SupportTokens;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/2 19:47
 * 文件描述: 以太坊转账页面接口回调函数
 */
public interface ITransferView extends IBaseView {

    /**
     * 转账成功
     */
    void transferSuccess();

    /**
     * 获取代币合约地址列表
     */
    void getContractAddr(String contractAddr);
}
