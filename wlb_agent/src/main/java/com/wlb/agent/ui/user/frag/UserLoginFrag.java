package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.android.util.text.StringUtil;
import com.wlb.agent.Constant.IntentAction;
import com.wlb.agent.R;
import com.wlb.agent.common.receiver.PushMsgHandler;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.common.view.CountButton;
import com.wlb.agent.ui.main.TabAct;
import com.wlb.agent.ui.main.frag.MsgFrag;
import com.wlb.agent.ui.order.frag.OrderFrag;
import com.wlb.agent.ui.user.util.UserUtil;
import com.wlb.agent.util.VerifyCode;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.SimpleFragAct.SimpleFragParam;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import common.share.LoginAction;
import common.share.LoginCallBack;
import common.share.LoginInfo;
import common.share.ShareApi;
import common.share.SharePlatform;
import common.widget.dialog.loading.LoadingDialog;

/**
 * 用户登录
 *
 * @author Berfy
 */
public class UserLoginFrag extends SimpleFrag {

    private static final String PARAM_LOGIN_TARGET = "PARAM_LOGIN_TARGET";
    private static final String PARAM_BUNDLE = "PARAM_BUNDLE";
    private Task loginTask, getCodeTask, loginThreeTask;
    private LoginTargetPage loginTargetPage;
    private Bundle valueBundle;
    private int type = 1;//1是验证码登录，2是密码登录

    @BindView(R.id.v_line_code)
    View division_one;
    @BindView(R.id.v_line_pwd)
    View division_two;
    @BindView(R.id.btn_get_code)
    CountButton mBtnGetCode;
    @BindView(R.id.iv_image_code)
    ImageView mIvImageCode;
    @BindView(R.id.tv_login_forget_password)
    TextView mTvFindPwd;
    @BindView(R.id.edit_login_mobile)
    EditText mEditMobile;
    @BindView(R.id.edit_login_image_code)
    EditText mEditImageCode;
    @BindView(R.id.edit_login_code)
    EditText mEditCode;
    @BindView(R.id.edit_login_pwd)
    EditText mEditPwd;
    @BindView(R.id.layout_image_code)
    LinearLayout mLlImageCode;
    @BindView(R.id.layout_code)
    LinearLayout mLlCode;
    @BindView(R.id.layout_pwd)
    LinearLayout mLlPwd;

    public static void start(Context context, LoginTargetPage loginTargetPage) {
        start(context, loginTargetPage, null);
    }

    public static void start(Context context, LoginTargetPage loginTargetPage, Bundle valueBundle) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_LOGIN_TARGET, loginTargetPage);
        if (null != valueBundle) {
            bundle.putBundle(PARAM_BUNDLE, valueBundle);
        }
        SimpleFragParam loginParam = new SimpleFragParam("",
                UserLoginFrag.class, bundle);
        SimpleFragAct.start(context, loginParam);
    }


    public static enum LoginTargetPage {
        /**
         * 打开主页面
         */
        HOMEPAGE(null),
        /**
         * 消息
         */
        MESSAGE(MsgFrag.getStartParam()),
        /**
         * 车险订单
         */
        USER_INSURANCEORDER(new SimpleFragParam(R.string.order, OrderFrag.class)),

        /**
         * 打开WebView
         */
        WEBVIEW_PAGE(new SimpleFragParam("",
                WebViewFrag.class)),
        /**
         * 钱包
         */
        USER_WALLET(WalletSummaryFrag.getStartParam()),
        /**
         * 用户信息
         */
        USER_INFO(UserInfoFrag.getStartParam()),
        /**
         * 修改密码
         */
        USER_PASSWORD(UserChangePasswordFrag.getStartParam()),
        /**
         * 设置
         */
        USER_SETTING(UserChangePasswordFrag.getStartParam());


        public SimpleFragParam param;

        private LoginTargetPage(SimpleFragParam param) {
            this.param = param;
        }

        public void setTitle(String title) {
            this.param.title = title;
        }

        public void setBundle(Bundle bundle) {
            this.param.paramBundle = bundle;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_login_frag;
    }

    @Override
    protected void init(Bundle bundle) {
        if (null != getArguments()) {
            loginTargetPage = (LoginTargetPage) getArguments().getSerializable(
                    PARAM_LOGIN_TARGET);
            valueBundle = getArguments().getBundle(PARAM_BUNDLE);
        }
        if (null != loginTargetPage) {
            ToastUtil.show("请先登录哦！");
        }
        getTitleBar().setVisibility(View.GONE);//隐藏左侧返回按钮
        addAction(IntentAction.USER_REGIST);
        mIvImageCode.setImageBitmap(VerifyCode.getInstance().createBitmap());
        mEditCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    login();
                }
                return false;
            }
        });
        mEditImageCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    login();
                }
                return false;
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        String action = event.getAction();
        if (containsAction(action)) {
            forwardTargetPage();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareApi.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != loginAction) loginAction.release();
        if (null != loginTask) {
            loginTask.stop();
        }
        if (null != getCodeTask) {
            getCodeTask.stop();
        }
        if (null != mBtnGetCode) {
            mBtnGetCode.stop();
        }
        if (null != loginThreeTask) {
            loginThreeTask.stop();
        }
    }

    @OnClick({R.id.title_left_button, R.id.user_select_code, R.id.user_select_psd, R.id.btn_get_code, R.id.user_login,
            R.id.tv_login_forget_password, R.id.user_qq_log, R.id.user_wx_log, R.id.user_wb_log, R.id.iv_image_code, R.id.tv_regist})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_left_button:
                close();
                break;
            case R.id.user_select_code://选择验证码登录
                division_one.setVisibility(View.VISIBLE);
                division_two.setVisibility(View.GONE);
                mLlImageCode.setVisibility(View.VISIBLE);
                mLlCode.setVisibility(View.VISIBLE);
                mLlPwd.setVisibility(View.GONE);
                type = 1;
                break;
            case R.id.user_select_psd://选择密码登录
                division_one.setVisibility(View.GONE);
                division_two.setVisibility(View.VISIBLE);
                mLlImageCode.setVisibility(View.GONE);
                mLlCode.setVisibility(View.GONE);
                mLlPwd.setVisibility(View.VISIBLE);
                type = 2;
                break;
            case R.id.btn_get_code://获取验证码
                String phone = StringUtil.trim(mEditMobile);
                // 检测电话号码
                if (!UserUtil.checkPhoneNum(phone)) {
                    return;
                }
//                String imageCode = StringUtil.trim(mEditImageCode);
//                if (TextUtils.isEmpty(imageCode)) {
//                    ToastUtil.show(R.string.login_tip_code_null);
//                    return;
//                }
//                if (!imageCode.equals(VerifyCode.getInstance().getCode())) {
//                    ToastUtil.show(R.string.login_tip_code);
//                    return;
//                }
                getCode(phone);//获取验证码
                break;
            case R.id.iv_image_code:
                mIvImageCode.setImageBitmap(VerifyCode.getInstance().createBitmap());
                break;
            case R.id.user_login:// 登录
                login();
                break;
            case R.id.tv_login_forget_password://找回密码
                FindPasswordFrag.start(mContext);
                break;
            case R.id.user_qq_log://QQ登录
                loginAction(SharePlatform.QQ, 1);
                break;
            case R.id.user_wb_log://微博登录
                loginAction(SharePlatform.SINA, 3);
                break;
            case R.id.user_wx_log://微信登录
                loginAction(SharePlatform.WEIXIN, 2);
                break;
            case R.id.tv_regist:
                UserRegisterFrag.start(mContext);
                break;
        }
    }

    private void login() {
        String phone = StringUtil.trim(mEditMobile);
        if (1 == type) { //验证码登录
            String code = StringUtil.trim(mEditCode);
            if (!UserUtil.checkPhoneNum(phone)) {
                return;
            }
            if (TextUtils.isEmpty(code)) {
                ToastUtil.show(R.string.tip_code_null);
                return;
            }
//        String imageCode = StringUtil.trim(mEditImageCode);
//        if (TextUtils.isEmpty(imageCode)) {
//            ToastUtil.show(R.string.login_tip_imgcode_null);
//            return;
//        }
//        if (!imageCode.equals(VerifyCode.getInstance().getCode())) {
//            ToastUtil.show(R.string.login_tip_imgcode);
//            return;
//        }
            doLogin(phone, code, null);
        } else if (2 == type) { //密码登录
            String password = StringUtil.trim(mEditPwd);
            if (!UserUtil.checkPhoneNum(phone)) {
                return;
            }
            if (!UserUtil.checkPwd(password)) {
                return;
            }
            doLogin(phone, null, password);
        }
    }

    private LoginAction loginAction;
    private int thirdType;

    private void loginAction(SharePlatform platform, int type) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        this.thirdType = type;
        LoginInfo info = ShareApi.getInstance().getLoginInfo(mContext, platform);
        if (null == info) {
            loginAction = new LoginAction();
            loginAction.setPlatform(platform);
            loginAction.setCallBack(loginCallBack);
            loginAction.login(getActivity());
        } else {
            doThreeLogin(info.uid, info.accessToken);
        }
    }

    LoginCallBack loginCallBack = new LoginCallBack() {

        @Override
        public void onError(Throwable e) {
            ToastUtil.show("登录失败");
        }

        @Override
        public void onComplete(SharePlatform sharePlatform, LoginInfo values) {
//            UserLoginUniteFrag.start(mContext, thirdType);//联合登录
            doThreeLogin(values.uid, values.accessToken);
        }

        @Override
        public void onCancel() {
            ToastUtil.show("取消登录");
        }
    };

    private void showLoginDialog() {
        loadingDialog = new LoadingDialog(getContext(),
                "登录中...").show();
    }

    private void dissmissLoginDialog() {
        if (null != loadingDialog) loadingDialog.dissmiss();
    }

    private LoadingDialog loadingDialog;
    private boolean isLogin;

    private void doLogin(String phone, String verifyCode, String pwd) {
        if (isLogin) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            showToastShort(R.string.net_noconnection);
            return;
        }
        showLoginDialog();
        loginTask = UserClient.doLogin3(phone, verifyCode, new ICallback() {

            @Override
            public void start() {
                isLogin = true;
            }

            public void success(Object data) {
                UserResponse userResponse = (UserResponse) data;
                if (userResponse.isSuccessful()) {
                    loginResult();
                } else {
                    ToastUtil.show(userResponse.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                dissmissLoginDialog();
                isLogin = false;
            }
        });
    }

    //第三方登录
    private boolean isThreeLogin;

    private void doThreeLogin(String useId, String accessToken) {
        if (isThreeLogin) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            showToastShort(R.string.net_noconnection);
            return;
        }
        showLoginDialog();
        loginThreeTask = UserClient.doThreeLogin(thirdType, useId, accessToken, new ICallback() {
            @Override
            public void start() {
                isThreeLogin = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ShareApi.getInstance().release();
//                    ThirdPartyUserService.start(mContext, platform);//获取用户头像
                    loginResult();
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
                isThreeLogin = false;
                dissmissLoginDialog();
            }
        });
    }

    private void loginResult() {
        ToastUtil.show(R.string.login_successful);
        PushMsgHandler.doOpenPush();//获取DeviceToken
        UserResponse loginedUser = UserClient.getLoginedUser();
//        if (TextUtils.isEmpty(loginedUser.nick_name)) {
//            LoginStep.start(mContext);
//            close();
//        } else {
        forwardTargetPage();
//        }
    }

    private void forwardTargetPage() {
//        UserFirstSetPasswordFrag.start(mContext);
        if (null != loginTargetPage) {
            if (loginTargetPage == LoginTargetPage.WEBVIEW_PAGE) { //WebView
                if (null != valueBundle) {
                    String url = valueBundle.getString("URL");
                    WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                    webViewParam.url = url;
                    WebViewFrag.start(mContext, webViewParam);
                }
            } else if (loginTargetPage == LoginTargetPage.HOMEPAGE) { //首页
                startAct(TabAct.class);
            } else if (loginTargetPage == LoginTargetPage.USER_INFO) { //个人信息页
                UserInfoFrag.start(mContext);
            } else {
                SimpleFragAct.start(getActivity(), loginTargetPage.param);
            }
        }
        close();
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
        getCodeTask = UserClient.doGetCode(phone, UserClient.CodeAction.LOGIN, new ICallback() {
            @Override
            public void start() {
                isGetCode = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show(R.string.get_code_sendding);
                    mBtnGetCode.setText("", "s");
                    mBtnGetCode.start(60);
                    showSoftInput(mEditCode);
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

//    private void showFocus(View view) {
//        view.requestFocus();
//        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
//        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//    }


    long backTime;

    @Override
    public boolean onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - backTime < 1 * 1500) {
            close();
        } else {
            backTime = now;
            ToastUtil.show("再按一次返回键退出应用");
            return true;
        }
        return false;

    }
}
