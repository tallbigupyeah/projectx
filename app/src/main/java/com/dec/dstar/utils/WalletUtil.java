package com.dec.dstar.utils;

import com.dec.dstar.R;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.ObjectMapperFactory;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

/**
 * 钱包Utils
 */
public class WalletUtil {
    private static final String TAG = "WalletUtil";
    static ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
    public static String ETH_JAXX_TYPE = "m/44'/60'/0'/0/0";
    /**
     * 随机
     */
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();



    public static EthereumWallet createWallet(String walletName, String password) {

        File destination = new File(AppFilePath.Wallet_DIR, walletName + ".tmp");

        if (destination.exists()) {
            ToastCustomUtils.showLong(R.string.wallet_namesake);
            return null;
        }
        //目录不存在则创建目录，创建不了则报错
        if (!createParentDir(destination)) {
            return null;
        }

        ECKeyPair ecKeyPair = null;
        WalletFile walletFile = null;
        try {
            ecKeyPair = Keys.createEcKeyPair();
            walletFile = Wallet.create(password, ecKeyPair, 16, 1); // WalletUtils. .generateNewWalletFile();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        EthereumWallet ethereumWallet = new EthereumWallet();
        try {
            /**
             * 保存钱包到本地
             */
            objectMapper.writeValue(destination, walletFile);
            String keyStore = objectMapper.writeValueAsString(walletFile);
            ethereumWallet.setKeyStore(keyStore);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ethereumWallet.setCredentials(Credentials.create(ecKeyPair));
        return ethereumWallet;
    }

    public static EthereumWallet reCreateWallet(String walletName, String password) {

        File destination = new File(AppFilePath.Wallet_DIR, walletName + ".tmp");

        if (!destination.exists()) {
            return null;
        }


        ECKeyPair ecKeyPair = null;
        WalletFile walletFile = null;
        try {
            ecKeyPair = Keys.createEcKeyPair();
            walletFile = Wallet.create(password, ecKeyPair, 16, 1); // WalletUtils. .generateNewWalletFile();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        EthereumWallet ethereumWallet = new EthereumWallet();
        try {
            /**
             * 保存钱包到本地
             */
            objectMapper.writeValue(destination, walletFile);
            String keyStore = objectMapper.writeValueAsString(walletFile);
            ethereumWallet.setKeyStore(keyStore);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ethereumWallet.setCredentials(Credentials.create(ecKeyPair));
        return ethereumWallet;
    }

    public static boolean createParentDir(File file) {
        //判断目标文件所在的目录是否存在
        if (!file.getParentFile().exists()) {
            //如果目标文件所在的目录不存在，则创建父目录
            System.out.println("目标文件所在目录不存在，准备创建");
            if (!file.getParentFile().mkdirs()) {
                System.out.println("创建目标文件所在目录失败！");
                return false;
            }
        }
        return true;
    }

    public static Credentials getCredentials(String walletName, String password){

        File destination = new File(AppFilePath.Wallet_DIR, walletName + ".tmp");

        if (!destination.exists()) {
            ToastCustomUtils.showLong(R.string.wallet_nowallet);
            return null;
        }

        Credentials credentials = null;
        try {
            credentials = WalletUtils.loadCredentials(
                    password,
                    destination);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }
        return credentials;
    }
}


















