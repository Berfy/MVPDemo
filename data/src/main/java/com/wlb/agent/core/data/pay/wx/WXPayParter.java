package com.wlb.agent.core.data.pay.wx;

public class WXPayParter {
	/**
	 * appId
	 */
	public String appId;
	/**
	 * 支付商户号
	 */
	public String parterId;
	/**
	 * ApiKey
	 */
	public String apiKey;

	public WXPayParter(String appId, String parterId, String apiKey) {
		this.appId = appId;
		this.parterId = parterId;
		this.apiKey = apiKey;
	}

}
