package com.dec.dstar.module.home.model;

import com.dec.dstar.network.response.loopring.BaseResponse;

import java.util.List;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/22 19:33
 * 文件描述: 交易对列表
 */
public class TickerListResponse extends BaseResponse<List<TickerListResponse.Ticker>> {

    public class Ticker{
        /**
         * market : AE-LRC
         * exchange :
         * interval :
         * amount : 0
         * vol : 0
         * open : 0
         * close : 0
         * high : 0
         * low : 0
         * last : 0
         * buy : 0
         * sell : 0
         * change :
         */

        private String market;
        private String exchange;
        private String interval;
        private int amount;
        private int vol;
        private int open;
        private int close;
        private int high;
        private int low;
        private int last;
        private int buy;
        private int sell;
        private String change;

        public String getMarket() {
            return market;
        }

        public void setMarket(String market) {
            this.market = market;
        }

        public String getExchange() {
            return exchange;
        }

        public void setExchange(String exchange) {
            this.exchange = exchange;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getVol() {
            return vol;
        }

        public void setVol(int vol) {
            this.vol = vol;
        }

        public int getOpen() {
            return open;
        }

        public void setOpen(int open) {
            this.open = open;
        }

        public int getClose() {
            return close;
        }

        public void setClose(int close) {
            this.close = close;
        }

        public int getHigh() {
            return high;
        }

        public void setHigh(int high) {
            this.high = high;
        }

        public int getLow() {
            return low;
        }

        public void setLow(int low) {
            this.low = low;
        }

        public int getLast() {
            return last;
        }

        public void setLast(int last) {
            this.last = last;
        }

        public int getBuy() {
            return buy;
        }

        public void setBuy(int buy) {
            this.buy = buy;
        }

        public int getSell() {
            return sell;
        }

        public void setSell(int sell) {
            this.sell = sell;
        }

        public String getChange() {
            return change;
        }

        public void setChange(String change) {
            this.change = change;
        }
    }
}
