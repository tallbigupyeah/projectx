package com.minchainx.networklite.callback;

import okhttp3.Call;
import okhttp3.Response;

public abstract class Callback<T> {

    /**
     * 请求前回调
     * 在主线程运行
     *
     * @param id 请求id
     */
    public void onPreExecute(int id) {
    }

    /**
     * 请求后回调（无论成功失败）
     * UI Thread
     *
     * @param result 请求结果 true成功 false失败
     * @param id     请求id
     */
    public void onPostExecute(boolean result, int id) {
    }

    /**
     * 请求过程中回调
     * UI Thread
     *
     * @param progress 进度(0-1)
     * @param total    总数
     * @param id       请求id
     */
    public void onProgressUpdate(float progress, long total, int id) {
    }

    /**
     * 解析响应结果
     *
     * @param response 响应结果
     * @param id       请求id
     * @return
     */
    public abstract T parseResponse(Response response, int id) throws Exception;

    /**
     * 请求失败回调
     *
     * @param call      请求
     * @param exception 异常
     * @param id        请求id
     */
    public abstract void onFailure(Call call, Exception exception, int id);

    /**
     * 请求成功回调
     *
     * @param call     请求
     * @param response 请求结果
     * @param id       请求id
     */
    public abstract void onSuccess(Call call, T response, int id);

    /**
     * 默认回调
     */
    public static Callback DEFAULT = new Callback() {

        @Override
        public Object parseResponse(Response response, int id) {
            return null;
        }

        @Override
        public void onFailure(Call call, Exception exception, int id) {
        }

        @Override
        public void onSuccess(Call call, Object response, int id) {
        }
    };
}
