package com.dec.dstar.module.home.iview;

import com.dec.dstar.base.IBaseView;
import com.dec.dstar.module.home.model.TickerListResponse;

import java.util.List;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/22 19:17
 * 文件描述: 市场行情首页列表
 */
public interface IMarketView extends IBaseView{

    /**
     * 交易对列表
     * @param list
     */
    void getTickerList(List<TickerListResponse.Ticker> list);
}
