package component.update;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 * 版本下载工具类
 * 
 * @author zhangquan
 * 
 */
public class AppDownloadUtil {
	private static final String TAG = "AppVersionDownloader";

	/**
	 * 网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connManager.getActiveNetworkInfo();
		if (info != null && info.isConnected() && info.isAvailable()) {
			return true;
		}
		return false;
	}

	public static void showToast(String msg) {
		Toast.makeText(AppDownloadClient.getContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public static void d(String msg) {
		if (AppDownloadClient.getConfiguration().isDebug) {
			Log.d(TAG, msg);
		}

	}

	public static void d(String tag, String msg) {
		if (AppDownloadClient.getConfiguration().isDebug) {
			Log.d(tag, msg);
		}
	}
}
