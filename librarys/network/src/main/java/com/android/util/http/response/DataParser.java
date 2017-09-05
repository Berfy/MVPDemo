package com.android.util.http.response;

/**
 * 数据解析
 *
 * @author 张全
 */
public interface DataParser<T> {
    T parseReponse(String response) throws Exception;
}
