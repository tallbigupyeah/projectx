package com.dec.dstar;

import android.app.Application;

import com.dec.dstar.config.EnvironmentConfig;
import com.dec.dstar.utils.AppFilePath;
import com.dec.dstar.utils.AsyncUtils;
import com.dec.dstar.utils.ethereum.TransactionManager;

import org.loopring.looprsdk.services.SendEth;

import io.realm.Realm;

public class DstarApplication extends Application {
    public static DstarApplication INSTANCE;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        Realm.init(this);
        EnvironmentConfig.getInstance().setBaseUrl(EnvironmentConfig.ENVIRONMENT.TEST);
        AppFilePath.init(this);

        TransactionManager.getInstance().initGasPrice();
    }

    public static DstarApplication getInstance(){

        return INSTANCE;
    }

}
