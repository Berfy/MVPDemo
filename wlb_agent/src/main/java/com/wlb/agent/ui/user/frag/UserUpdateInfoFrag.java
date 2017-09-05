package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.android.util.text.RegUtil;
import com.android.util.text.StringUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.user.helper.GetCodeTimer;
import com.wlb.agent.ui.user.util.UserUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;

/**
 * 更新用户信息
 *
 * @author 张全
 */
public class UserUpdateInfoFrag extends SimpleFrag implements OnClickListener {

    private static final String PARAM_ACTION = "action";
    private Task doBindPhoneTask;
    private Task doGetPhoneCode;
    private Task updateTask;
    private UserUpateAction mAction;
    private UserResponse loginedUser;
    private final int INTERVAL = 60 * 1000;
    @BindView(R.id.phone)
    EditText et_phone;
    @BindView(R.id.code_tex)
    TextView et_phoneCode;
    @BindView(R.id.add_address)
    EditText add_address;
    private GetCodeTimer getCodeTimer;
    private int PHONE_CODE = 6;
    @BindView(R.id.nickName)
    TextView nickName;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.button_code)
    Button et_code;
    @BindView(R.id.submit)
    Button submit;
    @BindView(R.id.phone_banner)
    View bindPhone_banner;
    @BindView(R.id.address_tex)
    View addressBanner;

    public static void start(Context ctx, UserUpateAction action) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_ACTION, action);
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam(action.name, UserUpdateInfoFrag.class, bundle));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_updateinfo_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mAction = (UserUpateAction) getArguments().getSerializable(PARAM_ACTION);
        loginedUser = UserClient.getLoginedUser();

        getCodeTimer = new GetCodeTimer(et_code);
        long nextTime = UserUtil.getNextTime();
        if (nextTime > 0
                && ((nextTime - System.currentTimeMillis()) > 1 * 1000)) {
            getCodeTimer.start();
        }

        switch (mAction) {
            case NICKNAME://用户昵称 nickName  待完善。参考详细地址信息
                nickName.setText("昵称");
                add_address.setHint("请输入新昵称2-10个字符、数字");
                String name = loginedUser.nick_name;
                add_address.setText(name);
                if (!TextUtils.isEmpty(name)) {
                    add_address.setSelection(name.length());
                }
                addressBanner.setVisibility(View.VISIBLE);
                bindPhone_banner.setVisibility(View.GONE);
                break;
            case EMAILADDRESS://Email地址
                nickName.setText("邮箱");
                String email = loginedUser.email;
                add_address.setText(email);
                add_address.setHint("请输入电子邮箱");
                if (!TextUtils.isEmpty(email)) {
                    add_address.setSelection(email.length());
                }
                addressBanner.setVisibility(View.VISIBLE);
                bindPhone_banner.setVisibility(View.GONE);
                break;
            case BINDPHONE:
                bindPhone_banner.setVisibility(View.VISIBLE);
                submit.setText("保存");
                break;
        }
    }

    @OnClick({R.id.button_code, R.id.submit})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_code:
                //手机号验证
                String Phone = StringUtil.trim(et_phone.getText().toString());
                if (UserUtil.checkPhoneNum(Phone)) {

                    boolean rightPhones = UserUtil.checkPhoneNum(Phone);
                    if (!rightPhones) {
                        return;
                    }
                    getBindPhoneCode(Phone);
                }
                break;
            case R.id.submit:
                switch (mAction) {
                    case NICKNAME://用户昵称   待完善。参考详细地址信息
                        String nickName = StringUtil.trim(add_address);
                        if (TextUtils.isEmpty(nickName) || nickName.length() < 2) {
                            ToastUtil.show("请输入2-10个字符、数字的新昵称");
                            return;
                        }
                        loginedUser.nick_name = nickName;
                        doUpdate();
                        break;
                    case EMAILADDRESS:
                        //Email地址
                        String emailAddress = StringUtil.trim(add_address);
                        if (TextUtils.isEmpty(emailAddress)) {
                            ToastUtil.show("Email地址不能为空");
                            return;
                        }
                        boolean rightEmail = RegUtil.isEmail(emailAddress);
                        if (!rightEmail) {
                            ToastUtil.show("请输入正确的Email");
                            return;
                        }
                        loginedUser.email = emailAddress;
                        doUpdate();
                        break;
                    case BINDPHONE:
                        //绑定手机号
                        String phoneCode = StringUtil.trim(et_phoneCode.getText().toString());
                        if (TextUtils.isEmpty(phoneCode)) {
                            ToastUtil.show(R.string.tip_code_null);
                            return;
                        }
                        //手机号验证
                        String bindPhone = StringUtil.trim(et_phone.getText().toString());
                        boolean rightPhone = UserUtil.checkPhoneNum(bindPhone);
                        if (!rightPhone) {
                            return;
                        }
                        //密码
                        String pass = StringUtil.trim(password.getText().toString());
                        if (!UserUtil.checkPwd(pass)) {
                            return;
                        }
                        doBindPhone(bindPhone, phoneCode, pass);
                        break;
                }
                break;
            default:
                break;
        }
    }

    //获取验证码
    private boolean isBindPhoneCode;

    private void getBindPhoneCode(String phone) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show("当前网络不可用");
            return;
        }
        if (isBindPhoneCode) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showBackCancelableDialog(mContext, "正在获取验证码");
        doGetPhoneCode = UserClient.doGetCode(phone, UserClient.CodeAction.BINDPHONE, new ICallback() {
            @Override
            public void start() {
                isBindPhoneCode = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("验证码已发出");
                    UserUtil.setNextTime(System.currentTimeMillis() + INTERVAL);
                    getCodeTimer.start();
                    showFocus(et_phoneCode);
                } else {
                    showToastShort(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                showToastShort("网络请求超时，请重新获取验证码。");
            }

            @Override
            public void end() {
                isBindPhoneCode = false;
                dialog.dissmiss();
            }
        });
    }

    //修改手机号
    private boolean isbindPhone;

    private void doBindPhone(String bindPhone, String phoneCode, String passward) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show("当前网络不可用");
            return;
        }
        if (isbindPhone) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "请稍后...");
        doBindPhoneTask = UserClient.doChangePhone(bindPhone, phoneCode, passward, new ICallback() {
            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("手机号绑定成功");
                    close();
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void start() {
                isbindPhone = true;
            }

            @Override
            public void failure(NetException e) {
                showToastShort("网络请求超时，请重新获取验证码。");
            }

            @Override
            public void end() {
                isbindPhone = false;
                dialog.dissmiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != updateTask) {
            updateTask.stop();
        }
        if (null != doBindPhoneTask) {
            doBindPhoneTask.stop();
        }
        if (null != doGetPhoneCode) {
            doGetPhoneCode.stop();
        }
    }

    //更新个人信息
    private boolean isUpdateUserInfo;

    private void doUpdate() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isUpdateUserInfo) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "请稍后...");
        updateTask = UserClient.doUpdateUserInfo(loginedUser, new ICallback() {
            @Override
            public void success(Object data) {
                if (isDestoryed) return;
                UserResponse response = (UserResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("修改成功");
                    close();
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void start() {
                isUpdateUserInfo = true;
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show("网络请求失败,请稍后重试");
            }

            @Override
            public void end() {
                isUpdateUserInfo = false;
                dialog.dissmiss();
            }
        });
    }

    /**
     * 修改动作
     */
    public static enum UserUpateAction {
        BINDPHONE("修改手机号"),
        EMAILADDRESS("Email地址"),
        NICKNAME("用户昵称");

        private UserUpateAction(String name) {
            this.name = name;
        }

        public String name;
    }

    private void showFocus(View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
