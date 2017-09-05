package com.android.util.http.response;

/**
 * 字符串解析
 * 
 * @author 张全
 */
public class StringParser implements DataParser {

	@Override
	public Object parseReponse(String response) throws Exception {
		return response;
	}

}
