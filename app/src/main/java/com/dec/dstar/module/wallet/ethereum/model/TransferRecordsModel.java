package com.dec.dstar.module.wallet.ethereum.model;

import com.dec.dstar.network.response.loopring.BaseResponse;

import java.util.List;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/19 16:34
 * 文件描述: 转账记录mode
 */
public class TransferRecordsModel extends BaseResponse<TransferRecordsModel.Result> {

    public class Result{


        /**
         * data : [{"protocol":"0x75104f80928b31cce5c02e69b361a661bae29186","owner":"0x75104f80928b31cce5c02e69b361a661bae29186","from":"0x2ef680f87989bce2a9f458e450cffd6589b549fa","to":"0x75104f80928b31cce5c02e69b361a661bae29186","txHash":"0x03ac2eae13dce70d9b7f3d6d1debb3ada8e0d1ebba360d3a571eb651a777cb95","symbol":"ETH","content":{"market":"","orderHash":"","fill":""},"blockNumber":937828,"value":"30000000000000000000","logIndex":0,"type":"receive","status":"success","createTime":1533010322,"updateTime":1533010322,"gas_price":"21000000000","gas_limit":"21000","gas_used":"21000","nonce":"79"}]
         * pageIndex : 1
         * pageSize : 20
         * total : 1
         */

        public int pageIndex;
        public int pageSize;
        public int total;
        public List<DataBean> data;

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public class DataBean {
            /**
             * protocol : 0x75104f80928b31cce5c02e69b361a661bae29186
             * owner : 0x75104f80928b31cce5c02e69b361a661bae29186
             * from : 0x2ef680f87989bce2a9f458e450cffd6589b549fa
             * to : 0x75104f80928b31cce5c02e69b361a661bae29186
             * txHash : 0x03ac2eae13dce70d9b7f3d6d1debb3ada8e0d1ebba360d3a571eb651a777cb95
             * symbol : ETH
             * content : {"market":"","orderHash":"","fill":""}
             * blockNumber : 937828
             * value : 30000000000000000000
             * logIndex : 0
             * type : receive
             * status : success
             * createTime : 1533010322
             * updateTime : 1533010322
             * gas_price : 21000000000
             * gas_limit : 21000
             * gas_used : 21000
             * nonce : 79
             */

            public String protocol;
            public String owner;
            public String from;
            public String to;
            public String txHash;
            public String symbol;
            public ContentBean content;
            public int blockNumber;
            public String value;
            public int logIndex;
            public String type;
            public String status;
            public int createTime;
            public int updateTime;
            public String gas_price;
            public String gas_limit;
            public String gas_used;
            public String nonce;

            public String getProtocol() {
                return protocol;
            }

            public void setProtocol(String protocol) {
                this.protocol = protocol;
            }

            public String getOwner() {
                return owner;
            }

            public void setOwner(String owner) {
                this.owner = owner;
            }

            public String getFrom() {
                return from;
            }

            public void setFrom(String from) {
                this.from = from;
            }

            public String getTo() {
                return to;
            }

            public void setTo(String to) {
                this.to = to;
            }

            public String getTxHash() {
                return txHash;
            }

            public void setTxHash(String txHash) {
                this.txHash = txHash;
            }

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }

            public ContentBean getContent() {
                return content;
            }

            public void setContent(ContentBean content) {
                this.content = content;
            }

            public int getBlockNumber() {
                return blockNumber;
            }

            public void setBlockNumber(int blockNumber) {
                this.blockNumber = blockNumber;
            }

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }

            public int getLogIndex() {
                return logIndex;
            }

            public void setLogIndex(int logIndex) {
                this.logIndex = logIndex;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public int getCreateTime() {
                return createTime;
            }

            public void setCreateTime(int createTime) {
                this.createTime = createTime;
            }

            public int getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(int updateTime) {
                this.updateTime = updateTime;
            }

            public String getGas_price() {
                return gas_price;
            }

            public void setGas_price(String gas_price) {
                this.gas_price = gas_price;
            }

            public String getGas_limit() {
                return gas_limit;
            }

            public void setGas_limit(String gas_limit) {
                this.gas_limit = gas_limit;
            }

            public String getGas_used() {
                return gas_used;
            }

            public void setGas_used(String gas_used) {
                this.gas_used = gas_used;
            }

            public String getNonce() {
                return nonce;
            }

            public void setNonce(String nonce) {
                this.nonce = nonce;
            }

            public class ContentBean {
                /**
                 * market :
                 * orderHash :
                 * fill :
                 */

                public String market;
                public String orderHash;
                public String fill;

                public String getMarket() {
                    return market;
                }

                public void setMarket(String market) {
                    this.market = market;
                }

                public String getOrderHash() {
                    return orderHash;
                }

                public void setOrderHash(String orderHash) {
                    this.orderHash = orderHash;
                }

                public String getFill() {
                    return fill;
                }

                public void setFill(String fill) {
                    this.fill = fill;
                }
            }
        }
    }
}
