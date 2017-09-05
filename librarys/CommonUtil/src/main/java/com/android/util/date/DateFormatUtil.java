package com.android.util.date;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 日期格式化工具类
 * 
 * @author chen:
 * 
 */
public class DateFormatUtil {
	public static SimpleDateFormat yyyy__MM__dd() {
		return new SimpleDateFormat("yyyy_MM_dd", Locale.getDefault());
	}

	public static SimpleDateFormat Z() {
		return new SimpleDateFormat("Z", Locale.getDefault());
	}

	public static SimpleDateFormat yyyy_MM_dd_HH_mm_ss_SSS() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
	}

	public static SimpleDateFormat yyyyMMdd() {
		return new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
	}

	public static SimpleDateFormat yyyyMMddHHmmss() {
		return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
	}
	
	public static SimpleDateFormat yyyy_MM_dd_HH_mm_ss() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
	}
	
	public static SimpleDateFormat yyyy_MM_dd() {
		return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	}


	public static SimpleDateFormat HH_mm_ss() {
		return new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
	}

	public static SimpleDateFormat HHmmss() {
		return new SimpleDateFormat("HHmmss", Locale.getDefault());
	}

	public static SimpleDateFormat HH_mm() {
		return new SimpleDateFormat("HH:mm", Locale.getDefault());
	}

	public static SimpleDateFormat mm_ss() {
		return new SimpleDateFormat("mm:ss", Locale.getDefault());
	}

	public static SimpleDateFormat MMMM_dd_yyyy() {
		return new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
	}
}
