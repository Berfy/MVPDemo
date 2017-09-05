package com.wlb.agent.core.data.base;

import android.os.Build;

import com.android.util.encode.Des3Encoder;
import com.wlb.agent.core.data.BuildConfig;
import com.wlb.agent.core.data.DataConfig;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;

import org.json.JSONObject;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * @author 张全
 */
public class RequestConfig {

    private static final String encryptKey = "bbchexia&a|c=2f$#hjk=abc";

    public static JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            // versionCode
            jsonObject.put("appVersion", DataConfig.versionCode);
            // versionName
            jsonObject.put("versionName", DataConfig.versionName);
            // APP ID
            jsonObject.put("appId", Integer.valueOf(BuildConfig.appId));
            //系统版本号
            jsonObject.put("systemVersion", Build.VERSION.RELEASE);
            //应用标识
            jsonObject.put("bundleId", "com.wolaibao.agent");
            //渠道
            jsonObject.put("pid", "");
            // 平台
            jsonObject.put("platform", "android");
            //设备id
//            String deviceId = DeviceUtil.getDeviceId(DataConfig.getContext());
//            jsonObject.put("deviceId", deviceId);

            //用户token
            String token = null;
            UserResponse loginedUser = UserClient.getLoginedUser();
            if (null != loginedUser) {
                token = loginedUser.token;
            }
            jsonObject.put("token", token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static String constructGetUrl(String url) {
        return HttpUrl.parse(url).newBuilder()
                // versionCode
                .addQueryParameter("appVersion", String.valueOf(DataConfig.versionCode))
                // versionName
                .addQueryParameter("versionName", DataConfig.versionName)
                // APP ID
                .addQueryParameter("appId", BuildConfig.appId)
                //系统版本号
                .addQueryParameter("systemVersion", Build.VERSION.RELEASE)
                //应用标识
                .addQueryParameter("bundleId", "com.wolaibao.agent")
                //渠道
                .addQueryParameter("pid", "")
                // 平台
                .addQueryParameter("platform", "android")
                //设备id
//                .addQueryParameter("deviceId", DeviceUtil.getDeviceId(DataConfig.getContext()))
                .build()
                .toString();
    }

    public static byte[] encrypt(String data) throws Exception {
        byte[] postData = data.getBytes("UTF-8");
        byte[] entryptData = Des3Encoder.encrypt(encryptKey, postData);
        return entryptData;
    }

    public static RequestBody toRequestBody(JSONObject jsonObject) {
        try {
            String postJson = jsonObject.toString();
            byte[] entryptData = encrypt(postJson);
            return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), entryptData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
