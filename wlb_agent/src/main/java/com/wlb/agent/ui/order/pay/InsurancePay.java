package com.wlb.agent.ui.order.pay;

import android.app.Activity;

import com.android.util.LContext;
import com.wlb.agent.R;
import com.wlb.agent.core.data.pay.alipay.AliPay;
import com.wlb.agent.core.data.pay.alipay.AliPayParter;
import com.wlb.agent.core.data.pay.alipay.PayResult;
import com.wlb.agent.core.data.pay.wx.WXPay;
import com.wlb.agent.core.data.pay.wx.WXPayParter;

/**
 * 车险支付生成prepayId
 *
 * @author 张全
 */
public class InsurancePay {
    private Activity ctx;
    private boolean isDestryod;
    private AliPay.AliPayCallBack mAliPayCallback;
    public static String WX_APPID;
    private static AliPayParter aliPayParter;
    private static WXPayParter wxPayParter;

    static {
        WX_APPID = LContext.getContext().getString(R.string.wx_appId);
        //阿里支付
        aliPayParter = new AliPayParter(
                "2088221726801502",//
                "wolaibao@wolaibao.com",//
                "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAN2++PyBMzM+YAeCllp4h7HSsvxA5yWGPtPGP5OEgaDYdlMXJW2jbekjiCZSmezKDCQmTXX/vBiMZgD8u4zGQnMn2YhMnib+lMfhd0eCiw9ULouN6QEWakFYk8YVak6eleFWzqT1N0gWv2PxkDRI7rvjyMEOYDsEvpSzDWlvCvXXAgMBAAECgYEAp+rh6XR9LSTi200MRl5Xg1UmrNcJMuVB1mSd6DvDXeYNVEaG1UuZA58gjsSmQyWNpCJNLjoGWiCXbc/0xlmsHtb1LbHBO8idZ1BUtl/icQPwv4hYYdbBqDcY28PaeK7EXEGnEGZbpiv4qJuzlK7KzXc8+d6WhciWyCBSBsLGqKECQQDzRk7KHmxppeqjIrU62KM4vx33hKLfPgtdm3FdwvObEHghas+RH4fY44fqY47kmc5VBDlLBt9Z+L2beDgblGgtAkEA6Vhf33qlIk7KMJaa9rTKXpFPnzlA28TSVlRnTfUURpMd3X6lg48CigClY3zd/3+Yw5j3np6PmzEMWhLQcpI0kwJAMpsgjrkPcla6XZ89tfUU1xwinrevreLZOGq6hXeld09Qvhra/ORjPQHv5xk4w2MfYd4UEQBn++5bevjiKxKPLQJAfQm8JhakUmQB+FNbqoNqRY58KggV9y3awCPuT1naiY7f3Aa3Tm3doHGELnGEzBplk7puRfMVZMW9pq6aQCqUPQJATeXE/DqwI+BY5x/dZFhdYHn4n1vLRCTihsf9bsVBUYwtzKFtWXJvvesfFvwAxJTMgRH2FfzsvnOBai+x9rztUA==",//
                "alipayment/orderCallback.json");
        //微信支付
        wxPayParter = new WXPayParter(WX_APPID, "1425408102",
                "wlb1608zhongbao1608hurongwxp1223");
    }

    public InsurancePay(Activity ctx) {
        this.ctx = ctx;
    }

    public void openAlipayClient(PayInfo payInfo, AliPay.AliPayCallBack callBack) {
        this.mAliPayCallback = callBack;
        AliPay aliPay = new AliPay();
        aliPay.pay(ctx, payInfo.subject, payInfo.desc, payInfo.prepayId,
                String.valueOf(payInfo.price), aliPayParter,
                new AliPay.AliPayCallBack() {

                    @Override
                    public void start() {
                        if (null != mAliPayCallback) mAliPayCallback.start();
                    }

                    @Override
                    public void end(PayResult payResult) {
                        if (isDestryod)
                            return;
                        if (null != mAliPayCallback) {
                            mAliPayCallback.end(payResult);
                        }
                    }
                });
    }

    public void openWxPayClient(String prepayId) {
        try {
            WXPay.payType = WXPay.WXPayType.INSURANCE;
            WXPay wxPay = new WXPay(ctx);
            wxPay.pay(wxPayParter, prepayId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        isDestryod = true;
        mAliPayCallback = null;
    }
}
