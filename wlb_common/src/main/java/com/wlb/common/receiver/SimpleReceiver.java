package com.wlb.common.receiver;

import android.content.IntentFilter;

/**
 * 监听一个action的广播
 * @author 张全
 */
public abstract class SimpleReceiver extends BaseReceiver {
	protected String action;

	public SimpleReceiver(String action) {
		this.action = action;
	}

	@Override
	protected IntentFilter getIntentFilter() {
		return new IntentFilter(action);
	}
}
