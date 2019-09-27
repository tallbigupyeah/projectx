package com.dec.dstar.module.wallet.ethereum.model;

import com.dec.dstar.network.response.loopring.BaseResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/12 14:43
 * 文件描述: 支持的代币合约地址列表
 */
public class SupportTokens extends BaseResponse<List<SupportTokens.Token>> {

    public class Token {

        /**
         * protocol : 0xe1c541ba900cbf212bc830a5aaf88ab499931751
         * symbol : LRC
         * name :
         * source : loopring
         * time : 0
         * deny : false
         * decimals : 1000000000000000000
         * isMarket : true
         * icoPrice : null
         */

        private String protocol;
        private String symbol;
        private String name;
        private String source;
        private int time;
        private boolean deny;
        private long decimals;
        private boolean isMarket;
        private String icoPrice;

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public boolean isDeny() {
            return deny;
        }

        public void setDeny(boolean deny) {
            this.deny = deny;
        }

        public long getDecimals() {
            return decimals;
        }

        public void setDecimals(long decimals) {
            this.decimals = decimals;
        }

        public boolean isIsMarket() {
            return isMarket;
        }

        public void setIsMarket(boolean isMarket) {
            this.isMarket = isMarket;
        }

        public String getIcoPrice() {
            return icoPrice;
        }

        public void setIcoPrice(String icoPrice) {
            this.icoPrice = icoPrice;
        }
    }
}
