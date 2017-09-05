package com.wlb.agent.core.data.base;

import android.text.TextUtils;

import com.wlb.agent.core.data.DataConfig;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RestClient {

    public static final String REST_API_URL = DataConfig.apiHost;
    private static Retrofit s_retrofit;

    static {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        s_retrofit = checkSSL();
//        s_retrofit = new Retrofit.Builder()
//                .baseUrl(REST_API_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
//                .client(client)
//                .build();
    }

    static class HeaderIntercepter implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {

            //用户token
            String token = null;
            try {
                UserResponse loginedUser = UserClient.getLoginedUser();
                if (null != loginedUser) {
                    token = loginedUser.token;
                }
                token = TextUtils.isEmpty(token) ? "" : token;
            } catch (Exception e) {
                e.printStackTrace();
            }

            Request request = chain.request();
            request = request.newBuilder()
                    .addHeader("Wlb-Token", token)
                    .addHeader("Cache-Control", CacheControl.FORCE_NETWORK.toString())
                    .build();
            Response response = chain.proceed(request);

            return response;
        }
    }

    public static Retrofit checkSSL() {
        OkHttpClient client;
        try {
            TrustManager[] trustManager = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws
                                CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws
                                CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[]{};
                            //  return null;    // 返回null
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustManager, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            client = new OkHttpClient.Builder()
                    //.addInterceptor(interceptor)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    //.addNetworkInterceptor(mTokenInterceptor4
                    .sslSocketFactory(sslSocketFactory)
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .build();
            return new Retrofit.Builder()
                    .baseUrl(REST_API_URL)
                    .client(client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        client = new OkHttpClient.Builder()
                //.addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
        return new Retrofit.Builder()
                .baseUrl(REST_API_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }


    public static <T> T getService(Class<T> serviceClass) {
        return s_retrofit.create(serviceClass);
    }


}
