package com.android.util.http.response;

import android.text.TextUtils;

import org.json.JSONArray;

/**
 * JsonArray解析
 * 
 * @author 张全
 */
public abstract class JsonArrayParser implements DataParser {

	@Override
	public Object parseReponse(String response) throws Exception {
		String data =response;
		if (TextUtils.isEmpty(data)) {
			throw new NullPointerException("response data===null");
		}
		return parseData(new JSONArray(data));
	}

	public abstract Object parseData(JSONArray dataArray) throws Exception;

}
