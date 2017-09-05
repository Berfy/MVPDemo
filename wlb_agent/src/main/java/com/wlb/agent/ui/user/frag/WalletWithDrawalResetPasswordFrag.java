package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.android.util.text.StringUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.common.view.CountButton;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;

/**
 * 提现密码重置
 * Created by Berfy
 */
public class WalletWithDrawalResetPasswordFrag extends SimpleFrag implements View.OnClickListener {

    private Task mTaskPassword;
    private boolean mIsTaskPassword;//避免重复请求

    @BindView(R.id.edit_password)
    EditText mEditPwd;
    @BindView(R.id.edit_confirm_password)
    EditText mEditConfirmPwd;
    @BindView(R.id.edit_code)
    EditText mEditCode;
    @BindView(R.id.edit_mobile)
    EditText mEditMobile;
    @BindView(R.id.btn_get_code)
    CountButton mBtnGetCode;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam(R.string.wallet_withdrawal_respwd, WalletWithDrawalResetPasswordFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_wallet_reset_withdrawal_pwd;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
    }

    @OnClick({R.id.btn_get_code, R.id.btn_submit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_code:
                String password = StringUtil.trim(mEditPwd);//新密码
                if (TextUtils.isEmpty(password)) {
                    ToastUtil.show(R.string.wallet_withdrawal_respwd_new_pwd_tip);
                    return;
                }
                String confirmPassword = StringUtil.trim(mEditConfirmPwd);//确认密码
                if (TextUtils.isEmpty(confirmPassword)) {
                    ToastUtil.show(R.string.wallet_withdrawal_respwd_confirm_pwd_tip);
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    ToastUtil.show(R.string.wallet_withdrawal_respwd_confirm_pwd_not_same_tip);
                    return;
                }
                String mobile = StringUtil.trim(mEditMobile);//手机号
                if (TextUtils.isEmpty(mobile)) {
                    ToastUtil.show(R.string.mobile_null_tip);
                    return;
                }
                getCode(mobile);
                break;
            case R.id.btn_submit:
                password = StringUtil.trim(mEditPwd);//新密码
                if (TextUtils.isEmpty(password)) {
                    ToastUtil.show(R.string.wallet_withdrawal_respwd_new_pwd_tip);
                    return;
                }
                confirmPassword = StringUtil.trim(mEditConfirmPwd);//确认密码
                if (TextUtils.isEmpty(confirmPassword)) {
                    ToastUtil.show(R.string.wallet_withdrawal_respwd_confirm_pwd_tip);
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    ToastUtil.show(R.string.wallet_withdrawal_respwd_confirm_pwd_not_same_tip);
                    return;
                }
                mobile = StringUtil.trim(mEditMobile);//手机号
                if (TextUtils.isEmpty(mobile)) {
                    ToastUtil.show(R.string.mobile_null_tip);
                    return;
                }
                String code = StringUtil.trim(mEditCode);//验证码
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.show(R.string.login_tip_code_null);
                    return;
                }
                resetPassword(password, confirmPassword, code);
                break;
            default:
                break;
        }
    }

    private void resetPassword(String oldPassword, String newPassword, String code) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsTaskPassword) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "密码重置中...");
        mTaskPassword = UserClient.resetWithDrawPwd(oldPassword, newPassword, code, new ICallback() {
            @Override
            public void start() {
                mIsTaskPassword = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show(R.string.wallet_withdrawal_pwd_reset_suc_tip);
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
                mIsTaskPassword = false;
                dialog.dissmiss();
            }
        });
    }

    private void getCode(String mobile) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsTaskPassword) {
            return;
        }
        final LoadingDialog dialog = new LoadingDialog(mContext, getString(R.string.getting_code)).show();
        mTaskPassword = UserClient.doGetCode(mobile, UserClient.CodeAction.RESETWALLETPWD, new ICallback() {
            @Override
            public void start() {
                mIsTaskPassword = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show(R.string.get_code_sendding);
                    mBtnGetCode.setText("", "s");
                    mBtnGetCode.start(60);
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
                mIsTaskPassword = false;
                dialog.dissmiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mTaskPassword) {
            mTaskPassword.stop();
        }
        if (null != mBtnGetCode) {
            mBtnGetCode.stop();
        }
    }
}
