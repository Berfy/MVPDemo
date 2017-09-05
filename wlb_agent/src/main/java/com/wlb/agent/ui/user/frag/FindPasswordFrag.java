package com.wlb.agent.ui.user.frag;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.text.StringUtil;
import com.wlb.agent.R;
import com.wlb.agent.ui.user.presenter.ForgetPasswordPresenter;
import com.wlb.agent.ui.user.presenter.view.IForgetPasswordView;
import com.wlb.agent.ui.user.util.UserUtil;
import com.wlb.agent.util.VerifyCode;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 忘记密码
 *
 * @author Berfy
 */
public class FindPasswordFrag extends SimpleFrag implements View.OnClickListener, IForgetPasswordView {
    @BindView(R.id.edit_mobile)
    EditText mEditMobile;
    @BindView(R.id.edit_code)
    EditText mEditCode;
    @BindView(R.id.edit_pwd)
    EditText mEditPwd;
    @BindView(R.id.edit_image_code)
    EditText mEditImageCode;
    @BindView(R.id.iv_image_code)
    ImageView mIvImageCode;
    @BindView(R.id.tv_get_code)
    TextView mTvGetCode;
    private ForgetPasswordPresenter presenter;

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam(R.string.find_pwd, FindPasswordFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_find_password_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        presenter = new ForgetPasswordPresenter(this);
        mIvImageCode.setImageBitmap(VerifyCode.getInstance().createBitmap());
    }

    public TextView getCodeView() {
        return mTvGetCode;
    }

    @OnClick({R.id.tv_get_code, R.id.btn_submit, R.id.iv_image_code})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_code://获取验证码
                String phone = StringUtil.trim(mEditMobile.getText().toString());
                // 检测电话号码
                if (!UserUtil.checkPhoneNum(phone)) {
                    return;
                }
                String imageCode = StringUtil.trim(mEditImageCode.getText().toString());
                if (TextUtils.isEmpty(imageCode)) {
                    ToastUtil.show(R.string.login_tip_code_null);
                    return;
                }
                if (imageCode.equals(VerifyCode.getInstance().getCode())) {
                    presenter.getCode(phone);//获取验证码
                } else {
                    ToastUtil.show(R.string.login_tip_code);
                }
                break;
            case R.id.iv_image_code:
                mIvImageCode.setImageBitmap(VerifyCode.getInstance().createBitmap());
                break;
            case R.id.btn_submit:
                String Phone = StringUtil.trim(mEditMobile.getText().toString());
                String verificationCode = StringUtil.trim(mEditCode);
                String password = StringUtil.trim(mEditPwd);
                presenter.doResetPassword(Phone, verificationCode, password);
                break;

            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestory();
    }

    @Override
    public void showFocus() {
        mEditCode.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditCode, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
