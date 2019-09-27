package com.dec.dstar.utils.ethereum;

import android.util.Log;

import com.dec.dstar.network.request.loopring.NotifyTransactionSubmitedRequest;
import com.dec.dstar.utils.AsyncUtils;
import com.dec.dstar.utils.CommonToast;
import com.dec.dstar.utils.DLog;
import com.dec.dstar.utils.WalletUtil;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.AdminFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/6 19:08
 * 文件描述: 以太坊交易管理
 */
public final class TransactionManager {

    private static final String TAG = "TransactionManager";
    private Admin web3j = null;
    // 尽量在应用初始化的时候获取gasPrice
    private BigInteger gasPrice = null;
    // 使用infura站点
//    private static final String mainNet = "https://mainnet.infura.io/v3/1f4a89ef04d84df4b7ad0252a81b8139";
//    private static final String testNet = "https://rinkeby.infura.io/v3/1f4a89ef04d84df4b7ad0252a81b8139";
    private static final String testNet = "http://13.112.62.24/eth";

    public static TransactionManager getInstance() {
        return InnerHolder.INSTANCE;
    }

    private TransactionManager() {
        web3j = AdminFactory.build(new HttpService(testNet));
    }

    private static class InnerHolder {
        public static TransactionManager INSTANCE = new TransactionManager();
    }

    public Admin getWeb3j() {
        return web3j;
    }

    /**
     * 获取指定钱包的eth余额
     */
    public BigInteger getBalanceByAddress(final String address) {

        BigInteger balance = null;
        try {
            balance = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return balance;
    }

    /**
     * 获取nonce值
     */
    public BigInteger getNonce(String address) {
        BigInteger ethGetTransactionCount = null;
//            ethGetTransactionCount = web3j.ethGetTransactionCount(
//                    address, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        try {
            ethGetTransactionCount = web3j.ethGetTransactionCount(
                    address, DefaultBlockParameterName.PENDING).sendAsync().get().getTransactionCount();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return ethGetTransactionCount;
    }


    /**
     * 转账交易
     */
    public NotifyTransactionSubmitedRequest transferEth(String fromAddress, String toAddress, String gasPrice1, String value, String walletName, String password) {

        //导出钱包
        Credentials credentials = WalletUtil.getCredentials(walletName, password);
        if (credentials == null) {
            return null;
        }
        BigInteger biValue = Convert.toWei(value, Convert.Unit.ETHER).toBigInteger();

        //生成rawTransaction
        BigInteger gasPrice = new BigInteger("21000000000");
        BigInteger gasLimit = new BigInteger("24000");
        BigInteger nonce = getNonce(fromAddress);
        Log.e(TAG, "nonce--->" + nonce);
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, gasPrice, gasLimit, toAddress, biValue);

        //交易签名
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        //发送交易
        String transactionHash = null;
//            transactionHash = web3j.ethSendRawTransaction(hexValue).send().getTransactionHash();
        try {
            transactionHash = web3j.ethSendRawTransaction(hexValue).sendAsync().get().getTransactionHash();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        NotifyTransactionSubmitedRequest request = new NotifyTransactionSubmitedRequest();
        if (transactionHash != null) {

            request.getParams()[0].setFrom(fromAddress);
//            request.getParams()[0].setGas(getGasPrice().toString(16));
            request.getParams()[0].setGas(gasLimit.toString(16));
//            request.getParams()[0].setGasPrice(getGasPrice().toString(16));
            request.getParams()[0].setGasPrice(gasPrice.toString(16));
            request.getParams()[0].setHash(transactionHash);
            request.getParams()[0].setInput("0x");
            request.getParams()[0].setValue(biValue.toString(16));
            request.getParams()[0].setTo(toAddress);
//            request.getParams()[0].setNonce(nonce.toString(16));
            request.getParams()[0].setNonce(nonce.toString());
        } else {

            return null;
        }
        return request;
    }


    /**
     * 其他代币转账
     */
    public NotifyTransactionSubmitedRequest transferOtherCurrency(String contractAddr, String fromAddress, String toAddress, String gasPrice, String value, String walletName, String password) {

        //导出钱包
        Credentials credentials = WalletUtil.getCredentials(walletName, password);
        if (credentials == null) {
            return null;
        }
        BigInteger biValue = Convert.toWei(value, Convert.Unit.ETHER).toBigInteger();

        Address address = new Address(toAddress);
        BigInteger gas = new BigInteger("21000000000");
        BigInteger gasLimit = new BigInteger("90000");
        BigInteger nonce = getNonce(fromAddress);
        Uint256 uintValue = new Uint256(biValue);
        List<Type> parametersList = new ArrayList<>();
        parametersList.add(address);
        parametersList.add(uintValue);
        List<TypeReference<?>> outList = new ArrayList<>();
        Function function = new Function("transfer", parametersList, outList);
        String encodedFunction = FunctionEncoder.encode(function);
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gas,
                gasLimit, contractAddr, encodedFunction);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        String transactionHash = null;
        try {
            transactionHash = web3j.ethSendRawTransaction(hexValue).send().getTransactionHash();
        } catch (IOException e) {
            e.printStackTrace();
        }

        NotifyTransactionSubmitedRequest request = new NotifyTransactionSubmitedRequest();
        if (transactionHash != null) {

            request.getParams()[0].setFrom(fromAddress);
            request.getParams()[0].setGas(getGasPrice().toString(16));
            request.getParams()[0].setGasPrice(getGasPrice().toString(16));
            request.getParams()[0].setHash(transactionHash);
            request.getParams()[0].setInput("0x");
            request.getParams()[0].setValue(biValue.toString(16));
            request.getParams()[0].setTo(toAddress);
//            request.getParams()[0].setNonce(nonce.toString(16));
            request.getParams()[0].setNonce(nonce.toString());
        } else {

            return null;
        }

        return request;
    }

    /**
     * 初始化gasPrice
     */
    public void initGasPrice() {
        if (gasPrice == null) {
            new AsyncUtils<>(new AsyncUtils.AsyncUtilsListener<Object>() {
                @Override
                public Object subThreadOperate() {
                    try {

                        gasPrice = web3j.ethGasPrice().send().getGasPrice();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return gasPrice;
                }

                @Override
                public void mainThreadCallback(Object o) {

                }
            });
        }
    }

    /**
     * 获取gasPrice
     */
    public BigInteger getGasPrice() {
        if (gasPrice == null) {
            initGasPrice();
            CommonToast.showMessage("正在初始化gasPrice...");
            return null;
        }
        return gasPrice;
    }
}
















