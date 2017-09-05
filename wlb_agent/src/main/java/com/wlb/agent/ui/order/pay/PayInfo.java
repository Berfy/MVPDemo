package com.wlb.agent.ui.order.pay;

import java.io.Serializable;

/**
 * 支付信息
 * 
 * @author 张全
 */
public class PayInfo implements Serializable {
	private static final long serialVersionUID = 6029596789705141399L;
	public long orderId;// 订单id
	public String licenseNo;//
	public String companyCode;// 保险公司代码
	public String price;// 价格
	public String payUrl;// 支付页面URL
	public String tradeNo;// 交易号
	public String orderNo;//订单号
	public int payWay;// 支付方式
	public long expirePayTime;

	public boolean needPay;// 是否立即支付

	public String prepayId; //支付id
	public String subject;//商品名称
	public String desc;//商品描述

	public boolean isWxPay;

	@Override
	public String toString() {
		return "PayInfo [orderId=" + orderId + ", licenseNo=" + licenseNo + ", companyCode=" + companyCode + ", price="
				+ price + ", payUrl=" + payUrl + ", tradeNo=" + tradeNo + ", payWay=" + payWay + ", offlinePaying=" + needPay
				+ "]";
	}

}
