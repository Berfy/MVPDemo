package common.share;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.open.utils.ThreadManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class QQShareHelper implements IShare {
    private int shareType;
    private ShareAction shareAction;
    private SharePlatform sharePlatform;
    private Activity ctx;
    public Tencent mTencent;
    private UserInfo mInfo;

    public QQShareHelper(Activity ctx) {
        this.ctx = ctx;
        checkTecentInited();
    }

    /**
     * 分享到QQ
     *
     * @param shareAction
     */
    private void share2QQ(ShareAction shareAction) {
        // 纯图分享只能支持本地图片
        shareType = QQShare.SHARE_TO_QQ_TYPE_DEFAULT;
        if (shareAction.isOnlyImage()) {
            shareType = QQShare.SHARE_TO_QQ_TYPE_IMAGE;
        }
        final Bundle params = new Bundle();
        if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
            params.putString(QQShare.SHARE_TO_QQ_TITLE, shareAction.getTitle());
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareAction.getTargetUrl());
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareAction.getText());
        }

        // 分享图片
        String imgUrl = null;
        ShareImage shareImage = shareAction.getShareImage();
        if (null != shareImage) {
            imgUrl = shareImage.getImgUrls().get(0);
            if (shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imgUrl);
            } else {
                params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);
            }
        }

        params.putString(shareType == QQShare.SHARE_TO_QQ_TYPE_IMAGE ? QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL
                : QQShare.SHARE_TO_QQ_IMAGE_URL, imgUrl);

        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, shareAction.getAppName());
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, shareType);
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, 0x00);

        // QQ分享要在主线程做
        ThreadManager.getMainHandler().post(new Runnable() {
            @Override
            public void run() {
                if (null != mTencent) {
                    mTencent.shareToQQ(ctx, params, qqShareListener);
                }
            }
        });
    }

    /**
     * 分享到QQ空间
     *
     * @param shareAction
     */
    private void share2QQZone(ShareAction shareAction) {
        // 图文分享
        shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;
        // 纯图分享
        if (shareAction.isOnlyImage()) {
            shareType = QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE;
        }
        final Bundle params = new Bundle();
        // 分享类型
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, shareType);
        // 分享标题
        String title = shareAction.getTitle();
        if (!TextUtils.isEmpty(title)) {
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
        }
        // 分享文本
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareAction.getText());

        // 分享链接
        String targetUrl = shareAction.getTargetUrl();
        if (!TextUtils.isEmpty(targetUrl)) {
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
        }
        // 支持传多个imageUrl
        ShareImage shareImage = shareAction.getShareImage();
        ArrayList<String> imgUrls = new ArrayList<String>();
        if (null != shareImage) {
            imgUrls = shareImage.getImgUrls();
        }
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgUrls);

        if (shareType == QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT) {
            ThreadManager.getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (null != mTencent) {
                        mTencent.shareToQzone(ctx, params, qqShareListener);
                    }
                }
            });
        } else {
            ThreadManager.getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (null != mTencent) {
                        mTencent.publishToQzone(ctx, params, qqShareListener);
                    }
                }
            });
        }
    }

    IUiListener qqShareListener = new IUiListener() {
        @Override
        public void onCancel() {
            // if (shareType != QQShare.SHARE_TO_QQ_TYPE_IMAGE) {
            if (null != shareAction.getCallBack()) {
                shareAction.getCallBack().onCancel(shareAction.getPlatform());
            }
            // }
        }

        @Override
        public void onComplete(Object response) {
            if (null != shareAction.getCallBack()) {
                shareAction.getCallBack().onResult(shareAction.getPlatform());
            }
        }

        @Override
        public void onError(UiError e) {
            if (null != shareAction.getCallBack()) {
                shareAction.getCallBack().onError(shareAction.getPlatform(), null != e ? new Throwable(e.errorMessage)
                        : null);
            }
        }
    };

    @Override
    public void share(ShareAction shareAction) {
        sharePlatform = shareAction.getPlatform();
        this.shareAction = shareAction;
        boolean qqShare = shareAction.getPlatform() == SharePlatform.QQ;
        if (qqShare) {
            share2QQ(shareAction);
        } else {
            share2QQZone(shareAction);
        }
    }

    @Override
    public void release() {
        ctx = null;
        loginCallBack = null;
        if (mTencent != null) {
            mTencent.releaseResource();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_QQ_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
        } else if (requestCode == Constants.REQUEST_QZONE_SHARE) {
            Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
        } else if (requestCode == Constants.REQUEST_LOGIN ||
                requestCode == Constants.REQUEST_APPBAR) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
        }
    }

    private void checkTecentInited() {
        if (mTencent == null) {
            PlatformConfig.QQZone platform = (PlatformConfig.QQZone) PlatformConfig.getPlatform(SharePlatform.QQ);
            mTencent = Tencent.createInstance(platform.appId, ctx);
        }
    }

    // ------------------------QQ登录---------------------
    private LoginCallBack loginCallBack;

    @Override
    public void login(SharePlatform sharePlatform, LoginCallBack callBack) {
        this.sharePlatform = sharePlatform;
        this.loginCallBack = callBack;

        LoginInfo loginInfo = ShareApi.getInstance().getLoginInfo(ctx, SharePlatform.QQ);
        if (null != loginInfo) {
            loginCallBack.onComplete(sharePlatform, loginInfo);
        } else {
            mTencent.login(ctx, "all", loginListener);
        }
    }

    public void loginOut() {
        if (mTencent.isSessionValid()) {
            mTencent.logout(ctx);
        }
    }

    IUiListener loginListener = new BaseUiListener() {
        @Override
        protected void doComplete(JSONObject values) {
            Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
            initOpenidAndToken(values);
            if (null != loginCallBack) {
                LoginInfo loginInfo = new LoginInfo();
                loginInfo.uid = mTencent.getOpenId();
                loginInfo.accessToken = mTencent.getAccessToken();
                loginCallBack.onComplete(sharePlatform, loginInfo);
            }
            // getUserInfo();
        }
    };

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                if (null != loginCallBack) {
                    loginCallBack.onError(new Throwable("登陆失败"));
                }
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                if (null != loginCallBack) {
                    loginCallBack.onError(new Throwable("登陆失败"));
                }
                return;
            }
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            if (null != loginCallBack) {
                loginCallBack.onError(new Throwable(e.errorDetail));
            }
        }

        @Override
        public void onCancel() {
            if (null != loginCallBack) {
                loginCallBack.onCancel();
            }
        }
    }

    private void initOpenidAndToken(JSONObject jsonObject) {
        try {
            String token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            String expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            String openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires)
                    && !TextUtils.isEmpty(openId)) {
                mTencent.setAccessToken(token, expires);
                mTencent.setOpenId(openId);
            }
        } catch (Exception e) {
        }
    }

    // --------------------------
    private UserInfoCallback userInfoCallback;

    /**
     * 获取用户信息
     */
    public void getUserInfo(UserInfoCallback callback) {
        checkTecentInited();
        this.userInfoCallback = callback;
        IUiListener listener = new IUiListener() {

            @Override
            public void onError(UiError e) {
                userInfoCallback.fail(new RuntimeException(e.errorMessage));
            }

            @Override
            public void onComplete(final Object response) {
                try {
                    ShareUserInfo shareUserInfo = new ShareUserInfo();
                    JSONObject json = (JSONObject) response;
                    if (json.has("nickname")) {
                        shareUserInfo.nickName = json.getString("nickname");
                    }
                    if (json.has("figureurl")) {
                        shareUserInfo.avater = json.getString("figureurl_qq_2");
                    }
                    userInfoCallback.success(shareUserInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                    userInfoCallback.fail(new RuntimeException(e.getMessage()));
                }
            }

            @Override
            public void onCancel() {

            }
        };
        mInfo = new UserInfo(ctx, mTencent.getQQToken());
        mInfo.getUserInfo(listener);
    }
}
