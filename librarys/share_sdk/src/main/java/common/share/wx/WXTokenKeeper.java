package common.share.wx;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class WXTokenKeeper {
	private static final String PREFERENCES_NAME = "com_weixin_sdk_android";

	private static final String KEY_UID = "uid";
	private static final String KEY_ACCESS_TOKEN = "access_token";

	public static void saveAccessToken(Context context, String uid, String accessToken) {
		if (null == context || null == accessToken) {
			return;
		}
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		Editor editor = pref.edit();
		editor.putString(KEY_UID, uid);
		editor.putString(KEY_ACCESS_TOKEN, accessToken);
		editor.commit();
	}

	public static String getUid(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		return pref.getString(KEY_UID, "");
	}

	public static String getAccessToken(Context context) {
		SharedPreferences pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_APPEND);
		return pref.getString(KEY_ACCESS_TOKEN, "");

	}
}
