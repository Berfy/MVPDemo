package com.wlb.agent.core.data;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * @author 张全
 */

public class DataConfig {

    public static String versionName;
    public static int versionCode;
    public static String appName;
    public static String apiHost; // 接口域名
    public static String h5Host;//h5地址
    public static String h5SharePage;//h5分享页面
    public static String companyIconHost;//保险公司图片地址
    public static String umeng_appKey = null;
    public static String umeng_channel = null;
    private static Context context;

    public static void init(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        int versionCode = 0;
        String versionName = null;
        String appName = null;
        String umeng_appKey = null;
        String umeng_channel = null;

        try {
            ApplicationInfo info = pm.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            PackageInfo packInfo = pm.getPackageInfo(ctx.getPackageName(), 0);
            appName = pm.getApplicationLabel(info).toString();
            if (info != null && info.metaData != null) {
                umeng_appKey = info.metaData.getString("UMENG_APPKEY");
                umeng_channel = info.metaData.getString("UMENG_CHANNEL");
            }
            if (packInfo != null) {
                versionName = packInfo.versionName;
                versionCode = packInfo.versionCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DataConfig.apiHost = BuildConfig.apiHost;
        DataConfig.h5Host = BuildConfig.h5Host;
        DataConfig.h5SharePage = BuildConfig.h5SharePage;
        DataConfig.companyIconHost = BuildConfig.companyIconHost;
        DataConfig.context = ctx.getApplicationContext();
        DataConfig.versionCode = versionCode;
        DataConfig.versionName = versionName;
        DataConfig.umeng_appKey = umeng_appKey;
        DataConfig.umeng_channel = umeng_channel;
        DataConfig.appName = appName;
    }

    public static Context getContext() {
        return context;
    }
}
