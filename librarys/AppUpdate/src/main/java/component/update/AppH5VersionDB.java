package component.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * h5页面版本升级信息数据库
 * 
 * @author 张全
 */
public class AppH5VersionDB {
	private static final String DB = "AppH5Version";

	public interface AppVersionColumn {
		/**
		 * app版本号
		 */
		String versionCode = "versionCode";
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

	public static void saveVersion(AppH5Version version) {
		SharedPreferences pref = getSP();
		Editor edit = pref.edit();
		if (null == version) {
			edit.clear();
		} else {
			edit.putInt(AppVersionColumn.versionCode, null != version ? version.versionCode : 0);
			edit.putString(AppVersionColumn.downloadUrl, null != version ? version.url : null);
			edit.putLong(AppVersionColumn.totalSize, null != version ? version.totalSize : 0);
		}
		edit.commit();
	}

	public static AppH5Version getVersion() {
		SharedPreferences pref = getSP();
		AppH5Version appVersion = new AppH5Version();
		appVersion.versionCode = pref.getInt(AppVersionColumn.versionCode, 0);
		appVersion.url = pref.getString(AppVersionColumn.downloadUrl, null);
		appVersion.totalSize = pref.getLong(AppVersionColumn.totalSize, 0);

		return appVersion;
	}

}
