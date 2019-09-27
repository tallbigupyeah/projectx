package com.minchainx.networklite.interceptor;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {

    private final ILogger logger;
    private Map<String, Map<String, String>> headers;

    public HeaderInterceptor() {
        this(new DefaultCallback());
    }

    public HeaderInterceptor(HeaderInterceptor.Callback callback) {
        this.logger = new DefaultLogger(callback);
        this.headers = new HashMap<String, Map<String, String>>();
    }

    public Map<String, Map<String, String>> getHeaders() {
        return headers;
    }

    public Map<String, String> getHeader(String regex) {
        return headers.containsKey(regex) ? headers.get(regex) : null;
    }

    public HeaderInterceptor setHeaders(Map<String, Map<String, String>> headers) {
        if (headers != null) {
            this.headers.clear();
            this.headers.putAll(headers);
        }
        return this;
    }

    public HeaderInterceptor addHeader(String regex, Map<String, String> header) {
        if (header != null && header.size() > 0 && !TextUtils.isEmpty(regex)) {
            headers.put(regex, header);
        }
        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("--------------------" + getClass().getSimpleName() + " Start--------------------");

        Request oldRequest = chain.request();
        Request.Builder builder = oldRequest.newBuilder();

        String url = oldRequest.url().toString();
        sb.append("\n" + oldRequest.method() + " " + url);

        if (headers != null && !TextUtils.isEmpty(url)) {
            sb.append("\nRequest Header:");

            String regex;
            Map<String, String> header;
            boolean isFirst = true;

            StringBuffer sbHeader = new StringBuffer();
            sbHeader.append("{");

            for (Map.Entry<String, Map<String, String>> headerEntry : headers.entrySet()) {
                regex = headerEntry.getKey();
                if (!TextUtils.isEmpty(regex) && url.matches(regex)) {
                    header = headers.get(regex);
                    if (header != null) {
                        for (Map.Entry<String, String> entry : header.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            if (!TextUtils.isEmpty(key)) {
                                if (value == null) {
                                    value = "";
                                }
                                builder.addHeader(key, value);
                                if (isFirst) {
                                    isFirst = false;
                                } else {
                                    sbHeader.append(",");
                                }
                                sbHeader.append("\n\"" + key + "\"" + ":\"" + value + "\"");
                            }
                        }
                    }
                }
            }

            sbHeader.append("\n}");
            sb.append(jsonFormat(sbHeader.toString()));
        }
        sb.append("\n====================" + getClass().getSimpleName() + " End======================");

        logger.log(sb.toString());

        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }

    /**
     * @param jsonText string param .expect a json string
     * @return formatted json string if param is a json string,otherwise return the param
     */
    public static String jsonFormat(String jsonText) {
        try {
            jsonText = jsonText.trim();
            if (jsonText.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(jsonText);
                return jsonObject.toString(2);
            }
            if (jsonText.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonText);
                return jsonArray.toString(2);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonText;
    }

    public interface ILogger {

        /**
         * Android's max limit for a log entry is ~4076 bytes,
         * so 4000 bytes is used as chunk size since default charset
         * is UTF-8
         */
        int CHUNK_SIZE = 4000;

        void log(String msg);
    }

    private static class DefaultLogger implements ILogger {

        private Callback callback;
        private String tag;

        public DefaultLogger(Callback callback) {
            this.callback = callback;
            this.tag = callback.generateLogTag();
        }

        @Override
        public synchronized void log(String msg) {
            if (callback.getLogOutputState()) {
                byte[] bytes = msg.getBytes();
                int length = bytes.length;
                String chunk;
                for (int i = 0; i < length; i += CHUNK_SIZE) {
                    int count = Math.min(length - i, CHUNK_SIZE);
                    //create a new String with system's default charset (which is UTF-8 for Android)
                    chunk = new String(bytes, i, count);
                    String[] lines = chunk.split(System.getProperty("line.separator"));
                    for (String line : lines) {
                        Log.i(tag, line);
                    }
                }
            }
        }
    }

    public interface Callback {

        /**
         * 获取日志输出状态
         */
        boolean getLogOutputState();

        /**
         * 生成日志TAG
         */
        String generateLogTag();
    }

    private static class DefaultCallback implements Callback {

        @Override
        public boolean getLogOutputState() {
            return true;
        }

        @Override
        public String generateLogTag() {
            return "";
        }
    }
}
