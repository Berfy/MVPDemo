package com.android.util.http.response;

import android.text.TextUtils;

import com.google.gson.Gson;

/**
 * Gson对象解析
 * @author 张全
 */
public class GsonParser<T> implements DataParser {
	private final Gson mGson = new Gson();
	private final Class<T> mClazz;//

	public GsonParser(Class<T> mParseCls) {
		this.mClazz = mParseCls;
	}

	@Override
	public Object parseReponse(String response) throws Exception {
		String data = response;
		if (TextUtils.isEmpty(data)) { throw new NullPointerException("response data===null"); }
		return mGson.fromJson(data, mClazz);
	}
}
