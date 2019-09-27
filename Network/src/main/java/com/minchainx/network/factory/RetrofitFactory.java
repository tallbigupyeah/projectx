package com.minchainx.network.factory;

import android.content.Context;
import android.os.Build;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.minchainx.network.factory.gson.GsonConverterFactory;
import com.minchainx.network.utils.LogUtils;
import com.minchainx.networklite.HttpsHelper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

public class RetrofitFactory {

    private static final long DEFAULT_TIMEOUT = 60000; // 默认超时时间(单位:毫秒)
    private static final long CONNECT_TIMEOUT = DEFAULT_TIMEOUT; // 连接超时时间
    private static final long READ_TIMEOUT = DEFAULT_TIMEOUT; // 读取超时时间
    private static final long WRITE_TIMEOUT = DEFAULT_TIMEOUT; // 写入超时时间

    private RetrofitFactory() {
    }

    public static Retrofit createDefaultRetrofit(String baseUrl, OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }

    public static OkHttpClient.Builder getDefaultClientBuilder(Context context, InputStream[] certificates) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                // 设置连接超时时间
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                // 设置读取超时时间
                .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
                // 设置写入超时时间
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                // 错误重连
//                .retryOnConnectionFailure(true)
                .sslSocketFactory(HttpsHelper.getSslSocketFactory(certificates));
        if (context != null) {
            //Cookie持久化
            builder.cookieJar(new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context)));
        }
        return enableTls12OnPreLollipop(builder);
    }

    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            try {
                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception exc) {
                LogUtils.e("e=" + exc.fillInStackTrace().toString());
            }
        }
        return client;
    }
}
