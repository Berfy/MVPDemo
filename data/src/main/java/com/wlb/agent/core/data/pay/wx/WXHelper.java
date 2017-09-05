package com.wlb.agent.core.data.pay.wx;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.wlb.agent.core.data.DataConfig;

import java.util.List;

/**
 * 微信工具类
 * 
 * @author 张全
 */
public class WXHelper {

	/**
	 * 检测是否安装了微信
	 * 
	 * @return
	 */
	public static boolean isWeiXinInstalled() {
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		PackageManager pm = DataConfig.getContext().getPackageManager();
		List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
		for (ResolveInfo resolveInfo : resolveInfos) {
			ApplicationInfo applicationInfo = resolveInfo.activityInfo.applicationInfo;
			String packageName = applicationInfo.packageName;
			if (packageName.startsWith("com.tencent.mm")) {
				return true;
			}
		}
		return false;
	}
}
