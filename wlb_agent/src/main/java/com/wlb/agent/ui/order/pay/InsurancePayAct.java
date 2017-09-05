package com.wlb.agent.ui.order.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.util.ext.ToastUtil;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.common.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 微信支付
 *
 * @author 张全
 */

public class InsurancePayAct extends BaseActivity {
    private static final String PARAM = "payinfo";
    private PayInfo payInfo;

    public static void start(Context ctx, PayInfo payInfo) {
        Intent intent = new Intent(ctx, InsurancePayAct.class);
        intent.putExtra(PARAM, payInfo);
        Activity act = ((Activity) ctx);
        act.startActivity(intent);
        act.overridePendingTransition(R.anim.anim_fade_out, R.anim.anim_none);
    }

    @Override
    public int getLayoutId() {
        return R.layout.insurance_pay_act;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        payInfo = (PayInfo) getIntent().getSerializableExtra(PARAM);
        if (null == payInfo) {
            ToastUtil.show("请求失败");
            close();
            return;
        }

        EventBus.getDefault().register(this);
        try {
            InsurancePay insurancePay = new InsurancePay(this);
            insurancePay.openWxPayClient(payInfo.prepayId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        String action = event.getAction();
        if (Constant.IntentAction.ORDER_BUY_SUCCESS.equals(action)
                || Constant.IntentAction.ORDER_BUY_FAIL.equals(action)
                || Constant.IntentAction.ORDER_BUY_CANCEL.equals(action)
                ) {
            close();
        }
    }


    @Override
    public void close() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

}