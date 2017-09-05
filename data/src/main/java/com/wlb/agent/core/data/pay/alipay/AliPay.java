package com.wlb.agent.core.data.pay.alipay;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.alipay.sdk.app.PayTask;
import com.android.util.log.LogUtil;
import com.wlb.agent.core.data.DataConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 支付宝支付
 * 
 * @author zhangquan
 */
public class AliPay {
	private static final int SDK_PAY_FLAG = 1;
	private AliPayCallBack callBack;
	private boolean isCanceled;
	public static final String TAG = "Alipay";

	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {
			if (isCanceled)
				return;
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				LogUtil.d(TAG, (String) msg.obj);
				PayResult payResult = new PayResult((String) msg.obj);
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				// String resultInfo = payResult.getResult();
				if (null != callBack)
					callBack.end(payResult);

				break;
			}
			default:
				break;
			}
		};
	};

	/**
	 * 调用支付宝支付
	 * 
	 * @param subject
	 *            商品名称
	 * @param body
	 *            商品描述
	 * @param orderId
	 *            订单id
	 * @param price
	 *            支付金额
	 * @param callBack
	 *            支付回调
	 * @return 支付结果
	 */
	public void pay(final Activity ctx, String subject, String body,
			String orderId, String price, AliPayParter aliPayParter,
			AliPayCallBack callBack) {
		this.callBack = callBack;
		this.isCanceled = false;
		if (null != callBack)
			callBack.start();

		// 订单
		String orderInfo = getOrderInfo(subject, body, orderId, price,
				aliPayParter);

		// 对订单做RSA 签名
		String sign = sign(orderInfo, aliPayParter.rsa);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				if (isCanceled) {
					return;
				}
				// 构造PayTask 对象
				PayTask alipay = new PayTask(ctx);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);
				if (isCanceled) {
					return;
				}
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	public void stop() {
		this.callBack = null;
		this.isCanceled = true;
	}

	/**
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 * @note 必须异步调用
	 */
	public boolean checkAccountIfExist(Activity ctx) {
		// 构造PayTask 对象
		PayTask payTask = new PayTask(ctx);
		// 调用查询接口，获取查询结果
		return payTask.checkAccountIfExist();
	}

	/**
	 * 获取SDK版本号
	 */
	public String getSDKVersion(Activity ctx) {
		PayTask payTask = new PayTask(ctx);
		return payTask.getVersion();
	}

	/**
	 * 创建订单信息
	 * 
	 * @param subject
	 *            商品名称
	 * @param body
	 *            商品描述
	 * @param orderId
	 *            订单id
	 * @param price
	 *            价格
	 * @return
	 */
	public String getOrderInfo(String subject, String body, String orderId,
			String price, AliPayParter aliPayParter) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + aliPayParter.pid + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + aliPayParter.seller + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + orderId + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + DataConfig.apiHost + aliPayParter.callback
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content, String key) {
		return SignUtils.sign(content, key);
	}

	/**
	 * 获取签名方式
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

	public static interface AliPayCallBack {
		void start();

		void end(PayResult payResult);
	}
}
