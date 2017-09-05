package com.android.util;

import android.content.Context;
import android.util.DisplayMetrics;

import com.android.util.log.LogUtil;

/**
 * @author 张全
 */
public class LContext {
	private static Context ctx;
	public static int appIcon;
	public static String appName;
	public static String pkgName;
	public static String versionName;
	public static int versionCode;
	public static int screenWidth;
	public static int screenHeight;
	public static float density;
	public static boolean isDebug;
	public static boolean isTranslucent;//沉浸式布局

	public LContext() {

	}

	/**
	 * 初始化
	 * 
	 * @param ctx
	 */
	public static void init(Context ctx, boolean debug) {
		LContext.ctx = ctx;
		LContext.isDebug = debug;
		LogUtil.openLog(debug);
		DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
		density = dm.density;
	}

	public static Context getContext() {
		if (null == ctx) {
			throw new NullPointerException("ctx=null,请在你应用中的Application中完成初始化");
		}
		return ctx;
	}

	public static String getString(int resId) {
		return getContext().getString(resId);
	}

	public static int getColor(int resId) {
		return getContext().getResources().getColor(resId);
	}

}
