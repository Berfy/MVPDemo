package com.android.util.http.response;

import android.text.TextUtils;

import org.json.JSONObject;

/**
 * @author 张全
 */
public abstract class JsonObjectParser implements DataParser {

	@Override
	public Object parseReponse(String response) throws Exception {
		String data = response;
		if (TextUtils.isEmpty(data)) throw new NullPointerException("response data===null");

		return parseData(new JSONObject(data));
	}

	public abstract Object parseData(JSONObject dataJson) throws Exception;

}
