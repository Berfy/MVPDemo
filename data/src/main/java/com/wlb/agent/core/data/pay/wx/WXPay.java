package com.wlb.agent.core.data.pay.wx;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.android.util.ext.ToastUtil;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wlb.agent.core.data.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * 微信支付
 * 
 * @author 张全
 */
public class WXPay {
	private IWXAPI api;
	public static WXPayType payType = WXPayType.INSURANCE;

	/**
	 * 购买商品类型
	 */
	public enum WXPayType {
		/**
		 * 创新险
		 */
		INNOVATION,
		/**
		 * 车险
		 */
		INSURANCE;
	}

	public WXPay(Context ctx) {
		// 注册微信支付
		api = WXAPIFactory.createWXAPI(ctx, null);
	}

	/**
	 * 检测支付环境
	 * 
	 * @return
	 */
	public boolean checkEnvirment() {
		// 检测是否安装了微信客户端
		if (!WXHelper.isWeiXinInstalled()) {
			unregisterApp();
			ToastUtil.show(R.string.share_alert_install_wx);
			return false;
		}
		// 检查微信版本是否支持支付
		boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
		if (!isPaySupported) {
			ToastUtil.show(R.string.wx_pay_notsupppot);
			unregisterApp();
			return false;
		}
		return true;
	}

	/**
	 * 支付
	 */
	public void pay(WXPayParter wxParter, String prepay_id) {
		if (TextUtils.isEmpty(prepay_id)) {
			ToastUtil.show("请求支付失败,prepay_id=null");
			return;
		}
		api.registerApp(wxParter.appId);
		PayReq req = new PayReq();
		req.appId = wxParter.appId;
		req.partnerId = wxParter.parterId;
		req.prepayId = prepay_id;
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<>();
		signParams.add(new NameValuePair("appid", req.appId));
		signParams.add(new NameValuePair("noncestr", req.nonceStr));
		signParams.add(new NameValuePair("package", req.packageValue));
		signParams.add(new NameValuePair("partnerid", req.partnerId));
		signParams.add(new NameValuePair("prepayid", req.prepayId));
		signParams.add(new NameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(wxParter, signParams);
		api.sendReq(req);
	}

	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 生成APP签名
	 * 
	 * @param params
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private String genAppSign(WXPayParter wxParter, List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(wxParter.apiKey);

		String appSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();
		return appSign;
	}

	public void unregisterApp() {
		try {
			api.unregisterApp();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public class NameValuePair{
		private String name;
		private String value;
		public NameValuePair(String name,String value){
			this.name=name;
			this.value=value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}
	}

}
