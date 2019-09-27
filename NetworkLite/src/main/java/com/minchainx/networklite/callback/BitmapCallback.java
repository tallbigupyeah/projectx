package com.minchainx.networklite.callback;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import okhttp3.Response;

public abstract class BitmapCallback extends Callback<Bitmap> {

    @Override
    public Bitmap parseResponse(Response response, int id) throws Exception {
        return BitmapFactory.decodeStream(response.body().byteStream());
    }
}
