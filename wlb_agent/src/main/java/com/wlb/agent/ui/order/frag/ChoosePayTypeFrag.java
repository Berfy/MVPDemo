package com.wlb.agent.ui.order.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.android.util.ext.ToastUtil;
import com.wlb.agent.R;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Berfy on 2017/7/19.
 * 选择支付类型
 */
public class ChoosePayTypeFrag extends SimpleFrag implements View.OnClickListener {

    private final static int PAY_WX = 0;
    private final static int PAY_ALIPAY = 1;
    private final static int PAY_BANK = 2;
    private final static int PAY_DAIFU = 3;
    private int mPayType;

    @BindView(R.id.v_wx)
    View mVWx;
    @BindView(R.id.v_alipay)
    View mVAli;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam(R.string.pay, ChoosePayTypeFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.pay_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }

    @OnClick({R.id.layout_wx, R.id.layout_alipay, R.id.layout_bank, R.id.layout_daifu, R.id.btn_pay})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_wx:
                choose(PAY_WX);
                break;
            case R.id.layout_alipay:
                choose(PAY_ALIPAY);
                break;
            case R.id.layout_bank:
                choose(PAY_BANK);
                break;
            case R.id.layout_daifu:
                choose(PAY_DAIFU);
                break;
            case R.id.btn_pay:
                pay();
                break;
        }
    }

    private void choose(int payType) {
        mPayType = payType;

        switch (payType) {
            case PAY_WX:
                mVWx.setBackgroundResource(R.drawable.ic_underwrite_checked);
                mVAli.setBackgroundResource(R.drawable.ic_underwrite_check);
                break;
            case PAY_ALIPAY:
                mVWx.setBackgroundResource(R.drawable.ic_underwrite_check);
                mVAli.setBackgroundResource(R.drawable.ic_underwrite_checked);
                break;
            case PAY_BANK:
                ToastUtil.show(R.string.test);
                break;
            case PAY_DAIFU:
                ToastUtil.show(R.string.test);
                break;
        }
    }

    private void pay() {
        switch (mPayType) {
            case PAY_WX:

                break;
            case PAY_ALIPAY:

                break;
            case PAY_BANK:

                break;
            case PAY_DAIFU:

                break;
        }
    }
}
