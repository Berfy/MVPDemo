package com.android.util.http.callback;

import com.android.util.http.exception.NetException;

/**
 * 回调基类，调用者可选择实现回调方法。
 * 
 * @author 张全
 */
public class SimpleCallback implements ICallback {

	@Override
	public void start() {
	}

	@Override
	public void success(Object data) {
	}

	@Override
	public void failure(NetException e) {
	}

	@Override
	public void end() {
	}
};