package com.minchainx.networklite.request;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.minchainx.networklite.builder.PostFormBuilder;
import com.minchainx.networklite.callback.Callback;

import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PostFormRequest extends OkHttpRequest {

    private List<PostFormBuilder.FileInput> mFiles;

    public PostFormRequest(String url, Map<String, String> params, Map<String, String> headers,
                           List<PostFormBuilder.FileInput> files, Object tag, int id) {
        super(url, params, headers, tag, id);
        this.mFiles = files;
    }

    @Override
    protected RequestBody buildRequestBody() {
        if (mFiles == null || mFiles.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            addParams(builder);
            return builder.build();
        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            addParams(builder);
            PostFormBuilder.FileInput fileInput;
            for (int i = 0, size = mFiles.size(); i < size; i++) {
                fileInput = mFiles.get(i);
                if (fileInput != null) {
                    RequestBody requestBody = RequestBody.create(
                            MediaType.parse(getMimeType(fileInput.filename)),
                            fileInput.file);
                    builder.addFormDataPart(fileInput.key, fileInput.filename, requestBody);
                }
            }
            return builder.build();
        }
    }

    @Override
    protected RequestBody wrapRequestBody(RequestBody requestBody, final Callback callback) {
        if (callback != null && !(mFiles == null || mFiles.isEmpty())) {
            return new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
                @Override
                public void onRequestProgress(final long bytesWritten, final long contentLength) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onProgressUpdate(bytesWritten * 1.0f / contentLength,
                                    contentLength, getId());
                        }
                    });
                }
            });
        }
        return requestBody;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return mBuilder.post(requestBody).build();
    }

    private void addParams(FormBody.Builder builder) {
        if (mParams != null && !mParams.isEmpty()) {
            String value;
            for (String key : mParams.keySet()) {
                value = mParams.get(key);
                if (!TextUtils.isEmpty(value)) {
                    builder.add(key, value);
                }
            }
        }
    }

    private void addParams(MultipartBody.Builder builder) {
        if (mParams != null && !mParams.isEmpty()) {
            String value;
            for (String key : mParams.keySet()) {
                value = mParams.get(key);
                if (!TextUtils.isEmpty(value)) {
                    builder.addPart(
                            Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                            RequestBody.create(null, value));
                }
            }
        }
    }

    private String getMimeType(String filename) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = null;
        try {
            contentType = fileNameMap.getContentTypeFor(URLEncoder.encode(filename, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(contentType)) {
            contentType = "application/octet-stream";
        }
        return contentType;
    }
}
