package com.minchainx.networklite.callback;

import java.io.IOException;

import okhttp3.Response;

public abstract class StringCallback extends Callback<String> {

    @Override
    public String parseResponse(Response response, int id) throws IOException {
        return response.body().string();
    }
}
