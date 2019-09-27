package com.minchainx.network.interceptor;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TimeCalibrationInterceptor implements Interceptor {

    /**
     * 响应头信息中标注时间过期的字段名
     */
    public static final String HEADER_NAME_KEY = "sign";
    /**
     * 响应头信息中标注时间过期的字段值
     */
    public static final String HEADER_NAME_VALUE = "te";

    private TimeCalibrationInterceptor.Callback callback;
    private String tag;

    public TimeCalibrationInterceptor(Callback callback) {
        this.callback = callback;
        if (this.callback != null) {
            this.tag = this.callback.generateLogTag();
        } else {
            this.tag = "okhttpTimestampExpired";
        }
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        String url = request.url().toString();
        i("url=" + url);

        Response response = chain.proceed(request);

        String value = response.header(HEADER_NAME_KEY, "");
        i(HEADER_NAME_KEY + "=" + value);
        if (!TextUtils.isEmpty(value) && value.equals(HEADER_NAME_VALUE)) {
            e("TimeCalibrationInterceptor " + response.code() + " " +
                    response.request().method() + " " + response.request().url());
            if (callback != null) {
                callback.requestServerTime();
            }
        }

        return response;
    }

    private void i(String msg) {
        if (callback != null && callback.getLogOutputState()) {
            Log.i(tag, msg);
        }
    }

    private void e(String msg) {
        if (callback != null && callback.getLogOutputState()) {
            Log.e(tag, msg);
        }
    }

    public interface Callback {

        /**
         * 请求服务器时间
         */
        void requestServerTime();

        /**
         * 获取日志输出状态
         */
        boolean getLogOutputState();

        /**
         * 生成日志TAG
         */
        String generateLogTag();
    }
}
