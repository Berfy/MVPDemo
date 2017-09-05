package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.util.LContext;
import com.android.util.ext.SoftInputUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.callback.SimpleCallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.BankCardInfo;
import com.wlb.agent.core.data.user.response.BankCardResponse;
import com.wlb.agent.core.data.user.response.WalletResponse;
import com.wlb.agent.core.data.user.response.WithDrawalCheckResponse;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;

/**
 * Created by Berfy on 2017/7/21.
 * 提现
 */
public class WithdrawalsFrag extends SimpleFrag implements View.OnClickListener {

    private PopupWindowUtil mPopupWindowUtil;
    private boolean mIsBankCard;//避免连续请求
    private boolean mIsCheck;//检查认证和密码，避免连续请求
    private boolean mIsGetWalletInfo;//避免连续请求
    private boolean mIsWithDrawal;//避免连续请求
    private Task mDoGetBankCardTask;
    private Task mDoWithDrawalCheck;
    private Task mGetWalletTask;
    private Task mWithDrawalTask;
    private List<BankCardInfo> mPayCardInfos;
    private BankCardInfo mPayCardInfo;
    private WalletResponse mWalletResponse;//钱包
    private WithDrawalCheckResponse mWithDrawalCheckResponse;//检查提现密码和认证状态
    private static final int mReqeustCode = 1;//提现回调代码
    private double mPrice;//提现金额
    private String mPwd;//提现密码

    @BindView(R.id.edit_price)
    EditText mEditPrice;//提现金额
    @BindView(R.id.tv_wallet_account)
    TextView mTvAccount;//提现账户
    @BindView(R.id.tv_account_nick)
    TextView mTvAccountNick;//提现账户昵称
    @BindView(R.id.layout_auth)
    LinearLayout mLlAuth;//认证
    @BindView(R.id.layout_pwd)
    LinearLayout mLlPwd;//设置提现密码

    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.wallet_get_rmb,
                WithdrawalsFrag.class);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_wallet_withdrawals_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        mEditPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    mPrice = Double.valueOf(s.toString());
                    if (null != mWalletResponse) {
                        if (mPrice > mWalletResponse.walletSummary.available_money) {
                            ToastUtil.show(R.string.wallet_withdrawal_price_tip);
                            mEditPrice.getText().delete(mEditPrice.getText().length() - 1, mEditPrice.getText().length());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        doGetWalletInfo();
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
                mWalletResponse = (WalletResponse) data;
                if (mWalletResponse.isSuccessful()) {//请求钱包后再依次请求
                    getPayAccountInfo();
                    check();
                } else {
                    ToastUtil.show(((WalletResponse) data).msg);
                    close();
                }
            }

            @Override
            public void end() {
                mIsGetWalletInfo = false;
            }
        });
    }

    private void check() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsCheck) {
            return;
        }

        final LoadingDialog dialog = LoadingDialog.showBackCancelableDialog(mContext, "请稍后");
        mDoWithDrawalCheck = UserClient.doWithDrawCheck(new ICallback() {
            @Override
            public void start() {
                mIsCheck = true;
            }

            @Override
            public void success(Object data) {
                mWithDrawalCheckResponse = (WithDrawalCheckResponse) data;
                if (mWithDrawalCheckResponse.isSuccessful()) {
                    if (mWithDrawalCheckResponse.needPWD == 0) {
                        mLlAuth.setVisibility(View.VISIBLE);
                        mLlAuth.setVisibility(View.VISIBLE);
                    }
                    updateLayout(mWithDrawalCheckResponse.needPWD == 1, mWithDrawalCheckResponse.needAuth == 1);
                } else {
                    ToastUtil.show(((WithDrawalCheckResponse) data).msg);
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                mIsCheck = false;
                dialog.dissmiss();
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

        final LoadingDialog dialog = LoadingDialog.showBackCancelableDialog(mContext, "请稍后");
        mDoGetBankCardTask = UserClient.doGetBankCard(new ICallback() {
            @Override
            public void start() {
                mIsBankCard = true;
            }

            @Override
            public void success(Object data) {
                BankCardResponse response = (BankCardResponse) data;
                BankCardInfo defaultAccount = null;
                BankCardInfo defaultWxAccount = null;
                if (response.isSuccessful()) {
                    if (null != response.list) {
                        mPayCardInfos = response.list;
                        for (BankCardInfo bankCardInfo : mPayCardInfos) {
                            if (!bankCardInfo.getCardType().equals("BANK")) {
                                if (bankCardInfo.isDefault()) {
                                    defaultAccount = bankCardInfo;
                                }
                                if (bankCardInfo.getCardType().equals("WX")) {
                                    defaultWxAccount = bankCardInfo;
                                }
                            }
                        }
                        if (null != defaultAccount) {
                            mPayCardInfo = defaultAccount;
                        } else {
                            mPayCardInfo = defaultWxAccount;
                        }
                        if (null != mPayCardInfo) {
                            mTvAccount.setText(mPayCardInfo.getCardType().equals("WX") ? getString(R.string.wallet_accounts_wx_title)
                                    : getString(R.string.wallet_accounts_ali));
                            mTvAccountNick.setText("(" + mPayCardInfo.bank_name + ")");
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

    //提现
    private void withDrawal() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsWithDrawal) {
            return;
        }

        final LoadingDialog dialog = LoadingDialog.showBackCancelableDialog(mContext, "请稍后");
        mWithDrawalTask = UserClient.doWithDrawMoneny3(mPwd, mPrice + "", mPayCardInfo.bank_id, new ICallback() {
            @Override
            public void start() {
                mIsWithDrawal = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show(response.msg);
                    close();
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
                mIsWithDrawal = false;
                dialog.dissmiss();
            }
        });
    }

    private void updateLayout(boolean isShowPWD, boolean isShowAuth) {
        mLlPwd.setVisibility(View.GONE);
        mLlAuth.setVisibility(View.GONE);
        if (isShowPWD) {
            mLlPwd.setVisibility(View.VISIBLE);
        }
        if (isShowAuth) {
            mLlAuth.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.layout_account, R.id.btn_set_pwd, R.id.btn_auth,
            R.id.btn_submit, R.id.tv_help})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_account:
                if (null != mPayCardInfos) {
                    WithdrawalsAccountsFrag.start(mContext, mReqeustCode);
                }
                break;
            case R.id.btn_set_pwd:
                WalletWithDrawalSetPwdFrag.start(mContext);
                break;
            case R.id.btn_auth:
                UserAuthFrag.start(mContext);
                break;
            case R.id.btn_submit:
                if (null != mWithDrawalCheckResponse) {
                    if (mPrice <= 0) {
                        ToastUtil.show(R.string.wallet_withdrawal_price_tip1);
                        return;
                    }
                    if (null == mPayCardInfo) {
                        ToastUtil.show(R.string.wallet_withdrawal_price_tip2);
                        return;
                    }
                    if (mWithDrawalCheckResponse.needAuth == 1) {//需要认证
                        UserAuthFrag.start(mContext);
                    } else if (mWithDrawalCheckResponse.needPWD == 1) {//需要设置密码
                        WalletWithDrawalSetPwdFrag.start(mContext);
                    } else {
                        //提现
                        SoftInputUtil.hideSoftInput(getActivity());
                        mPopupWindowUtil.showPwdInput(new PopupWindowUtil.OnInputPwdListener() {
                            @Override
                            public void ok(String pwd) {
                                mPwd = pwd;
                                withDrawal();
                            }

                            @Override
                            public void modifyPwd() {
                                WalletWithDrawalChangePasswordFrag.start(mContext);
                            }
                        });
                    }
                }
                break;
            case R.id.tv_help:
                WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.title = getString(R.string.wallet_help);
                webViewParam.url = H5.WALLET_RULE;
                WebViewFrag.start(mContext, webViewParam);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mDoGetBankCardTask)
            mDoGetBankCardTask.stop();
        if (null != mDoWithDrawalCheck)
            mDoWithDrawalCheck.stop();
        if (null != mGetWalletTask)
            mGetWalletTask.stop();
        if (null != mWithDrawalTask)
            mWithDrawalTask.stop();
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
