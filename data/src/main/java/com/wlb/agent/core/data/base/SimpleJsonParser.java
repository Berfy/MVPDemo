package com.wlb.agent.core.data.base;

import com.android.util.http.response.JsonObjectParser;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * 简单的接口响应信息处理，比如调用者只想知道接口请求成功还是失败
 * @author 张全
 */
public class SimpleJsonParser extends JsonObjectParser {

	@Override
	public Object parseData(JSONObject jsonObject) throws Exception {
		BaseResponse response = new Gson().fromJson(jsonObject.toString(),BaseResponse.class);
		return response;
	}
}
