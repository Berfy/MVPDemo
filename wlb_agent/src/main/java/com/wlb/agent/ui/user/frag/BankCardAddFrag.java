package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.android.util.text.StringUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.user.presenter.BackCardAddPresenter;
import com.wlb.agent.ui.user.presenter.view.IBackCardAddView;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by JiaHongYa
 * <p>
 * 添加银行卡
 * Berfy修改
 */

public class BankCardAddFrag extends SimpleFrag implements View.OnClickListener,IBackCardAddView {

    @BindView(R.id.edit_cardMaster)
    EditText mEditCardMaster;//持卡人
    @BindView(R.id.edit_cardNumber)
    EditText mEditardNumber;//卡号
    @BindView(R.id.float_banner)
    View mVFloatBanner;
    @BindView(R.id.tv_error)
    TextView mTvError;

    private Animation fadeOut, fadeIn;
    private long duration = 300;
    private BackCardAddPresenter presenter;

    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam("添加银行卡", BankCardAddFrag.class);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.bankcard_add_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        presenter = new BackCardAddPresenter(this);

        fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.anim_fade_out);
        fadeIn = AnimationUtils.loadAnimation(mContext, R.anim.anim_fade_in);
        fadeOut.setDuration(duration);
        fadeIn.setDuration(duration);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestory();
    }

    @OnClick({R.id.submit, R.id.attention, R.id.btn_know})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit: //提交
                String carMaster = StringUtil.trim(mEditCardMaster);//持卡人姓名
                String cardNumber = StringUtil.trim(mEditardNumber);//卡号
                presenter.addBankCard(carMaster,cardNumber);
                break;
            case R.id.attention://持卡人注意事项说明
                WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.title = getString(R.string.wallet_help);
                webViewParam.url = H5.WALLET_RULE;
                WebViewFrag.start(mContext, webViewParam);
                break;
            case R.id.btn_know://关闭错误页面
                hide();
                break;
        }
    }

    //显示浮层
    @Override
    public void showErrorBar(String errorMsg) {
        mTvError.setText(errorMsg);
        mVFloatBanner.setVisibility(View.VISIBLE);
        mVFloatBanner.startAnimation(fadeOut);
    }

    //隐藏浮层
    public void hide() {
        mVFloatBanner.startAnimation(fadeIn);
        mVFloatBanner.postDelayed(new Runnable() {
            @Override
            public void run() {
                mVFloatBanner.setVisibility(View.GONE);
            }
        }, duration);
    }
}
