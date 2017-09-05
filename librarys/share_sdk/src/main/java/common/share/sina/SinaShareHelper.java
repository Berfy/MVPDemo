package common.share.sina;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import com.sina.weibo.sdk.utils.Utility;

import common.share.CShareListener;
import common.share.IShare;
import common.share.LoginCallBack;
import common.share.LoginInfo;
import common.share.PlatformConfig;
import common.share.PlatformConfig.SinaWeibo;
import common.share.ShareAction;
import common.share.ShareApi;
import common.share.ShareImage;
import common.share.SharePlatform;
import common.share.ShareUserInfo;
import common.share.UserInfoCallback;

public class SinaShareHelper implements IShare {
    private Activity ctx;
    private SinaWeibo sinaWeibo;
    public ShareAction shareAction;

    public static final int SHARE_CLIENT = 1;
    public static final int SHARE_ALL_IN_ONE = 2;
    private int mShareType = SHARE_ALL_IN_ONE;
    /**
     * 微博分享的接口实例
     */
    private IWeiboShareAPI mWeiboShareAPI;

    public SinaShareHelper(Activity ctx) {
        this.ctx = ctx;
        this.sinaWeibo = (SinaWeibo) PlatformConfig.getPlatform(SharePlatform.SINA);
    }

    public IWeiboShareAPI createWeiboShareAPI(Activity ctx) {
        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(ctx, sinaWeibo.appKey);

        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        mWeiboShareAPI.registerApp();
        return mWeiboShareAPI;
    }

    @Override
    public void share(ShareAction action) {
        this.shareAction = action;

        Oauth2AccessToken savedAccessToken = AccessTokenKeeper.readAccessToken(ctx);
        if (null == savedAccessToken
                || TextUtils.isEmpty(savedAccessToken.getToken())
                || !savedAccessToken.isSessionValid()) {
            // 先授权后分享
            loginCallBack = new LoginCallBack() {

                @Override
                public void onError(Throwable e) {
                    if (null != shareAction.getCallBack()) {
                        shareAction.getCallBack().onError(SharePlatform.SINA, e);
                    }
                }

                @Override
                public void onComplete(SharePlatform sharePlatform, LoginInfo loginInfo) {
                    ctx.startActivity(new Intent(ctx, WBShareActivity.class));
                }

                @Override
                public void onCancel() {
                    if (null != shareAction.getCallBack()) {
                        shareAction.getCallBack().onCancel(SharePlatform.SINA);
                    }
                }
            };
            login(shareAction.getPlatform(), loginCallBack);
        } else {
            ctx.startActivity(new Intent(ctx, WBShareActivity.class));
        }
    }


    public void share(Activity activity) {

        String text = null;
        Bitmap bitmap = null;
        if (!shareAction.isOnlyImage()) {
            text = shareAction.getText();
            String targetUrl = shareAction.getTargetUrl();
            if (!TextUtils.isEmpty(targetUrl)) {
                text += targetUrl;
            }
        }
        ShareImage shareImage = shareAction.getShareImage();
        if (null != shareImage) {
            String imgUrl = shareImage.getImgUrls().get(0);
            if (!TextUtils.isEmpty(imgUrl)) {
                bitmap = BitmapFactory.decodeFile(imgUrl);
            }
        }
        sendMessage(activity, text, bitmap, null);
    }

    public void handleShareResponse(BaseResponse baseResp) {
        if (null == shareAction || null == shareAction.getCallBack()) {
            return;
        }
        CShareListener callBack = shareAction.getCallBack();
        SharePlatform platform = shareAction.getPlatform();
        if (baseResp != null) {
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    callBack.onResult(platform);
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    callBack.onCancel(platform);
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    callBack.onError(platform,
                            new Throwable(null != baseResp ? baseResp.errMsg : "分享失败"));
                    break;
            }
        }
    }

    // --------------------分享-----------------------------------------

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     *
     * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
     */
    private void sendMessage(Activity activity, String text, Bitmap bitmap,
                             String targetUrl) {
        boolean weiboAppInstalled = mWeiboShareAPI.isWeiboAppInstalled();
        // if (weiboAppInstalled) {
        // mShareType = SHARE_CLIENT;
        // }
        if (mShareType == SHARE_CLIENT) {
            if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
                int supportApi = mWeiboShareAPI.getWeiboAppSupportAPI();
                System.out.println("supportApi=" + supportApi);
                if (supportApi >= 10351) {
                    sendMultiMessage(activity, text, bitmap, targetUrl);
                } else {
                    sendSingleMessage(activity, text, bitmap, targetUrl);
                }
            } else {
                Toast.makeText(activity.getApplicationContext(), "微博客户端不支持 SDK 分享或微博客户端未安装或微博客户端是非官方版本。", Toast.LENGTH_SHORT).show();
            }
        } else if (mShareType == SHARE_ALL_IN_ONE) {
            sendMultiMessage(activity, text, bitmap, targetUrl);
        }
    }

    private void sendMultiMessage(Activity activity, String text, Bitmap bitmap, String targetUrl) {

        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (!TextUtils.isEmpty(text)) {
            weiboMessage.textObject = getTextObj(text);
        }

        if (null != bitmap) {
            weiboMessage.imageObject = getImageObj(bitmap);
        }

        // 用户可以分享其它媒体资源（网页、音乐、视频、声音中的一种）
        if (!TextUtils.isEmpty(targetUrl)) {
            weiboMessage.mediaObject = getWebpageObj(targetUrl);
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        if (mShareType == SHARE_CLIENT) {
            mWeiboShareAPI.sendRequest(activity, request);
        } else if (mShareType == SHARE_ALL_IN_ONE) {
            AuthInfo authInfo = new AuthInfo(activity, sinaWeibo.appKey, sinaWeibo.redirectUrl, SinaWeibo.SCOPE);
            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(activity.getApplicationContext());
            String token = "";
            if (accessToken != null) {
                token = accessToken.getToken();
            }
            mWeiboShareAPI.sendRequest(activity, request, authInfo, token, new WeiboAuthListener() {

                @Override
                public void onWeiboException(WeiboException arg0) {
                    if (null != shareAction && null != shareAction.getCallBack()) {
                        shareAction.getCallBack().onError(SharePlatform.SINA, arg0);
                    }
                }

                @Override
                public void onComplete(Bundle bundle) {
                    Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                    AccessTokenKeeper.writeAccessToken(ctx.getApplicationContext(), newToken);

                    if (null != shareAction && null != shareAction.getCallBack()) {
                        shareAction.getCallBack().onResult(SharePlatform.SINA);
                    }
                }

                @Override
                public void onCancel() {
                    if (null != shareAction && null != shareAction.getCallBack()) {
                        shareAction.getCallBack().onCancel(SharePlatform.SINA);
                    }
                }
            });
        }
    }

    private void sendSingleMessage(Activity activity, String text, Bitmap bitmap, String targetUrl) {

        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        if (!TextUtils.isEmpty(text)) {
            weiboMessage.mediaObject = getTextObj(text);
        }
        if (null != bitmap) {
            weiboMessage.mediaObject = getImageObj(bitmap);
        }
        if (!TextUtils.isEmpty(targetUrl)) {
            weiboMessage.mediaObject = getWebpageObj(targetUrl);
        }

        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;

        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(activity, request);
    }

    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private TextObject getTextObj(String text) {
        TextObject textObject = new TextObject();
        textObject.text = text;
        return textObject;
    }

    /**
     * 创建图片消息对象。
     *
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(Bitmap bitmap) {
        ImageObject imageObject = new ImageObject();
        // 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        imageObject.setImageObject(bitmap);
        return imageObject;
    }

    /**
     * 创建多媒体（网页）消息对象。
     *
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj(String actionUrl) {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = "";
        mediaObject.description = "";

        // Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
        // R.drawable.ic_logo);
        // // 设置 Bitmap 类型的图片到视频对象里 设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        // mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = actionUrl;
        mediaObject.defaultText = "";
        return mediaObject;
    }

    // ----------------登录----------------------------------------------
    private SsoHandler mSsoHandler;
    private LoginCallBack loginCallBack;

    @Override
    public void login(SharePlatform sharePlatform, LoginCallBack loginCallBack) {
        this.loginCallBack = loginCallBack;

        LoginInfo loginInfo = ShareApi.getInstance().getLoginInfo(ctx, SharePlatform.SINA);
        if (null != loginInfo) {
            loginCallBack.onComplete(sharePlatform, loginInfo);
        } else {
            mWeiboShareAPI = createWeiboShareAPI(ctx);
//			if(!mWeiboShareAPI.isWeiboAppInstalled()){
//				Toast.makeText(ctx,"请先安装微博客户端",Toast.LENGTH_SHORT).show();
//				loginCallBack.onError(new NullPointerException("请先安装微博客户端"));
//				return;
//			}
            // 创建微博实例
            // mWeiboAuth = new WeiboAuth(this, Constants.APP_KEY,
            // Constants.REDIRECT_URL, Constants.SCOPE);
            // 快速授权时，请不要传入 SCOPE，否则可能会授权不成功
            AuthInfo mAuthInfo = new AuthInfo(ctx, sinaWeibo.appKey, sinaWeibo.redirectUrl, SinaWeibo.SCOPE);
            mSsoHandler = new SsoHandler(ctx, mAuthInfo);
            mSsoHandler.authorize(new AuthListener());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResults
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    /**
     * 微博认证授权回调类。 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用
     * {@link SsoHandler#authorizeCallBack} 后， 该回调才会被执行。 2. 非 SSO
     * 授权时，当授权结束后，该回调就会被执行。 当授权成功后，请保存该 access_token、expires_in、uid 等信息到
     * SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {

        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            Oauth2AccessToken mAccessToken = Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
                AccessTokenKeeper.writeAccessToken(ctx, mAccessToken);
                if (null != loginCallBack) {
                    LoginInfo loginInfo = new LoginInfo();
                    loginInfo.uid = mAccessToken.getUid();
                    loginInfo.accessToken = mAccessToken.getToken();
                    loginCallBack.onComplete(shareAction.getPlatform(), loginInfo);
                }
            } else {
                // 以下几种情况，您会收到 Code：
                // 1. 当您未在平台上注册的应用程序的包名与签名时；
                // 2. 当您注册的应用程序包名与签名不正确时；
                // 3. 当您在平台上注册的包名和签名与您当前测试的应用的包名和签名不匹配时。
                String code = values.getString("code");
                String message = "授权失败";
                if (!TextUtils.isEmpty(code)) {
                    message = message + "\nObtained the code: " + code;
                }
                if (null != loginCallBack) {
                    loginCallBack.onError(new Throwable(message));
                }

            }
        }

        @Override
        public void onCancel() {
            if (null != loginCallBack) {
                loginCallBack.onCancel();
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            if (null != loginCallBack) {
                loginCallBack.onError(new Throwable(e));
            }
        }
    }

    //----------------------获取用户信息
    UserInfoCallback userInfoCallback;

    @Override
    public void getUserInfo(UserInfoCallback callback) {
        // 获取当前已保存过的 Token
        Oauth2AccessToken mAccessToken = AccessTokenKeeper.readAccessToken(ctx);
        if (null != mAccessToken && mAccessToken.isSessionValid()) {
            userInfoCallback = callback;
            // 获取用户信息接口
            UsersAPI mUsersAPI = new UsersAPI(ctx, sinaWeibo.appKey, mAccessToken);
            long uid = Long.parseLong(mAccessToken.getUid());
            mUsersAPI.show(uid, mListener);
        }
    }

    /**
     * 微博 OpenAPI 回调接口。
     */
    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                // 调用 User#parse 将JSON串解析成User对象
                User user = User.parse(response);
                if (user != null) {
                    ShareUserInfo shareUserInfo = new ShareUserInfo();
                    shareUserInfo.nickName = user.screen_name;
                    shareUserInfo.avater = user.avatar_large;
                    if (null != userInfoCallback) userInfoCallback.success(shareUserInfo);
                } else {
                    if (null != userInfoCallback)
                        userInfoCallback.fail(new RuntimeException(response));
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            System.out.println("onWeiboException,e=" + e);
            if (null != userInfoCallback)
                userInfoCallback.fail(new RuntimeException(e.getMessage()));
        }
    };

    //----------------------------------------------------

    @Override
    public void release() {
        ctx = null;
        loginCallBack = null;
        shareAction = null;
        userInfoCallback = null;
        mListener = null;
    }

}
