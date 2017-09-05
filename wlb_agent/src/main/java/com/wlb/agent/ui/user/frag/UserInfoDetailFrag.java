package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.ThirdParty;
import com.wlb.agent.core.data.user.response.ThirdPartyBindResponse;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.common.view.PhotoPickSheet;
import com.wlb.agent.ui.user.util.UserUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.SimpleFragAct.SimpleFragParam;
import com.wlb.common.imgloader.ImageFactory;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.share.LoginAction;
import common.share.LoginCallBack;
import common.share.LoginInfo;
import common.share.ShareApi;
import common.share.SharePlatform;
import common.widget.dialog.loading.LoadingDialog;


/**
 * 用户信息
 *
 * @author 曹泽琛
 */
public class UserInfoDetailFrag extends SimpleFrag implements View.OnClickListener {
    private Task getUserInfoTask;
    private DisplayImageOptions imageOptions;
    @BindView(R.id.user_nickName)
    TextView tv_nickName;//昵称
    @BindView(R.id.wx)
    TextView WX;//微信
    @BindView(R.id.qq)
    TextView QQ;//QQ
    @BindView(R.id.sina)
    TextView Sina;//微博
    @BindView(R.id.user_email)
    TextView tv_email;//邮箱
    @BindView(R.id.user_phone)
    TextView tv_phone;//手机号
    @BindView(R.id.user_head_img)
    ImageView img_head;//头像信息
    @BindView(R.id.select_city)
    TextView tv_city;//选择城市
    @BindView(R.id.real_name)
    TextView tv_realName;//实名认证
    private Task doUnBindTask;
    private Task bindTask;
    private PhotoPickSheet photoPickSheet;

    public static SimpleFragParam getStartParam() {
        SimpleFragParam simpleFragParam = new SimpleFragParam(R.string.usercenter_userinfo, UserInfoDetailFrag.class);
        return simpleFragParam;
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_info_detail_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        photoPickSheet = new PhotoPickSheet(this);
        photoPickSheet.setReqSize(150);
        photoPickSheet.uploadPhoto(true);
        photoPickSheet.setOnPhotoUploadListener(onPhotoUploadListener);

        imageOptions = ImageFactory.getImageOptions(R.drawable.sliding_head);

        doGetUserInfo();

        //绑定第三方相关
        addAction(Constant.IntentAction.USER_CITY);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        String action = event.getAction();
        if (containsAction(action)) {
            if (Constant.IntentAction.USER_CITY.equalsIgnoreCase(action)) {
                String city = (String) event.getData();
                UserResponse loginedUser = UserClient.getLoginedUser();
                loginedUser.city = city;
                doUpdateUserCity(loginedUser);
            }
        }
    }

    //显示第三方账号是否被绑定
    private void setBindInfo(List<ThirdParty> dataList) {
        for (int i = 0; i < dataList.size(); i++) {
            ThirdParty thirdParty = dataList.get(i);
            updateUI(thirdParty.type, thirdParty.isBind());
        }
    }

    private void updateUI(int thridType, boolean isBind) {
        String bindStr = isBind ? "已绑定" : "未绑定";
        if (thridType == 1) {
            QQ.setText(bindStr);
            QQ.setSelected(isBind);
        } else if (thridType == 2) {
            WX.setText(bindStr);
            WX.setSelected(isBind);
        } else if (thridType == 3) {
            Sina.setText(bindStr);
            Sina.setSelected(isBind);
        }
    }

    private boolean isUpdateUserInfo;
    private Task updateTask;

    private void doUpdateUserCity(final UserResponse user) {
        if (isUpdateUserInfo) {
            return;
        }
        updateTask = UserClient.doUpdateUserInfo(user, new ICallback() {
            @Override
            public void success(Object data) {
                UserResponse response = (UserResponse) data;
                if (response.isSuccessful()) {
                    tv_city.setText(user.city);
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
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                isUpdateUserInfo = false;
            }
        });
    }

    /**
     * 解除绑定第三方账号
     */
    private boolean isUnbind;

    private void doUnBindThirdParty(final int thirdPartyType, String thirdPartyUserId) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isUnbind) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "取消绑定中");
        doUnBindTask = UserClient.doUnbindThirdParty(thirdPartyType, thirdPartyUserId, new ICallback() {
            @Override
            public void start() {
                isUnbind = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    updateUI(thirdPartyType, false);
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
                isUnbind = false;
                dialog.dissmiss();
            }
        });
    }

    PhotoPickSheet.OnPhotoUploadListener onPhotoUploadListener = new PhotoPickSheet.OnPhotoUploadListener() {

        @Override
        public void uploadSuccess(View view, String localUrl, String url, Bitmap bitmap) {
            if (!url.isEmpty()) {
                ImageFactory.getUniversalImpl().getImg(url, img_head,
                        null, imageOptions);
            } else {
                img_head.setImageResource(R.drawable.sliding_head);
            }
        }

        @Override
        public void local(String localUrl, Bitmap bitmap) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        setUserInfo();
    }

    private void setUserInfo() {
        UserResponse userInfo = UserClient.getLoginedUser();
        if (null != userInfo) {
            if (!TextUtils.isEmpty(userInfo.avatar)) {
                ImageFactory.getUniversalImpl().getImg(userInfo.avatar, img_head, null, imageOptions);
            }
            tv_email.setText(userInfo.email);//Email
            //手机号
            String user_phone = userInfo.phone;
            if (!TextUtils.isEmpty(user_phone)) {
                tv_phone.setText(UserUtil.getEncodedPhone(user_phone));
            }
            //所在城市
            String user_city = userInfo.city;
            if (!TextUtils.isEmpty(user_city)) {
                tv_city.setText(user_city);
            }
            //昵称 nickName
            String nick_name = userInfo.nick_name;
            if (!TextUtils.isEmpty(nick_name)) {
                tv_nickName.setText(nick_name);
            }
            //是否实名认证
            int auth_status = 0;
            if (null != userInfo.id_auth_info) {
                auth_status = userInfo.id_auth_info.getAuthStatus().statusCode;
            }
            String msg = "";
            if (auth_status == 1) {
                msg = "审核中";
            } else if (auth_status == 0) {
                msg = "未认证";
            } else if (auth_status == 2) {
                msg = "审核失败";
            } else if (auth_status == 3) {
                msg = "已认证";
            }
            tv_realName.setText(msg);
            //第三方账号相关
            List<ThirdParty> arrEntity = userInfo.third_party;
            setBindInfo(arrEntity);
        }

    }

    private boolean isGetUserInfo;

    private void doGetUserInfo() {
        if (isGetUserInfo) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }

        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "请稍后...");
        getUserInfoTask = UserClient.doGetUserInfo(new ICallback() {

            @Override
            public void success(Object data) {
                UserResponse response = (UserResponse) data;
                if (response.isSuccessful()) {
                    setUserInfo();
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void start() {
                isGetUserInfo = true;
            }

            @Override
            public void failure(NetException e) {
            }

            @Override
            public void end() {
                isGetUserInfo = false;
                dialog.dissmiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ShareApi.getInstance().release();
        if (null != getUserInfoTask) {
            getUserInfoTask.stop();
        }
        if (null != doUnBindTask) {
            doUnBindTask.stop();
        }
        if (null != bindTask) {
            bindTask.stop();
        }
        if (null != updateTask) {
            updateTask.stop();
        }
        onPhotoUploadListener = null;
        photoPickSheet.release();
    }

    @OnClick({R.id.changePassword_banner, R.id.nickName_banner, R.id.bindWX_banner, R.id.bindQQ_banner,
            R.id.bindSina_banner, R.id.realName_banner, R.id.user_email_banner, R.id.user_phone_banner,
            R.id.user_head_banner, R.id.city_banner})
    public void onClick(View v) {
        String uid = null;
        switch (v.getId()) {
            case R.id.bindWX_banner://微信绑定
                if (WX.isSelected()) {
                    LoginInfo loginInfo = ShareApi.getInstance().getLoginInfo(mContext, SharePlatform.WEIXIN);
                    if (null != loginInfo) {
                        uid = loginInfo.uid;
                    }
                    doUnBindThirdParty(2, uid);
                } else {
                    loginAction(SharePlatform.WEIXIN, 2);
                }
                break;
            case R.id.bindQQ_banner://QQ绑定
                if (QQ.isSelected()) {
                    LoginInfo loginInfo = ShareApi.getInstance().getLoginInfo(mContext, SharePlatform.QQ);
                    if (null != loginInfo) {
                        uid = loginInfo.uid;
                    }
                    doUnBindThirdParty(1, uid);
                } else {
                    loginAction(SharePlatform.QQ, 1);
                }
                break;
            case R.id.bindSina_banner://微博绑定
                if (Sina.isSelected()) {
                    LoginInfo loginInfo = ShareApi.getInstance().getLoginInfo(mContext, SharePlatform.SINA);
                    if (null != loginInfo) {
                        uid = loginInfo.uid;
                    }
                    doUnBindThirdParty(3, uid);
                } else {
                    loginAction(SharePlatform.SINA, 3);
                }
                break;
            case R.id.user_email_banner://Email地址
                UserUpdateInfoFrag.start(mContext, UserUpdateInfoFrag.UserUpateAction.EMAILADDRESS);
                break;
            case R.id.user_phone_banner://绑定手机号
                UserUpdateInfoFrag.start(mContext, UserUpdateInfoFrag.UserUpateAction.BINDPHONE);
                break;
            case R.id.user_head_banner://更换头像
                photoPickSheet.show();
                break;
            case R.id.changePassword_banner://修改密码
                UserChangePasswordFrag.start(mContext);
                break;
            case R.id.nickName_banner://用户昵称
                UserUpdateInfoFrag.start(mContext, UserUpdateInfoFrag.UserUpateAction.NICKNAME);
                break;
            case R.id.realName_banner://实名认证
                ProveNameFrag.start(mContext);
                break;
            case R.id.city_banner:// 所在城市
                WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.title = "选择城市";
                webViewParam.url = H5.SELECT_CITY;
                WebViewFrag.start(mContext, webViewParam);
                break;
        }
    }

    private boolean isBind;

    private void doBind(LoginInfo loginInfo) {
        if (isBind) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(getActivity())) {
            showToastShort(R.string.net_noconnection);
            return;
        }

        UserResponse userInfo = UserClient.getLoginedUser();
        bindTask = UserClient.doBindThirdParty(userInfo, thirdType, loginInfo.uid, loginInfo.accessToken, new ICallback() {

            @Override
            public void start() {
                isBind = true;
            }

            public void success(Object data) {
                ThirdPartyBindResponse response = (ThirdPartyBindResponse) data;
                if (response.isSuccessful()) {
                    updateUI(thirdType, true);
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
                isBind = false;
            }
        });
    }

    //--------------------------登录-------------------
    private int thirdType;

    private void loginAction(SharePlatform platform, int type) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        this.thirdType = type;
        LoginInfo info = ShareApi.getInstance().getLoginInfo(mContext, platform);
        if (null == info) {
            LoginAction loginAction = new LoginAction();
            loginAction.setPlatform(platform);
            loginAction.setCallBack(loginCallBack);
            loginAction.login(getActivity());
        } else {
            doBind(info);
        }
    }

    LoginCallBack loginCallBack = new LoginCallBack() {

        @Override
        public void onError(Throwable e) {
            ToastUtil.show(getString(R.string.other_login_bind_error) + "，" + e.getMessage());
        }

        @Override
        public void onComplete(SharePlatform sharePlatform, LoginInfo values) {
            doBind(values);
        }

        @Override
        public void onCancel() {
            ToastUtil.show(getString(R.string.other_login_cancel));
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (photoPickSheet.isShowing()) {
            photoPickSheet.onActivityResult(requestCode, resultCode, data);
        }
        ShareApi.getInstance().onActivityResult(requestCode, resultCode, data);
    }
}
