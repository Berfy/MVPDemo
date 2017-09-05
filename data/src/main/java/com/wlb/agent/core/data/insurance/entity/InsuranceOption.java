package com.wlb.agent.core.data.insurance.entity;

import java.io.Serializable;

/**
 * 险种保额
 * 
 * @author 张全
 */
public class InsuranceOption implements Serializable{
	/**
	 * 保额的值,比如10000
	 */
	public int key;
	/**
	 * 保额显示的值,比如10万
	 */
	public String value;

	@Override
	public String toString() {
		return "InsuranceOption [key=" + key + ", value=" + value + "]";
	}

}
