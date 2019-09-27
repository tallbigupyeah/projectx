package com.dec.dstar.network.request.loopring;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/30 20:07
 * 文件描述:
 */
public final class NotifyTransactionSubmitedRequest {

    public String jsonrpc = "2.0";
    public String method = "loopring_notifyTransactionSubmitted";
    public int id = 64;
    public NotifyTransactionSubmitedRequest.Param[] params = new NotifyTransactionSubmitedRequest.Param[1];

    public NotifyTransactionSubmitedRequest(){
        params[0] = new NotifyTransactionSubmitedRequest.Param();
    }

    public NotifyTransactionSubmitedRequest.Param[] getParams() {
        return params;
    }

    public void setParams(NotifyTransactionSubmitedRequest.Param[] params) {
        this.params = params;
    }

    public class Param{
        private String hash;
        private String nonce;
        private String to;
        private String value;
        private String gasPrice;
        private String gas;
        private String input;
        private String from;

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public String getNonce() {
            return nonce;
        }

        public void setNonce(String nonce) {
            this.nonce = nonce;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getGasPrice() {
            return gasPrice;
        }

        public void setGasPrice(String gasPrice) {
            this.gasPrice = gasPrice;
        }

        public String getGas() {
            return gas;
        }

        public void setGas(String gas) {
            this.gas = gas;
        }

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

    }
}
