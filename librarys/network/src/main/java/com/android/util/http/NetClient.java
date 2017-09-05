package com.android.util.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.request.NetRequest;
import com.android.util.http.task.Task;
import com.android.util.log.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 张全
 */

public class NetClient {

    private static NetClient mInstance = null;
    private OkHttpClient mHttpClient;
    private Handler mDelivery;
    public static boolean LOGABLE = true;

    /**
     * 日志拦截器
     */
    static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            Logger.d("NetClient", String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

//            long t2 = System.nanoTime();
//            Logger.d("NetClient",String.format("Received response for %s in %.1fms%n%s",
//                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        }
    }

    private NetClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS);

        if (LOGABLE) {
            builder.addInterceptor(new LoggingInterceptor());
        }
        mHttpClient = builder.build();
        try {
            SSLContext sc = null;
            sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {

                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {

                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }}, new SecureRandom());

            HostnameVerifier hv1 = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            String workerClassName = "okhttp3.OkHttpClient";
            try {
                Class workerClass = Class.forName(workerClassName);
                Field hostnameVerifier = workerClass.getDeclaredField("hostnameVerifier");
                hostnameVerifier.setAccessible(true);
                hostnameVerifier.set(mHttpClient, hv1);

                Field sslSocketFactory = workerClass.getDeclaredField("sslSocketFactory");
                sslSocketFactory.setAccessible(true);
                sslSocketFactory.set(mHttpClient, sc.getSocketFactory());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static NetClient getInstance() {
        if (null == mInstance) {
            synchronized (NetClient.class) {
                if (null == mInstance) {
                    mInstance = new NetClient();
                }
            }
        }
        return mInstance;
    }


    public Task execute(final NetRequest request) {
        OkHttpClient httpClient = mHttpClient;
        OkHttpClient.Builder builder = null;

        //连接超时
        long connectTimeout = request.connectTimeout();
        if (connectTimeout > 0) {
            if (null == builder) builder = httpClient.newBuilder();
            builder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        }
        //读取数据超时
        long readTimeout = request.readTimeout();
        if (readTimeout > 0) {
            if (null == builder) builder = httpClient.newBuilder();
            builder.readTimeout(readTimeout, TimeUnit.MILLISECONDS);
        }
        //发送数据超时
        long writeTimeout = request.writeTimeout();
        if (writeTimeout > 0) {
            if (null == builder) builder = httpClient.newBuilder();
            builder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        }
        if (null != builder) {
            httpClient = builder.build();
        }
        Call call = httpClient.newCall(request.getRealRequest());
        request.getCallback().start();
        call.enqueue(new okhttp3.Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                if (call.isCanceled()) {
                    //call.cancel也会回调onFailure()
                    return;
                }
                postFailResult(request.getCallback(), call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (call.isCanceled()) {
                    return;
                }

                String data = null;
                try {
                    data = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                logResponse(data, response);

                if (response.isSuccessful()) {
                    if (TextUtils.isEmpty(data)) {
                        postFailResult(request.getCallback(), call, new RuntimeException("response data=null"));
                        return;
                    }
                    try {
                        Object obj = request.parseResponse(data);
                        postSuccessResult(request.getCallback(), call, obj);
                    } catch (Exception e) {
                        postFailResult(request.getCallback(), call, e);
                    }
                } else {
                    postFailResult(request.getCallback(), call, new RuntimeException(data));
                }
            }
        });
        return new Task(call, request);
    }

    private void logResponse(String data, Response response) {
        try {
            StringBuffer logInfo = new StringBuffer();
            logInfo.append("Received response for " + response.request().url()).append("\n")
                    .append(response.headers())
                    .append("\r\n");
            data = data.trim();
            if (data.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(data);
                String message = jsonObject.toString(2);
                logInfo.append(message);
            } else if (data.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(data);
                String message = jsonArray.toString(2);
                logInfo.append(message);
            } else {
                logInfo.append(data);
            }
            Logger.d("NetClient", logInfo.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void postFailResult(final ICallback callback, final Call call, final Exception e) {
        Logger.d("NetClient", "请求失败 " + e.getMessage());
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (null != call) callback.failure(new NetException(e));
                if (null != call) callback.end();
            }
        });
    }

    private void postSuccessResult(final ICallback callback, final Call call, final Object o) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                if (null != call) callback.success(o);
                if (null != call) callback.end();
            }
        });
    }
}
