package com.wlb.agent.core.data.insurance.entity;

/**
 * 支付平台
 * 
 * @author 张全
 */
public enum PayPlatform {
	/**
	 * 线下支付
	 */
     Offline(0),
	/**
	 * 微信支付
	 */
	WeiXin(1),
	/**
	 * 支付宝支付
	 */
	Alipay(2);

	public int code;

	private PayPlatform(int value) {
		this.code = value;
	}

}
