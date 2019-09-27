package com.minchainx.networklite;

import com.minchainx.networklite.builder.GetBuilder;
import com.minchainx.networklite.builder.PostFormBuilder;
import com.minchainx.networklite.builder.PostStringBuilder;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

public class NetworkLiteHelper {

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private static NetworkLite sNetworkLite;

    private NetworkLiteHelper() {
    }

    public static NetworkLite getNetworkLite() {
        return initializeClient(null);
    }

    public static NetworkLite initializeClient(OkHttpClient client) {
        if (sNetworkLite == null) {
            synchronized (NetworkLiteHelper.class) {
                if (sNetworkLite == null) {
                    sNetworkLite = new NetworkLite(client);
                }
            }
        }
        return sNetworkLite;
    }

    public static NetworkLite replaceClient(OkHttpClient client) {
        if (client != null) {
            synchronized (NetworkLiteHelper.class) {
                if (client != null) {
                    getNetworkLite().replaceClient(client);
                }
            }
        }
        return sNetworkLite;
    }

    public static OkHttpClient getOkHttpClient() {
        return getNetworkLite().getOkHttpClient();
    }

    public static GetBuilder get() {
        return new GetBuilder();
    }

    public static PostFormBuilder postForm() {
        return new PostFormBuilder();
    }

    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    public static PostStringBuilder postJson() {
        return new PostStringBuilder().mediaType(MEDIA_TYPE_JSON);
    }

    public static void setLogOutput(boolean enable) {
        getNetworkLite().setLogOutput(enable);
    }
}
