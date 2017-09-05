package com.wlb.agent.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import com.android.util.encode.HEX;
import com.android.util.encode.MD5;
import com.wlb.agent.BuildConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 二次打包校验
 * @author 张全
 */

public class ApkDefender {
    public static boolean checkInvalidate(final Context ctx) {
        //一、判断线上版本是否运行在模拟器上
        if (!BuildConfig.DEBUG&&isEmulator()) {
            return false;
        }
        //二、应用完整性校验(建议放在服务器端校验)
        //1、包名校验
        if (!"com.wlb.agent".equals(ctx.getPackageName())) {
            return false;
        }
        //2、检测签名
        String signature = getSignature(ctx);
        if (!"10e3b2e4f97994010afb4f0ed530f042".equals(signature)) {
            return false;
        }

        //3、校验classes.dex是否被修改（每次发版，classes.dex的Crc值都不一样，应该和版本号对应。）
       /* long dexCrc = getDexCrc(ctx);
        System.out.println("dexCrc=" + dexCrc);
        if (dexCrc > 0 && dexCrc != 1276560181L) {
            return false;
        }*/

        /**
         4、校验整个apk是否被修改（每次发版，该apk的SHA1值都不一样，应该和版本号对应。）
         */
      /*  String sha1 = getSha1(ctx);
        System.out.println("sha1="+sha1);
        if(!TextUtils.isEmpty(sha1)&&!"bfa2cb2d9160806efaea6adb505e379ac1057a49".equals(sha1)){
          return false;
        }*/
        return true;
    }

    /**
     * 判断应用程序是否运行在模拟器上
     *
     * @return
     */
    private static boolean isEmulator() {
        //只要是在模拟器中，不管是什么版本的模拟器，在它的MODEL信息里就会带有关键字参数sdk
        if (Build.MODEL.contains("sdk")||Build.MODEL.contains("SDK")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得应用程序的数字签名
     *
     * @return
     */
    private static String getSignature(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            // 得到当前应用程序的签名
            PackageInfo info = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            String signature = info.signatures[0].toCharsString();//原始md5值，比较长
            signature = MD5.MD5Encode(signature);
            return signature;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static long getDexCrc(Context ctx) {
        try {
            String apkPath = getApkPath(ctx);
            if (TextUtils.isEmpty(apkPath)) return 0;
            ZipFile zipfile = new ZipFile(apkPath);
            Enumeration<? extends ZipEntry> entries = zipfile.entries();
            ZipEntry dexentry = zipfile.getEntry("classes.dex");
            return dexentry.getCrc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取Apk的SHA1值
     * 每次发版，该apk的SHA1值都不一样，应该和版本号对应。
     *
     * @return
     */
    private static String getSha1(Context ctx) {
        String apkPath = getApkPath(ctx);
        if (TextUtils.isEmpty(apkPath)) return null;
        FileInputStream fis = null;
        try {
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = new byte[1024];
            int byteCount;
            fis = new FileInputStream(new File(apkPath));
            while ((byteCount = fis.read(bytes)) > 0) {
                msgDigest.update(bytes, 0, byteCount);
            }
            byte[] digest = msgDigest.digest();
            String sha = HEX.byteToString(digest);
            return sha;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != fis) try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String getApkPath(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pkgInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            return appInfo.sourceDir;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
