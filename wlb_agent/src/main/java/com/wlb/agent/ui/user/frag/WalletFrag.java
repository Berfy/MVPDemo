package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.util.LContext;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.callback.SimpleCallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.AuthStatus;
import com.wlb.agent.core.data.user.entity.BankCardInfo;
import com.wlb.agent.core.data.user.entity.WalletInfo;
import com.wlb.agent.core.data.user.response.BankCardResponse;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.core.data.user.response.WalletResponse;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;

/**
 * Created by Berfy on 2017/7/21.
 * 我的钱包
 */

public class WalletFrag extends SimpleFrag implements View.OnClickListener {

    private boolean mIsGetWalletInfo;
    private Task mGetWalletTask;
    private boolean mIsBankCard;
    private Task mDoGetBankCardTask;
    private BankCardInfo mPayCardInfo;
    private static final int mReqeustCode = 1;//提现回调代码
    private WalletInfo mWalletInfo;//钱包
    private UserResponse mUserResponse;//登录用户信息

    @BindView(R.id.btn_tuiguangfei)
    Button mBtnTuiguangfei;
    @BindView(R.id.tv_help)
    TextView mTvHelp;
    @BindView(R.id.tv_tuiguangfei)
    TextView mTvTuiguangfei;//推广费
    @BindView(R.id.tv_tuiguangfei_close)
    TextView mTvTuiguangfeiClose;//隐藏推广费
    @BindView(R.id.tv_wallet_withdrawal)
    TextView mTvWithDrawal;//可提现
    @BindView(R.id.tv_cumulative)
    TextView mTvCumulative;//累计
    @BindView(R.id.tv_will_can)
    TextView mTvWillcan;//待审核
    @BindView(R.id.tv_wallet_account)
    TextView mTvAccount;//提现账户
    @BindView(R.id.tv_wallet_account_null_tip)
    TextView mTvAccountNullTip;//选择银行卡提示，没有默认账户时显示
    @BindView(R.id.tv_account_nick)
    TextView mTvAccountNick;//提现账户昵称


    public static SimpleFragAct.SimpleFragParam getStartParam() {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.my_wallet, WalletFrag.class);
        return param;
    }

    public static void start(Context context) {
        getStartParam().coverTilteBar(true);
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_wallet_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        getTitleBar().setRightText(R.string.wallet_detail);
        getTitleBar().setOnRightTxtClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mUserResponse.id_auth_info && mUserResponse.id_auth_info.getAuthStatus() != AuthStatus.AUTH_SUCCESS) {
                    ToastUtil.show(R.string.user_auth_tip);
                    return;
                }
                WalletDetailFrag.start(mContext);
            }
        });
        mTvHelp.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
    }

    @Override
    public void onResume() {
        super.onResume();
        mUserResponse = UserClient.getLoginedUser();
        doGetWalletInfo();
        getPayAccountInfo();
        updateSwitch();
    }

    /**
     * 获取钱包信息
     */
    private void doGetWalletInfo() {
        if (!NetworkUtil.isNetworkAvailable(LContext.getContext())) {
            return;
        }
        if (mIsGetWalletInfo) {
            return;
        }
        mIsGetWalletInfo = true;
        mGetWalletTask = UserClient.doGetWallet(new SimpleCallback() {
            @Override
            public void success(Object data) {
                WalletResponse response = (WalletResponse) data;
                if (response.isSuccessful()) {
                    if (null != response.walletSummary) {
                        mWalletInfo = response.walletSummary;
                        mTvTuiguangfei.setText(response.walletSummary.curren_balance + "");
                        mTvWithDrawal.setText(response.walletSummary.available_money + "");
                        mTvWillcan.setText(response.walletSummary.processing_money + "");
                        mTvCumulative.setText(response.walletSummary.total_draw + "");
                    }
                }
            }

            @Override
            public void end() {
                mIsGetWalletInfo = false;
            }
        });
    }

    private void getPayAccountInfo() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsBankCard) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showBackCancelableDialog(mContext, "");
        mDoGetBankCardTask = UserClient.doGetBankCard(new ICallback() {
            @Override
            public void start() {
                mIsBankCard = true;
            }

            @Override
            public void success(Object data) {
                BankCardResponse response = (BankCardResponse) data;
                BankCardInfo defaultAccount = null;
//                BankCardInfo defaultWxAccount = null;
                if (response.isSuccessful()) {
                    if (null != response.list) {
                        for (BankCardInfo bankCardInfo : response.list) {
                            if (bankCardInfo.getCardType().equals("BANK")) {
                                if (bankCardInfo.isDefault()) {
                                    defaultAccount = bankCardInfo;
                                }
                                //老版本
//                                if (!bankCardInfo.getCardType().equals("BANK")) {
//                                    if (bankCardInfo.isDefault()) {
//                                        defaultAccount = bankCardInfo;
//                                    }
//                                    if (bankCardInfo.getCardType().equals("WX")) {
//                                        defaultWxAccount = bankCardInfo;
//                                    }
//                                }
                            }
                        }
                        if (null != defaultAccount) {
                            mPayCardInfo = defaultAccount;
                        }
                        //老版本
//                        else {
//                            mPayCardInfo = defaultWxAccount;
//                        }
                        if (null != mPayCardInfo) {
                            mTvAccountNullTip.setVisibility(View.GONE);
                            //老版本
                            mTvAccount.setText(mPayCardInfo.bank_name);
//                            mTvAccount.setText(mPayCardInfo.getCardType().equals("WX") ? getString(R.string.wallet_accounts_wx_title)
//                                    : getString(R.string.wallet_accounts_ali));
//                            mTvAccountNick.setText("(" + mPayCardInfo.bank_name + ")");
                        } else {
                            mTvAccountNullTip.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                mIsBankCard = false;
                dialog.dissmiss();
            }
        });
    }

    @OnClick({R.id.tv_help, R.id.btn_tuiguangfei, R.id.layout_wallet_will_can, R.id.btn_submit, R.id.layout_wallet_accounts})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_tuiguangfei:
//                UserCacheUtil.getInstance(mContext).saveTuifuangfei(!UserCacheUtil.getInstance(mContext).getTuifuangfei());
//                updateSwitch();
                break;
            case R.id.layout_wallet_will_can:
                if (null != mUserResponse.id_auth_info && mUserResponse.id_auth_info.getAuthStatus() != AuthStatus.AUTH_SUCCESS) {
                    ToastUtil.show(R.string.user_auth_tip);
                    return;
                }
                WalletWillCanFrag.start(mContext);
                break;
            case R.id.layout_wallet_accounts:
                if (null != mUserResponse.id_auth_info && mUserResponse.id_auth_info.getAuthStatus() != AuthStatus.AUTH_SUCCESS) {
                    ToastUtil.show(R.string.user_auth_tip);
                    return;
                }
                BankCardListFrag.start(mContext, true);
//                WithdrawalsAccountsFrag.start(mContext, mReqeustCode);
                break;
            case R.id.tv_help:
                WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.title = getString(R.string.wallet_help);
                webViewParam.url = H5.WALLET_RULE;
                WebViewFrag.start(mContext, webViewParam);
                break;
            case R.id.btn_submit:
                if (null != mUserResponse.id_auth_info && mUserResponse.id_auth_info.getAuthStatus() != AuthStatus.AUTH_SUCCESS) {
                    ToastUtil.show(R.string.user_auth_tip);
                    return;
                }
                WalletWithdrawFrag.start(mContext, mWalletInfo);
//                WithdrawalsFrag.start(mContext);
                break;
        }
    }

    private void updateSwitch() {
        if (UserClient.getTuifuangfei()) {
            mBtnTuiguangfei.setBackgroundResource(R.drawable.ic_user_tuiguangfei_open);
            mTvTuiguangfeiClose.setVisibility(View.GONE);
            mTvTuiguangfei.setVisibility(View.VISIBLE);
        } else {
            mBtnTuiguangfei.setBackgroundResource(R.drawable.ic_user_tuiguangfei_close);
            mTvTuiguangfeiClose.setVisibility(View.VISIBLE);
            mTvTuiguangfei.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mGetWalletTask)
            mGetWalletTask.stop();
        if (null != mDoGetBankCardTask)
            mDoGetBankCardTask.stop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != data) {
            switch (requestCode) {
                case mReqeustCode://提现账号选择
                    BankCardInfo bankCardInfo = (BankCardInfo) data.getSerializableExtra(WithdrawalsAccountsFrag.PARAM);
                    if (null != bankCardInfo) {
                        mPayCardInfo = bankCardInfo;
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
