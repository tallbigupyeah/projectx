package com.dec.dstar.config;

/**
 * 作者：luoxiaohui
 * 日期:2018/8/3 17:17
 * 文件描述: 路印环境配置
 */
public final class EnvironmentConfig {

    public enum ENVIRONMENT{
        PRODUCTION,
        TEST
    }

    private static EnvironmentConfig INSTANCE;
    private String baseUrl;
    private String delegateAddress;//请求路印需要的委托地址
    private ENVIRONMENT environment;
    private EnvironmentConfig(){}

    public static EnvironmentConfig getInstance(){
        if (INSTANCE == null){
            INSTANCE = new EnvironmentConfig();
        }
        return INSTANCE;
    }

    public void setBaseUrl(ENVIRONMENT environment){

        this.environment = environment;
        if (environment.equals(ENVIRONMENT.PRODUCTION)){

            baseUrl = Constant.LOOPRING_PRO;
            delegateAddress = Constant.DELEGATEADDRESS_PRO;
        } else if (environment.equals(ENVIRONMENT.TEST)){

            baseUrl = Constant.LOOPRING_TEST;
            delegateAddress = Constant.DELEGATEADDRESS_TEST;
        }
    }

    public String getBaseUrl(){

        return baseUrl;
    }

    public String getDelegateAddress(){

        return delegateAddress;
    }

    public ENVIRONMENT getEnvironment(){

        return environment;
    }
}
