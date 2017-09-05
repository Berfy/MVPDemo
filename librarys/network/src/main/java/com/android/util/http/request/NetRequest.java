package com.android.util.http.request;

import android.text.TextUtils;

import com.android.util.http.callback.ICallback;
import com.android.util.http.response.DataParser;
import com.android.util.http.response.StringParser;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;

/**
 * 张全
 */

public final class NetRequest {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String UTF8 = "UTF-8";
    private Request mRealRequest;
    private ICallback mCallback;
    private DataParser mDataParser;

    private int connectTimeout;
    private int readTimeout;
    private int writeTimeout;

    private NetRequest(Builder builder) {
        String method = builder.method;
        String url = builder.url;
        Headers headers = builder.headers.build();
        Map<String, Object> params = builder.params;
        url = constructUrl(method, url, params, UTF8);
        RequestBody body = builder.body;
        Object tag = builder.tag;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.writeTimeout = builder.writeTimeout;
        this.mCallback = builder.mCallback;
        this.mDataParser = builder.mDataParser;

        Request.Builder reqBuilder = new Request.Builder();
        //请求url
        reqBuilder.url(url);
        //请求消息头
        reqBuilder.headers(headers);
        //请求消息体
        if (null != body) {
            reqBuilder.post(body);
        }
        reqBuilder.tag(tag);

        mRealRequest = reqBuilder.build();
    }

    private String constructUrl(String method, String path, Map<String, Object> values, String encoding) {
        if (method == POST) {
            return path;
        } else {
            try {
                StringBuilder url = new StringBuilder(path);
                if (values != null && !values.isEmpty()) {
                    url.append("?");
                    for (Map.Entry<String, Object> entry : values.entrySet()) {
                        String key = entry.getKey();
                        if (!TextUtils.isEmpty(key)) {
                            key = URLEncoder.encode(key, encoding);
                        }
                        url.append(key);
                        url.append("=");
                        Object value = entry.getValue();
                        if (null != value && value instanceof String) {
                            url.append(URLEncoder.encode((String) value, encoding));
                        } else {
                            url.append(value);
                        }
                        url.append("&");
                    }
                    url.deleteCharAt(url.length() - 1);
                }
                return url.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return path;
    }

    public long connectTimeout() {
        return connectTimeout;
    }

    public long readTimeout() {
        return readTimeout;
    }

    public long writeTimeout() {
        return writeTimeout;
    }

    public Request getRealRequest() {
        return mRealRequest;
    }

    public ICallback getCallback() {
        return mCallback;
    }

    public DataParser getDataParser() {
        return mDataParser;
    }

    public Object parseResponse(String response) throws Exception {
        return mDataParser.parseReponse(response);
    }

    public void stop() {
        mCallback = null;
    }

    @Override
    public String toString() {
        return "NetRequest{" +
                "mRealRequest=" + mRealRequest +
                ", mCallback=" + mCallback +
                ", mDataParser=" + mDataParser +
                ", connectTimeout=" + connectTimeout +
                ", readTimeout=" + readTimeout +
                ", writeTimeout=" + writeTimeout +
                '}';
    }

    public static class Builder {
        private Map<String, Object> params = new HashMap<String, Object>();
        private String url;

        private String method;
        private Headers.Builder headers;
        private RequestBody body;
        private Object tag;

        private int connectTimeout;
        private int readTimeout;
        private int writeTimeout;

        private ICallback mCallback;
        private DataParser mDataParser;

        public Builder() {
            this.method = GET;
            this.headers = new Headers.Builder();
            this.mDataParser = new StringParser();
        }

        public Builder url(String url) {
            if (url == null) throw new NullPointerException("url == null");
            this.url = url;
            return this;
        }
        public String getUrl(){
            return this.url;
        }

        public Builder callback(ICallback callback) {
            if (callback == null) throw new NullPointerException("callback == null");
            this.mCallback = callback;
            return this;
        }

        public Builder dataParser(DataParser dataParser) {
            this.mDataParser = dataParser;
            return this;
        }

        /**
         * 设置Header，如果已经存在同名的header，则替换
         */
        public Builder header(String name, String value) {
            headers.set(name, value);
            return this;
        }

        /**
         * Adds a header with {@code name} and {@code value}. Prefer this method for multiply-valued
         * headers like "Cookie".
         * <p>
         * <p>Note that for some headers including {@code Content-Length} and {@code Content-Encoding},
         * OkHttp may replace {@code value} with a header derived from the request body.
         */
        public Builder addHeader(String name, String value) {
            headers.add(name, value);
            return this;
        }

        public Builder removeHeader(String name) {
            headers.removeAll(name);
            return this;
        }

        /**
         * Removes all headers on this builder and adds {@code headers}.
         */
        public Builder headers(Headers headers) {
            this.headers = headers.newBuilder();
            return this;
        }

        /**
         * Sets this request's {@code Cache-Control} header, replacing any cache control headers already
         * present. If {@code cacheControl} doesn't define any directives, this clears this request's
         * cache-control headers.
         */
        public Builder cacheControl(CacheControl cacheControl) {
            String value = cacheControl.toString();
            if (value.isEmpty()) return removeHeader("Cache-Control");
            return header("Cache-Control", value);
        }


        /**
         * 添加请求参数
         *
         * @param key
         * @param value
         * @return
         */
        public Builder addParam(String key, Object value) {
            if (null == params) {
                params = new HashMap<String, Object>();
            }
            params.put(key, value);
            return this;
        }

        /**
         * 移除请求参数
         *
         * @param key
         */
        public Builder removeParam(String key) {
            if (null != params) {
                params.remove(key);
            }
            return this;
        }

        public Builder get() {
            return method(GET, null);
        }


        public Builder post(RequestBody body) {
            return method(POST, body);
        }

        public Builder post() {
            this.method = POST;
            return this;
        }
//        public Builder head() {
//            return method("HEAD", null);
//        }

//        public Builder delete(RequestBody body) {
//            return method("DELETE", body);
//        }

//        public Builder delete() {
//            return delete(RequestBody.create(null, new byte[0]));
//        }
//
//        public Builder put(RequestBody body) {
//            return method("PUT", body);
//        }
//
//        public Builder patch(RequestBody body) {
//            return method("PATCH", body);
//        }

        public Builder method(String method, RequestBody body) {
            if (method == null) throw new NullPointerException("method == null");
            if (method.length() == 0) throw new IllegalArgumentException("method.length() == 0");
            if (body != null && !HttpMethod.permitsRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must not have a request body.");
            }
            if (body == null && HttpMethod.requiresRequestBody(method)) {
                throw new IllegalArgumentException("method " + method + " must have a request body.");
            }
            this.method = method;
            this.body = body;
            return this;
        }

        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder connectTimeout(long timeout, TimeUnit unit) {
            if (timeout < 0) throw new IllegalArgumentException("timeout < 0");
            if (unit == null) throw new NullPointerException("unit == null");
            long millis = unit.toMillis(timeout);
            if (millis > Integer.MAX_VALUE)
                throw new IllegalArgumentException("Timeout too large.");
            if (millis == 0 && timeout > 0)
                throw new IllegalArgumentException("Timeout too small.");
            connectTimeout = (int) millis;
            return this;
        }

        /**
         * Sets the default read timeout for new connections. A value of 0 means no timeout, otherwise
         * values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
         */
        public Builder readTimeout(long timeout, TimeUnit unit) {
            if (timeout < 0) throw new IllegalArgumentException("timeout < 0");
            if (unit == null) throw new NullPointerException("unit == null");
            long millis = unit.toMillis(timeout);
            if (millis > Integer.MAX_VALUE)
                throw new IllegalArgumentException("Timeout too large.");
            if (millis == 0 && timeout > 0)
                throw new IllegalArgumentException("Timeout too small.");
            readTimeout = (int) millis;
            return this;
        }

        /**
         * Sets the default write timeout for new connections. A value of 0 means no timeout, otherwise
         * values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
         */
        public Builder writeTimeout(long timeout, TimeUnit unit) {
            if (timeout < 0) throw new IllegalArgumentException("timeout < 0");
            if (unit == null) throw new NullPointerException("unit == null");
            long millis = unit.toMillis(timeout);
            if (millis > Integer.MAX_VALUE)
                throw new IllegalArgumentException("Timeout too large.");
            if (millis == 0 && timeout > 0)
                throw new IllegalArgumentException("Timeout too small.");
            writeTimeout = (int) millis;
            return this;
        }

        public NetRequest build() {
            if (url == null) throw new IllegalStateException("url == null");
            if (mCallback == null) throw new NullPointerException("callback == null");

            if (method.equalsIgnoreCase(POST)) {
                if (!params.isEmpty()) {
                    FormBody.Builder formBuilder = new FormBody.Builder();
                    for (Map.Entry<String, Object> entry : params.entrySet()) {
                        String key = entry.getKey();
                        String value = null;

                        Object valueObj = entry.getValue();
                        if (null != valueObj) {
                            value = String.valueOf(valueObj);
                        }
                        formBuilder.add(key, value);
                    }
                    body = formBuilder.build();
                }
            }

            return new NetRequest(this);
        }
    }
}
