package component.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

/**
 * 版本升级信息数据库
 * 
 * @author 张全
 */
public class AppVersionDB {
	private static final String DB = "AppVersion";

	public interface AppVersionColumn {
		/**
		 * app版本号
		 */
		String versionCode = "versionCode";
		/**
		 * app版本名称
		 */
		String versionName = "versionName";
		/**
		 * 升级描述
		 */
		String desc = "desc";
		/**
		 * 下载url
		 */
		String downloadUrl = "downloadUrl";
		/**
		 * 下载总大小
		 */
		String totalSize = "totalSize";
	}

	private static SharedPreferences getSP() {
		return AppDownloadClient.getContext().getSharedPreferences(DB, Context.MODE_APPEND);
	}

	public static void saveVersion(AppVersion version) {
		SharedPreferences pref = getSP();
		Editor edit = pref.edit();
		if (null == version) {
			edit.clear();
		} else {
			edit.putInt(AppVersionColumn.versionCode, null != version ? version.versionCode : 0);
			edit.putString(AppVersionColumn.versionName, null != version ? version.versionName : null);
			edit.putString(AppVersionColumn.desc, null != version ? version.desc : null);
			edit.putString(AppVersionColumn.downloadUrl, null != version ? version.downloadUrl : null);
			edit.putLong(AppVersionColumn.totalSize, null != version ? version.totalSize : 0);
		}
		edit.commit();
	}

	public static AppVersion getVersion() {
		SharedPreferences pref = getSP();
		String desc = pref.getString(AppVersionColumn.desc, null);
		if (TextUtils.isEmpty(desc)) {
			return null;
		}
		AppVersion appVersion = new AppVersion();
		appVersion.versionCode = pref.getInt(AppVersionColumn.versionCode, 0);
		appVersion.versionName = pref.getString(AppVersionColumn.versionName, null);
		appVersion.desc = pref.getString(AppVersionColumn.desc, null);
		appVersion.downloadUrl = pref.getString(AppVersionColumn.downloadUrl, null);
		appVersion.totalSize = pref.getLong(AppVersionColumn.totalSize, 0);

		return appVersion;
	}

}
