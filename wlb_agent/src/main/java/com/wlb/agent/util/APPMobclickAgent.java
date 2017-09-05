package com.wlb.agent.util;

import android.text.TextUtils;

import com.android.util.LContext;
import com.umeng.analytics.MobclickAgent;

import java.util.Map;

/**
 * 事件统计
 * 
 * @author 张全
 */
public class APPMobclickAgent {

	/**
	 * 计数事件
	 * 
	 * @param eventId
	 *            事件id
	 */
	public static void onEvent(String eventId) {
		// if (!BBContext.isDebug && !TextUtils.isEmpty(eventId)) {
		if (!TextUtils.isEmpty(eventId)) {
			MobclickAgent.onEvent(LContext.getContext(), eventId);
		}
	}

	/**
	 * 计数事件
	 * 
	 * @param eventId
	 *            事件id
	 * @param values
	 *            事件属性
	 */
	public static void onEvent(String eventId, Map<String, String> values) {
		// if (!BBContext.isDebug && !TextUtils.isEmpty(eventId)) {
		if (!TextUtils.isEmpty(eventId)) {
			MobclickAgent.onEvent(LContext.getContext(), eventId, values);
		}
	}
}
