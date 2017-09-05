package com.android.util.device;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 手机工具类
 *
 * @author 张全
 */
public class DeviceUtil {
    public static int screenHeight = -1;
    public static int screenWidth = -10;
    public static float screenDensity = -1;
    private static int screenHeightWithoutStatusBar = -1;

    private DeviceUtil() {

    }

    /**
     * 获取设备机型.
     *
     * @return 设备机型字符串
     */
    public static String getDeviceType() {
        return Build.MODEL;
    }

    /**
     * 获取系统版本.
     *
     * @return 当前版本
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取应用版本名称.
     *
     * @param context 上下文
     * @return 应用版本名
     */
    public static String getAppVersionName(Context context) {
        PackageInfo pi = getPackageInfo(context);
        if (null != pi) {
            return pi.versionName;
        }
        return null;
    }

    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String metaValue = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                metaValue = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return metaValue;
    }

    /**
     * 获取版本号。
     *
     * @param context 上下文
     * @return 应用版本号
     */
    public static int getAppVersionCode(Context context) {
        PackageInfo pi = getPackageInfo(context);
        if (null != pi) {
            return pi.versionCode;
        }
        return -1;
    }

    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return pi;
    }

    /**
     * 获取设备号，imei号。
     *
     * @param context 上下文
     * @return 设备ID
     */
    public static String getDeviceId(Context context) {
        try {
            TelephonyManager ty =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = ty.getDeviceId();
            if (!TextUtils.isEmpty(deviceId))
                return deviceId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String deviceId = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            if (!TextUtils.isEmpty(deviceId))
                return deviceId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取显示矩阵。
     *
     * @param context 上下文
     * @return 返回矩阵
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 获取屏幕宽度。
     *
     * @param context 上下文
     * @return 宽度
     */
    public static int getScreenWidthPx(Context context) {
        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        return screenWidth;
    }

    /**
     * 获取屏幕高度。
     *
     * @param context 上下文
     * @return 屏幕高度
     */
    public static int getScreenHeightPx(Context context) {
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
        return screenHeight;
    }

    /**
     * 获取屏幕状态栏高度
     *
     * @param act
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    /**
     * 获取屏幕高度(去掉状态栏高度)
     *
     * @param act
     * @return
     */
    public static int getScreenHeightWithoutStausBar(Activity act) {
        if (screenHeightWithoutStatusBar > 0) {
            return screenHeightWithoutStatusBar;
        }
        screenHeightWithoutStatusBar = screenHeight - getStatusBarHeight(act);
        return screenHeightWithoutStatusBar;
    }

    /**
     * dip转px。
     *
     * @param context 上下文
     * @param dip     dip
     * @return px
     */
    public static int dip2px(Context context, float dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
                context.getResources().getDisplayMetrics());
    }

    /**
     * px转dip
     *
     * @param context
     * @param px
     * @return
     */
    public static int px2dip(Context context, int px) {
        return Math.round(px
                / context.getResources().getDisplayMetrics().density);
    }

    /**
     * 是否安装了某个app
     *
     * @param context
     * @param pkg     target app的包名
     * @return
     */
    public static boolean isAppInstalled(Context context, String pkg) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            ApplicationInfo applicationInfo = resolveInfo.activityInfo.applicationInfo;
            String packageName = applicationInfo.packageName;
            if (packageName.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 直接调用短信接口发短信
     *
     * @param message
     */
    public static void sendSMS(Context context, String message) {
        Uri smsToUri = Uri.parse("smsto:");
        Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
        //短信内容
        intent.putExtra("sms_body", message);
        context.startActivity(intent);
    }
}
