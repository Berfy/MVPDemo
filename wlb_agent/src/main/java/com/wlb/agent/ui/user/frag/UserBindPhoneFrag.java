package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.android.util.text.StringUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.AuthStatus;
import com.wlb.agent.core.data.user.entity.IdAuthInfo;
import com.wlb.agent.core.data.user.response.UserCodeResponse;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.view.CountButton;
import com.wlb.agent.ui.main.TabAct;
import com.wlb.agent.ui.order.frag.OrderFrag;
import com.wlb.agent.ui.team.frag.TeamManagerFrag;
import com.wlb.agent.ui.user.util.UserUtil;
import com.wlb.agent.util.VerifyCode;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.dialog.loading.LoadingDialog;


/**
 * 绑定手机号
 *
 * @author Berfy
 */
public class UserBindPhoneFrag extends SimpleFrag {

    @BindView(R.id.edit_mobile)
    EditText mEditMobile;
    @BindView(R.id.edit_code)
    EditText mEditCode;
    @BindView(R.id.btn_get_code)
    CountButton mBtnGetCode;
    @BindView(R.id.iv_image_code)
    ImageView mIvImageCode;
    private Task getCodeTask, commitTask;
    private static final String PARAM_PAGE = "param_page";
    private BindTargetPage bindTargetPage;

    public static void start(Context context, BindTargetPage bindTargetPage) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM_PAGE, bindTargetPage);
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam("绑定手机号",
                UserBindPhoneFrag.class, bundle);
        SimpleFragAct.start(context, param);
    }

    public static enum BindTargetPage {

        HOMEPAGE(null),
        /**
         * 钱包
         */
        USER_WALLET(null),

        /**
         * 认证
         */
        VERIFY(VerifyFrag.getStartParam(0)),

        /**
         * 我的名片
         */
        BUSINESS_CARD(null),
        /**
         * 个人信息
         */
        USER_INFO(UserInfoFrag.getStartParam()),
        /**
         * 车险订单
         */
        USER_INSURANCEORDER(new SimpleFragAct.SimpleFragParam(R.string.order, OrderFrag.class)),
        /**
         * 团队管理
         */
        TEAM_SUMMARY(TeamManagerFrag.getStartParam()),
        /**
         * 我的二维码
         */
        USER_QRCODE(QrcodeFrag.getStartParam()),
        /**
         * 我的优惠券
         */
        COUPON_LIST(CouponListFrag.getStartParam()),
        /**
         * 我的套餐
         */
        COMBO_LIST(ComboListFrag.getStartParam());

        public SimpleFragAct.SimpleFragParam param;

        private BindTargetPage(SimpleFragAct.SimpleFragParam param) {
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
        return R.layout.user_bind_phone_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mIvImageCode.setImageBitmap(VerifyCode.getInstance().createBitmap());
        if (null != getArguments()) {
            bindTargetPage = (BindTargetPage) getArguments().getSerializable(PARAM_PAGE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != getCodeTask) {
            getCodeTask.stop();
        }
        if (null != commitTask) {
            commitTask.stop();
        }
        if (null != mBtnGetCode) {
            mBtnGetCode.stop();
        }
    }

    @OnClick({R.id.btn_submit, R.id.tv_get_code, R.id.iv_image_code})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_code:
                String phone = mEditMobile.getText().toString();
                if (!UserUtil.checkPhoneNum(phone)) {
                    return;
                }
                doGetCode(phone);
                break;
            case R.id.iv_image_code:
                mIvImageCode.setImageBitmap(VerifyCode.getInstance().createBitmap());
                break;
            case R.id.btn_submit:
                String phoneNo = mEditMobile.getText().toString();
                if (!UserUtil.checkPhoneNum(phoneNo)) {
                    return;
                }
                String code = mEditCode.getText().toString();
                if (StringUtil.isEmpty(code)) {
                    ToastUtil.show("请输入验证码");
                    return;
                }
                doBindPhone(phoneNo, code);
                break;
        }
    }

    private boolean isGetCode;

    private void doGetCode(String phone) {
        if (isGetCode) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        final LoadingDialog dialog = new LoadingDialog(mContext, getString(R.string.getting_code)).show();
        getCodeTask = UserClient.doGetCode(phone, UserClient.CodeAction.ADDPHONE, new ICallback() {
            @Override
            public void start() {
                isGetCode = true;
            }

            @Override
            public void success(Object data) {
                UserCodeResponse response = (UserCodeResponse) data;
                if (response.isSuccessful()) {
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
                isGetCode = false;
                dialog.dissmiss();
            }
        });
    }

    private boolean isBindPhone;

    private void doBindPhone(String phone, String code) {
        if (isBindPhone) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        commitTask = UserClient.doAddPhone(phone, code, new ICallback() {
            @Override
            public void start() {
                isBindPhone = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("绑定成功");
                    bindResult();
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
                isBindPhone = false;
            }
        });
    }

    private void bindResult() {
        if (null != bindTargetPage) {
            if (bindTargetPage == BindTargetPage.HOMEPAGE) { //首页
                startAct(TabAct.class);
            } else if (bindTargetPage == BindTargetPage.USER_WALLET) {
                WalletSummaryFrag.start(mContext);
            } else if (bindTargetPage == BindTargetPage.VERIFY) {
                UserAuthFrag.start(mContext);
            } else if (bindTargetPage == BindTargetPage.USER_INFO) {
                UserInfoFrag.start(mContext);
            } else if (bindTargetPage == BindTargetPage.USER_INSURANCEORDER) {
                OrderFrag.start(mContext);
            } else if (bindTargetPage == BindTargetPage.TEAM_SUMMARY) {
                TeamManagerFrag.start(mContext);
            } else if (bindTargetPage == BindTargetPage.BUSINESS_CARD) {
                UserResponse loginedUser = UserClient.getLoginedUser();
                IdAuthInfo id_auth_info = loginedUser.id_auth_info;
                if (null != id_auth_info && id_auth_info.getAuthStatus() == AuthStatus.AUTH_SUCCESS) {
                    //实名认证通过
                    UserInfoCardFrag.start(mContext);
                } else {
                    ToastUtil.show(R.string.user_auth_tip);
                    UserAuthFrag.start(mContext);
                }
            }
        }
        close();
    }
}
