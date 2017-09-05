package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.android.util.text.StringUtil;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.common.view.CountButton;
import com.wlb.agent.ui.user.util.UserUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;

/**
 * 用户注册
 *
 * @author Berfy
 */
public class UserRegisterFrag extends SimpleFrag {

    private Task mGetCodeTask, mCheckCodeTask;
    @BindView(R.id.edit_login_mobile)
    EditText mEditMobile;
    @BindView(R.id.edit_code)
    EditText mEditCode;
    @BindView(R.id.btn_get_code)
    CountButton mBtnGetCode;
    @BindView(R.id.v_delete_mobile)
    ImageView mBtnDeleteMobile;
    @BindView(R.id.v_delete_code)
    ImageView mIvDeleteCode;
    @BindView(R.id.chb_isagree)
    CheckBox mChbCheck;


    public static void start(Context context) {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam("注册",
                UserRegisterFrag.class);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_register_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        tvChange(mEditMobile, mBtnDeleteMobile);
        tvChange(mEditCode, mIvDeleteCode);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mGetCodeTask) {
            mGetCodeTask.stop();
        }
        if (null != mCheckCodeTask) {
            mCheckCodeTask.stop();
        }
        if (null != mBtnGetCode) {
            mBtnGetCode.stop();
        }
    }

    @OnClick({R.id.v_delete_mobile, R.id.btn_get_code, R.id.register_commit, R.id.tv_login, R.id.tv_web})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_delete_mobile:
                mEditMobile.setText("");
                break;
            case R.id.v_delete_code:
                mEditCode.setText("");
                break;
            case R.id.btn_get_code://获取验证码
                String phone = StringUtil.trim(mEditMobile);
                // 检测电话号码
                if (!UserUtil.checkPhoneNum(phone)) {
                    return;
                }
                getCode(phone);//获取验证码
                break;
            case R.id.register_commit://提交
                String phoneNo = StringUtil.trim(mEditMobile.getText().toString());
                // 检测电话号码
                if (!UserUtil.checkPhoneNum(phoneNo)) {
                    return;
                }
                String code = mEditCode.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.show(R.string.tip_code_null);
                    return;
                }
                if (!mChbCheck.isChecked()) {
                    ToastUtil.show(R.string.regist_reg_check);
                    return;
                }
                doRegist(phoneNo, code);
                break;
            case R.id.tv_web:
                WebViewFrag.start(mContext, new WebViewFrag.WebViewParam(getString(R.string.regist_web_title), false, H5.REGIST_LICENCE, null));
                break;
            case R.id.tv_login:
                UserLoginFrag.start(mContext, UserLoginFrag.LoginTargetPage.HOMEPAGE);
                finish();
                break;
        }
    }

    private boolean isGetCode;

    private void getCode(String phone) {
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            showToastShort(R.string.net_noconnection);
            return;
        }
        final LoadingDialog dialog = new LoadingDialog(mContext, getString(R.string.getting_code)).show();

        if (isGetCode) {
            return;
        }
        mGetCodeTask = UserClient.doGetCode(phone, UserClient.CodeAction.REGIST, new ICallback() {
            @Override
            public void start() {
                isGetCode = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("验证码已发出");
                    mBtnGetCode.setText("", "s");
                    mBtnGetCode.start(60);
                    showFocus(mEditCode);
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
                isGetCode = false;
                dialog.dissmiss();
            }
        });
    }

    private boolean isCheckCode;

    private void doRegist(String phoneNo, String verifyCode) {
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            showToastShort(R.string.net_noconnection);
            return;
        }
        final LoadingDialog dialog = new LoadingDialog(mContext, "请稍候").show();

        if (isCheckCode) {
            return;
        }
        mCheckCodeTask = UserClient.doRegist(phoneNo, verifyCode, new ICallback() {
            @Override
            public void start() {
                isCheckCode = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show(R.string.regist_suc);
                    EventBus.getDefault().post(new PushEvent(Constant.IntentAction.USER_REGIST));
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
                isCheckCode = false;
                dialog.dissmiss();
            }
        });
    }


    private void showFocus(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void tvChange(EditText textView, final ImageView imageView) {
        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    imageView.setVisibility(View.VISIBLE);
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
