package com.wlb.agent.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * App综合提供类
 *
 * @author Berfy
 * @date 2017.7.3
 */
public class AppInfoUtil {


    /**
     * 获取Application中的meta-data.
     *
     * @param context 上下文
     * @param metaKey meta-data key
     * @return
     */
    public static String getApplicationMetaData(Context context, String metaKey) {
        try {
            Bundle bundle = getAppMetaDataBundle(context.getPackageManager(), context.getPackageName());
            String msg = bundle.getString(metaKey);
            return msg;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 获取Application中的meta-data.
     *
     * @param packageManager
     * @param packageName
     * @return
     */
    private static Bundle getAppMetaDataBundle(PackageManager packageManager,
                                               String packageName) {
        Bundle bundle = null;
        try {
            ApplicationInfo ai = packageManager.getApplicationInfo(packageName,
                    PackageManager.GET_META_DATA);
            bundle = ai.metaData;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return bundle;
    }
}
