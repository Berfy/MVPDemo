package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.android.util.text.StringUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;

/**
 * 提现密码修改
 * Created by Berfy
 */
public class WalletWithDrawalChangePasswordFrag extends SimpleFrag implements View.OnClickListener {

    private Task mTaskPassword;
    private boolean mIsTaskPassword;//避免重复请求

    @BindView(R.id.edit_old_password)
    EditText mEditOldPwd;
    @BindView(R.id.edit_new_password)
    EditText mEditNewPwd;
    @BindView(R.id.tv_forget_password)
    TextView mTvForgetPwd;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam(R.string.wallet_input_withdrawal_pwd_tip2, WalletWithDrawalChangePasswordFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_wallet_change_withdrawal_pwd;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
    }

    @OnClick({R.id.btn_submit, R.id.tv_forget_password})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                String oldPassword = StringUtil.trim(mEditOldPwd);//原始密码
                if (TextUtils.isEmpty(oldPassword)) {
                    ToastUtil.show(R.string.wallet_withdrawal_changepwd_old_pwd_hint);
                    return;
                }
                String newPassword = StringUtil.trim(mEditNewPwd);//原始密码
                if (TextUtils.isEmpty(newPassword)) {
                    ToastUtil.show(R.string.wallet_withdrawal_changepwd_new_pwd_hint);
                    return;
                }
                changePassword(oldPassword, newPassword);
                break;
            case R.id.tv_forget_password:
                WalletWithDrawalResetPasswordFrag.start(mContext);
                break;
            default:
                break;
        }
    }

    private void changePassword(String oldPassword, String newPassword) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsTaskPassword) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "密码修改中...");
        mTaskPassword = UserClient.modifyWithDrawPwd(oldPassword, newPassword, new ICallback() {
            @Override
            public void start() {
                mIsTaskPassword = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show(R.string.wallet_withdrawal_pwd_change_suc_tip);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mTaskPassword) {
            mTaskPassword.stop();
        }
    }
}
