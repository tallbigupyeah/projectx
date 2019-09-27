package com.dec.dstar.module.home.model;

import com.dec.dstar.network.response.loopring.BaseResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/3 09:49
 * 文件描述:获取钱包代币余额
 */
public class GetBalanceModel extends BaseResponse<GetBalanceModel.Result> {

    public class Result{

        public String delegateAddress;//代理委托地址(路印提供)
        public String owner;//需要查询的钱包
        public ArrayList<Token> tokens; //拥有的代币列表

        public String getDelegateAddress() {
            return delegateAddress;
        }

        public void setDelegateAddress(String delegateAddress) {
            this.delegateAddress = delegateAddress;
        }

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public ArrayList<Token> getTokens() {
            return tokens;
        }

        public void setTokens(ArrayList<Token> tokens) {
            this.tokens = tokens;
        }
    }

    public class Token implements Serializable{
        public String symbol;//代币名称
        public String balance;//余额
        public String allowance;//津贴

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getAllowance() {
            return allowance;
        }

        public void setAllowance(String allowance) {
            this.allowance = allowance;
        }
    }
}
