package com.wlb.common.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

/**
 * Receiver基类
 * 
 * @author zhangquan
 */
public abstract class BaseReceiver extends BroadcastReceiver {
	protected abstract IntentFilter getIntentFilter();

	public  BaseReceiver register(Context context) {
		context.registerReceiver(this, getIntentFilter());
		return this;
	}

	public final void unregister(Context ctx) {
		try {
			ctx.unregisterReceiver(this);
			return;
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
