package com.android.util.http;


import android.util.Log;

/**
 * 网络请求日志
 *
 * @author 张全
 */
public class NetLog {
    private static final String TAG = "NetClient";

    public static void d(String msg) {
        Log.d(TAG, msg);
    }
}
