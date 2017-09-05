package com.wlb.agent.core.data.pay.alipay;

public class AliPayParter {
	/**
	 * 回调地址
	 */
	public String callback;
	/**
	 * 商户PID
	 */
	public String pid;
	/**
	 * 商户收款账号
	 */
	public String seller;
	/**
	 * 商户私钥，pkcs8格式
	 */
	public String rsa;

	public AliPayParter(String pid, String seller, String rsa, String callback) {
		this.pid = pid;
		this.seller = seller;
		this.rsa = rsa;
		this.callback = callback;
	}

}
