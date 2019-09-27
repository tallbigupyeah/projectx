package com.minchainx.networklite.request;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PostStringRequest extends OkHttpRequest {

    private static MediaType MEDIA_TYPE_TEXT_PLAIN = MediaType.parse("text/plain;charset=utf-8");

    private String mBodyContent;
    private MediaType mMediaType;

    public PostStringRequest(String url, Map<String, String> params, Map<String, String> headers,
                             String bodyContent, MediaType mediaType, Object tag, int id) {
        super(url, params, headers, tag, id);
        if (bodyContent == null) {
            this.mBodyContent = "";
        } else {
            this.mBodyContent = bodyContent;
        }
        if (mediaType == null) {
            this.mMediaType = MEDIA_TYPE_TEXT_PLAIN;
        } else {
            this.mMediaType = mediaType;
        }
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(mMediaType, mBodyContent);
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return mBuilder.post(requestBody).build();
    }
}
