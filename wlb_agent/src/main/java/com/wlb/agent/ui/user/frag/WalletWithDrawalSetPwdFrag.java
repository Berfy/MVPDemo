package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.util.device.DeviceUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.common.view.Pwd6KeyBorad;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;

/**
 * Created by Berfy on 2017/7/24.
 * 第一次设置提现密码
 */
public class WalletWithDrawalSetPwdFrag extends SimpleFrag implements View.OnClickListener, Pwd6KeyBorad.OnPwd6SelectListener {

    private List<TextView> mTvPwds = new ArrayList<>();
    private Task mTaskPwd;
    private boolean mIsTaskPwd;//避免重复请求

    @BindView(R.id.layout_pwd)
    LinearLayout mLlPwd;
    @BindView(R.id.tv_pwd1)
    TextView mTvPwd1;
    @BindView(R.id.tv_pwd2)
    TextView mTvPwd2;
    @BindView(R.id.tv_pwd3)
    TextView mTvPwd3;
    @BindView(R.id.tv_pwd4)
    TextView mTvPwd4;
    @BindView(R.id.tv_pwd5)
    TextView mTvPwd5;
    @BindView(R.id.tv_pwd6)
    TextView mTvPwd6;
    @BindView(R.id.pwd6KeyBoard)
    Pwd6KeyBorad mpwd6KeyBorad;


    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.wallet_set_withdrawal_pwd,
                WalletWithDrawalSetPwdFrag.class);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_wallet_set_withdrawal_pwd_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        int width = DeviceUtil.getScreenWidthPx(mContext) - DeviceUtil.dip2px(mContext, 40);
        int height = (width - 5 * DeviceUtil.dip2px(mContext, 4)) / 6;
        mLlPwd.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        mTvPwds.add(mTvPwd1);
        mTvPwds.add(mTvPwd2);
        mTvPwds.add(mTvPwd3);
        mTvPwds.add(mTvPwd4);
        mTvPwds.add(mTvPwd5);
        mTvPwds.add(mTvPwd6);
        mpwd6KeyBorad.setListener(this);
    }

    @OnClick({R.id.tv_pwd1, R.id.tv_pwd2, R.id.tv_pwd3, R.id.tv_pwd4, R.id.tv_pwd5, R.id.tv_pwd6})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_pwd1:
            case R.id.tv_pwd2:
            case R.id.tv_pwd3:
            case R.id.tv_pwd4:
            case R.id.tv_pwd5:
            case R.id.tv_pwd6:
                mpwd6KeyBorad.show();
                break;
        }
    }

    private void setUpPwd(String pwd) {
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.show(R.string.wallet_withdrawal_pwd_null_tip);
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (mIsTaskPwd) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "密码修改中...");
        mTaskPwd = UserClient.setWithDrawPwd(pwd, new ICallback() {
            @Override
            public void start() {
                mIsTaskPwd = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse baseResponse = (BaseResponse) data;
                if (baseResponse.isSuccessful()) {
                    ToastUtil.show(R.string.wallet_withdrawal_pwd_set_suc_tip);
                    close();
                } else {
                    ToastUtil.show(baseResponse.msg);
                }
            }

            @Override
            public void failure(NetException e) {

            }

            @Override
            public void end() {
                mIsTaskPwd = false;
                dialog.dissmiss();
            }
        });
    }

    @Override
    public void pwd(String pwd) {
        //设置密码
        setUpPwd(pwd);
    }

    @Override
    public void input(List<String> pwds) {
        showPwd(pwds);
    }

    @Override
    public void delete(List<String> pwds, String pwd) {
        showPwd(pwds);
    }

    private void showPwd(List<String> pwds) {
        int size = pwds.size();
        for (int i = 0; i < 6; i++) {
            mTvPwds.get(i).setText(size > i ? pwds.get(i) : "");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mTaskPwd) {
            mTaskPwd.stop();
        }
    }
}
