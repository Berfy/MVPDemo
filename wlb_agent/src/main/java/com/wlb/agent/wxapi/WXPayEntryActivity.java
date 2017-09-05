package com.wlb.agent.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.android.util.LContext;
import com.android.util.log.LogUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.order.pay.InsurancePay;
import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;

/**
 * 微信支付回调页面
 * @author 张全
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";
    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wx_pay_result);
        api = WXAPIFactory.createWXAPI(this, InsurancePay.WX_APPID);
        api.handleIntent(getIntent(), this);
        LogUtil.d(TAG, "WXPayEntryActivity............onCreate");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        LogUtil.d(TAG, "WXPayEntryActivity............onNewIntent");
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (null == resp) {
            finish();
            return;
        }

        LogUtil.d(TAG, "WXPayEntryActivity,onResp...onPayFinish, errCode = "
                + resp.errCode + ",type=" + resp.getType());

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            switch (resp.errCode) {
                case 0:// 成功
                    LogUtil.d(TAG, "WXPayEntryActivity.....onResp.......车险支付");
                    EventBus.getDefault().post(new PushEvent(Constant.IntentAction.ORDER_BUY_SUCCESS));
                    finish();
                    break;
                case -1:// 错误
                /*
                 * 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
				 */
                    EventBus.getDefault().post(new PushEvent(Constant.IntentAction.ORDER_BUY_FAIL));
                    Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case -2:// 用户取消
                /*
                 * 无需处理。发生场景：用户不支付了，点击取消，返回APP。
				 */
                    EventBus.getDefault().post(new PushEvent(Constant.IntentAction.ORDER_BUY_CANCEL));
                    Toast.makeText(this, "取消支付", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    finish();
                    break;
            }
        }
    }

    public static void printInfo(BaseResp baseResp) {
        if (!LContext.isDebug || null == baseResp) {
            return;
        }
        LogUtil.d(TAG, "---------微信支付--------start");
        Field[] fields = baseResp.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(baseResp);
                LogUtil.d(TAG, field.getName() + "=" + value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        LogUtil.d(TAG, "---------微信支付--------end");
    }

}