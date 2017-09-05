package com.android.util.db;

import org.json.JSONException;

/**
 * @author 张全
 */
public interface DBModel<T> {
	/**
	 * 获取uid
	 * @return
	 */
	String getUid();

	/**
	 * 获取该条数据在数据库中唯一key
	 * @return
	 */
	String getKey();

    /**
     * 获取该条数据的储存值
     * @return
     */
	String getModelName();

	/**
	 * 转换为数据库储存值
	 * @return
	 */
	String toDBValue();

	/**
	 * 将数据库储存值转换为业务bean
	 * @param dbValue
	 * @return
	 */
	T fromDBValue(String dbValue) throws JSONException;
}
