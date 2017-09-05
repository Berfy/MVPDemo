package com.wlb.agent.core.data.base;

import android.text.TextUtils;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.RequestBody;
import okhttp3.internal.http.HttpMethod;

/**
 * @author 张全
 */

public final class RestRequest {
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String UTF8 = "UTF-8";

    private int connectTimeout;
    private int readTimeout;
    private int writeTimeout;

    private String url;
    private Map<String,String> headerMap=new HashMap<>();
    private RequestBody requestBody;
    private  Object tag;
    private  String method;



    private RestRequest(RestRequest.Builder builder) {
        Headers headers = builder.headers.build();
        int size = headers.size();
        for(int i=0;i<size;i++){
            String key = headers.name(i);
            String value = headers.value(i);
            headerMap.put(key,value);
        }

        this.method = builder.method;
        this.url = builder.url;
        this.requestBody = builder.body;
        this.tag = builder.tag;
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.writeTimeout = builder.writeTimeout;
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

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }
    public RequestBody getRequestBody() {
        return requestBody;
    }

    public Object getTag() {
        return tag;
    }
    public String getMethod() {
        return method;
    }

    public static class Builder {
        private Map<String, Object> params = new HashMap<>();
        private String url;

        private String method;
        private Headers.Builder headers;
        private RequestBody body;
        private Object tag;

        private int connectTimeout;
        private int readTimeout;
        private int writeTimeout;

//        private ICallback mCallback;
//        private DataParser mDataParser;

        public Builder() {
            this.method = GET;
            this.headers = new Headers.Builder();
        }
        public Builder(String url) {
            if (url == null) throw new NullPointerException("url == null");
            this.url = url;
            this.method = GET;
            this.headers = new Headers.Builder();
        }

        public RestRequest.Builder url(String url) {
            if (url == null) throw new NullPointerException("url == null");
            this.url = url;
            return this;
        }
        public String getUrl(){
            return this.url;
        }

        /**
         * 设置Header，如果已经存在同名的header，则替换
         */
        public RestRequest.Builder header(String name, String value) {
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
        public RestRequest.Builder addHeader(String name, String value) {
            headers.add(name, value);
            return this;
        }

        public RestRequest.Builder removeHeader(String name) {
            headers.removeAll(name);
            return this;
        }

        /**
         * Removes all headers on this builder and adds {@code headers}.
         */
        public RestRequest.Builder headers(Headers headers) {
            this.headers = headers.newBuilder();
            return this;
        }

        /**
         * Sets this request's {@code Cache-Control} header, replacing any cache control headers already
         * present. If {@code cacheControl} doesn't define any directives, this clears this request's
         * cache-control headers.
         */
        public RestRequest.Builder cacheControl(CacheControl cacheControl) {
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
        public RestRequest.Builder addParam(String key, Object value) {
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
        public RestRequest.Builder removeParam(String key) {
            if (null != params) {
                params.remove(key);
            }
            return this;
        }

        public RestRequest.Builder get() {
            return method(GET, null);
        }


        public RestRequest.Builder post(RequestBody body) {
            return method(POST, body);
        }

        public RestRequest.Builder post() {
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

        public RestRequest.Builder method(String method, RequestBody body) {
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

        public RestRequest.Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public RestRequest.Builder connectTimeout(long timeout, TimeUnit unit) {
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
        public RestRequest.Builder readTimeout(long timeout, TimeUnit unit) {
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
        public RestRequest.Builder writeTimeout(long timeout, TimeUnit unit) {
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

        //-------------------------------------------------------------
        private String constructUrl(String path,String encoding) {
            if (method == POST) {
                return path;
            } else {
                try {
                    StringBuilder url = new StringBuilder(path);
                    if (params != null && !params.isEmpty()) {
                        if(!url.toString().contains("?")){
                            url.append("?");
                        }else{
                            url.append("&");
                        }
                        for (Map.Entry<String, Object> entry : params.entrySet()) {
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
        public RestRequest.Builder setPostJsonData(JSONObject jsonObject) {
            RequestBody requestBody = RequestConfig.toRequestBody(jsonObject);
            post(requestBody);
//            try {
//                StringBuffer logInfo = new StringBuffer();
//                logInfo.append("Sending request for " + getUrl()).append("\n");
//                logInfo.append(jsonObject.toString(2));
//                Logger.d("NetClient", logInfo.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            return this;
        }

        public JSONObject getPostJson() {
            return RequestConfig.getJsonObject();
        }

        public RestRequest build() {
            if (url == null) throw new IllegalStateException("url == null");
            if(method.equalsIgnoreCase(GET)){
                url=RequestConfig.constructGetUrl(url);
                url=constructUrl(url,UTF8);
            }

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

            return new RestRequest(this);
        }
    }
}
