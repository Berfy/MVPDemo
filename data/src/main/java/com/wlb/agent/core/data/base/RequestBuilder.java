package com.wlb.agent.core.data.base;

import android.text.TextUtils;

import com.android.util.http.callback.ICallback;
import com.android.util.http.request.NetRequest;
import com.android.util.http.response.DataParser;
import com.android.util.log.Logger;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;

import org.json.JSONObject;

import okhttp3.CacheControl;
import okhttp3.RequestBody;

/**
 * 张全
 */

public class RequestBuilder extends NetRequest.Builder {
    private static final String GET = "GET";
    private static final String POST = "POST";

    public static RequestBuilder postBuilder(String url, ICallback callback, DataParser dataParser) {
        return getBuilder(POST, url, callback, dataParser);
    }

    public static RequestBuilder getBuilder(String url, ICallback callback, DataParser dataParser) {
        return getBuilder(GET, url, callback, dataParser);
    }

    private static RequestBuilder getBuilder(String method, String url, ICallback callback, DataParser dataParser) {
        RequestBuilder requestBuilder = new RequestBuilder();
        if (GET.equals(method)) {
            requestBuilder.get();
            //请求参数
            url=RequestConfig.constructGetUrl(url);
//            try {
//                // versionCode
//                requestBuilder.addParam("appVersion", DataConfig.versionCode);
//                // versionName
//                requestBuilder.addParam("versionName", DataConfig.versionName);
//                //系统版本号
//                requestBuilder.addParam("systemVersion", Build.VERSION.RELEASE);
//                //应用标识
//                requestBuilder.addParam("bundleId", "com.wolaibao.agent");
//                //渠道
//                requestBuilder.addParam("pid", "");
//                // 平台
//                requestBuilder.addParam("platform", "android");
//                //设备id
//                String deviceId = CommonUtil.getDeviceId(DataConfig.getContext());
//                requestBuilder.addParam("deviceId", deviceId);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } else if (POST.equals(method)) {
            requestBuilder.post();
        }
        //请求url
        requestBuilder.url(url);
        requestBuilder.callback(callback);
        requestBuilder.dataParser(dataParser);
        requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);

        //用户token
        String token = null;
        try {
            UserResponse loginedUser = UserClient.getLoginedUser();
            if (null != loginedUser) {
                token = loginedUser.token;
            }
            token = TextUtils.isEmpty(token) ? "" : token;
            requestBuilder.addHeader("Wlb-Token", token);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestBuilder;
    }
    public NetRequest.Builder setPostJsonData(JSONObject jsonObject) {
        RequestBody requestBody = RequestConfig.toRequestBody(jsonObject);
        post(requestBody);
        try {
            StringBuffer logInfo = new StringBuffer();
            logInfo.append("POST Sending request for " + getUrl()).append("\n");
            logInfo.append(jsonObject.toString(2));
            Logger.d("NetClient", logInfo.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public JSONObject getPostJson() {
        return RequestConfig.getJsonObject();
    }
}
