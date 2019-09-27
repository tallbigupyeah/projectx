package com.minchainx.networklite.interceptor;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.StatusLine;
import okio.Buffer;
import okio.BufferedSource;

public class HttpLoggerInterceptor implements Interceptor {

    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final ILogger logger;
    private Callback callback;

    public HttpLoggerInterceptor(HttpLoggerInterceptor.Callback callback) {
        if (callback == null) {
            this.callback = new DefaultCallback();
        } else {
            this.callback = callback;
        }
        this.logger = new DefaultLogger(callback);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (!callback.getLogOutputState()) {
            return chain.proceed(request);
        }

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        StringBuffer sbRequest = new StringBuffer();
        sbRequest.append("--------------------" + getClass().getSimpleName() + "--------------------");

        Connection connection = chain.connection();
        Protocol protocol = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        sbRequest.append("\n--> " + request.method() + ' ' + request.url() + ' ' + protocol);
        if (hasRequestBody) {
            sbRequest.append(" (" + requestBody.contentLength() + "-byte body)");

            // Request body headers are only present when installed as a network interceptor. Force
            // them to be included (when available) so there values are known.
            if (requestBody.contentType() != null) {
                sbRequest.append("\nContent-Type: " + requestBody.contentType());
            }
            if (requestBody.contentLength() != -1) {
                sbRequest.append("\nContent-Length: " + requestBody.contentLength());
            }
        }

        sbRequest.append("\n-->Request Header:{");
        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            // Skip headers from the request body as they are explicitly logged above.
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                sbRequest.append("\n\"" + name + "\"" + ":\"" + headers.value(i) + "\",");
            }
        }
        sbRequest.append("\n}");

        if (!hasRequestBody) {
            sbRequest.append("\n--> END " + request.method());
        } else if (bodyEncoded(request.headers())) {
            sbRequest.append("\n--> END " + request.method() + " (encoded body omitted)");
        } else {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }

            if (isPlaintext(buffer)) {
                sbRequest.append("\n" + decode(buffer.readString(charset)));
            } else {
                sbRequest.append("\n" + jsonFormat(buffer.readString(charset)));
            }

            sbRequest.append("\n--> END " + request.method() + " (" + requestBody.contentLength() + "-byte body)");
        }

        StringBuffer sbResponse = new StringBuffer();

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            sbResponse.append("\n<-- HTTP FAILED: " + e);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();
        sbResponse.append("\n<-- " + response.code() + ' ' + response.message() + ' '
//                + response.request().url()
                + " (" + tookMs + "ms" + ')');

        sbResponse.append("\n-->Response Header:{");
        Headers responseHeaders = response.headers();
        for (int i = 0, count = responseHeaders.size(); i < count; i++) {
            sbResponse.append("\n" + responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }
        sbResponse.append("\n}");

        if (!hasBody(response)) {
            sbResponse.append("\n<-- END HTTP");
        } else if (bodyEncoded(response.headers())) {
            sbResponse.append("\n<-- END HTTP (encoded body omitted)");
        } else {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    sbResponse.append("\n");
                    sbResponse.append("\nCouldn't decode the response body; charset is likely malformed.");
                    sbResponse.append("\n<-- END HTTP");
                    if (!isPlaintext(buffer)) {
                        return response;
                    }
                }
            }

            if (contentLength != 0) {
                sbResponse.append("\n");
                sbResponse.append("\n<-- Response Body: \n");
                String responseText = buffer.clone().readString(charset);
//                if (isJSONText(responseText)) {
//                    sbResponse.append("\n" + jsonFormat(responseText));
//                } else {
//                    sbResponse.append("\n" + responseText);
//                }
                sbResponse.append("\n" + responseText);
            }

            sbResponse.append("\n<-- END HTTP (" + buffer.size() + "-byte body)");
        }

        sbResponse.append("\n====================" + getClass().getSimpleName() + "====================");

        logger.log(sbRequest.toString() + "\n" + sbResponse.toString());

        return response;
    }

    public boolean isJSONText(String text) {
        if (!TextUtils.isEmpty(text)) {
            text = text.trim();
            return text.startsWith("{") && text.endsWith("}");
        }
        return false;
    }

    /**
     * Returns true if the response must have a (possibly 0-length) body. See RFC 2616 section 4.3.
     */
    public boolean hasBody(Response response) {
        // HEAD requests never yield a body regardless of the response headers.
        if (response.request().method().equals("HEAD")) {
            return false;
        }

        int responseCode = response.code();
        if ((responseCode < StatusLine.HTTP_CONTINUE || responseCode >= 200)
                && responseCode != HttpURLConnection.HTTP_NO_CONTENT
                && responseCode != HttpURLConnection.HTTP_NOT_MODIFIED) {
            return true;
        }

        // If the Content-Length or Transfer-Encoding headers disagree with the
        // response code, the response is malformed. For best compatibility, we
        // honor the headers.
        if (contentLength(response) != -1
                || "chunked".equalsIgnoreCase(response.header("Transfer-Encoding"))) {
            return true;
        }

        return false;
    }

    public long contentLength(Response response) {
        return contentLength(response.headers());
    }

    public long contentLength(Headers headers) {
        return stringToLong(headers.get("Content-Length"));
    }

    private long stringToLong(String s) {
        if (s == null) return -1;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    public static String decode(String text) {
        String rtn;
        try {
            rtn = URLDecoder.decode(text, "UTF-8");
        } catch (Exception e) {
            rtn = text;
//            e.printStackTrace();
        }
        return rtn;
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
