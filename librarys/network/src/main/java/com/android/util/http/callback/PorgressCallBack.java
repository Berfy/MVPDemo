package com.android.util.http.callback;

/**
 * 下载进度回调
 * @author 张全
 */
public interface PorgressCallBack {
	/**
	 * 下载进度
	 * 
	 * @param percent
	 * @param curProgress
	 * @param total
	 */
	void progress(int percent, long curProgress, long total);
}
