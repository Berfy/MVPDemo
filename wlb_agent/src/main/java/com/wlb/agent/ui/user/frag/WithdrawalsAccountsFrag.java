package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.log.LogUtil;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.BankCardInfo;
import com.wlb.agent.core.data.user.response.AddBankCardInfoResponse;
import com.wlb.agent.core.data.user.response.BankCardResponse;
import com.wlb.agent.util.PopupWindowUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.share.LoginAction;
import common.share.LoginCallBack;
import common.share.LoginInfo;
import common.share.ShareApi;
import common.share.SharePlatform;
import common.share.ShareUserInfo;
import common.share.UserInfoCallback;
import common.widget.dialog.loading.LoadingDialog;

/**
 * Created by Berfy on 2017/7/21.
 * 提现账户列表
 */
public class WithdrawalsAccountsFrag extends SimpleFrag implements View.OnClickListener {

    private PopupWindowUtil mPopupWindowUtil;
    private List<BankCardInfo> mBankCardInfos;
    private BankCardInfo mPayBankCardInfo = new BankCardInfo();
    private boolean mIsGetPayAccount;//获取账户列表，避免连续请求
    private boolean mIsAddAccount;//添加修改账户，避免连续请求
    private Task mDoGetBankCardTask;
    private int mRequestCode;//回调代码
    public static final String PARAM = "data";
    private static final String PARAM_REQUEST_CODE = "requestCode";
    private String mToken, mUid, mNick;

    @BindView(R.id.tv_wx_nick)
    TextView mTvNickWx;

    public static void start(Context context, int requestCode) {
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_REQUEST_CODE, requestCode);
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.wallet_accounts_title,
                WithdrawalsAccountsFrag.class, bundle);
        SimpleFragAct.start(context, param);
    }

    public static void start(Context context, List<BankCardInfo> bankCardInfos, int requestCode) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM, (Serializable) bankCardInfos);
        bundle.putInt(PARAM_REQUEST_CODE, requestCode);
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.wallet_accounts_title,
                WithdrawalsAccountsFrag.class, bundle);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_wallet_withdrawals_accounts_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (null != getArguments()) {
            mBankCardInfos = (List<BankCardInfo>) getArguments().getSerializable(PARAM);
            if (null != mBankCardInfos) {
                for (BankCardInfo bankCardInfo : mBankCardInfos) {
                    if (bankCardInfo.getCardType().equals("WX")) {
                        mPayBankCardInfo = bankCardInfo;
                        mTvNickWx.setText(mPayBankCardInfo.bank_name);
                    }
                }
            }
            mRequestCode = getArguments().getInt(PARAM_REQUEST_CODE);
        }
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        if (null != mBankCardInfos) {
            getPayAccountInfo();
        }
    }

    private void getPayAccountInfo() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsGetPayAccount) {
            return;
        }

        final LoadingDialog dialog = LoadingDialog.showBackCancelableDialog(mContext, "请稍后");
        mDoGetBankCardTask = UserClient.doGetBankCard(new ICallback() {
            @Override
            public void start() {
                mIsGetPayAccount = true;
            }

            @Override
            public void success(Object data) {
                BankCardResponse response = (BankCardResponse) data;
                BankCardInfo defaultAccount = null;
                BankCardInfo defaultWxAccount = null;
                if (response.isSuccessful()) {
                    for (BankCardInfo bankCardInfo : response.list) {
                        if (!bankCardInfo.getCardType().equals("BANK")) {
                            if (bankCardInfo.getCardType().equals("WX")) {
                                defaultWxAccount = bankCardInfo;
                            }
                        }
                    }
                    mPayBankCardInfo = defaultWxAccount;
                    mTvNickWx.setText(mPayBankCardInfo.bank_name);
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
                mIsGetPayAccount = false;
                dialog.dissmiss();
            }
        });
    }

    /**
     * 添加修改微信账号
     */
    private void addWxAccount() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsAddAccount) {
            return;
        }

        final LoadingDialog dialog = LoadingDialog.showBackCancelableDialog(mContext, "请稍后");
        mDoGetBankCardTask = UserClient.addWxAccount(mUid, mNick, new ICallback() {
            @Override
            public void start() {
                mIsAddAccount = true;
            }

            @Override
            public void success(Object data) {
                AddBankCardInfoResponse response = (AddBankCardInfoResponse) data;
                if (response.isSuccessful()) {
                    mPayBankCardInfo.bank_name = mNick;
                    mPayBankCardInfo.bank_no = mUid;
                    mPayBankCardInfo.bank_id = response.bank_id;
                    mTvNickWx.setText(mNick);
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
                mIsAddAccount = false;
                dialog.dissmiss();
            }
        });
    }

    @OnClick({R.id.layout_wx, R.id.layout_ali})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_wx:
                if (null != mPayBankCardInfo && !TextUtils.isEmpty(mPayBankCardInfo.bank_name)) {//只支持微信
                    mPopupWindowUtil.chooseWalletWithDrawal(0, mPayBankCardInfo.bank_name, new PopupWindowUtil.OnChooseWithDrawalListener() {
                        @Override
                        public void withDrawal(int payType, String name) {
                            Intent intent = new Intent();
                            intent.putExtra(PARAM, mPayBankCardInfo);
                            setResult(mRequestCode);
                            close();
                        }

                        @Override
                        public void change(int payType, String curName) {

                        }
                    });
                    break;
                } else {
                    loginAction(SharePlatform.WEIXIN);
                }
        }
    }

    private LoginAction loginAction;

    private void loginAction(SharePlatform platform) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        LoginInfo info = ShareApi.getInstance().getLoginInfo(mContext, platform);
        if (null == info) {
            try {
                loginAction = new LoginAction();
                loginAction.setPlatform(platform);
                loginAction.setCallBack(mLoginCallBack);
                loginAction.login(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            doThreeLogin(platform, info.uid, info.accessToken);
        }
    }

    LoginCallBack mLoginCallBack = new LoginCallBack() {

        @Override
        public void onError(Throwable e) {
            ToastUtil.show("登录失败");
        }

        @Override
        public void onComplete(SharePlatform platform, LoginInfo values) {
            LogUtil.e("登陆成功  ", values.uid + "   " + values.accessToken);
            doThreeLogin(platform, values.uid, values.accessToken);
        }

        @Override
        public void onCancel() {
            ToastUtil.show("取消登录");
        }
    };

    UserInfoCallback mUserInfoCallback = new UserInfoCallback() {
        @Override
        public void success(ShareUserInfo userInfo) {
            mNick = userInfo.nickName;
            addWxAccount();
        }

        @Override
        public void fail(Exception e) {

        }
    };

    private void doThreeLogin(SharePlatform platform, String useId, String accessToken) {//获取昵称
        mToken = accessToken;
        mUid = useId;
        ShareApi.getInstance().getUserInfo(mContext, platform, mUserInfoCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mDoGetBankCardTask) {
            mDoGetBankCardTask.stop();
        }
    }
}
