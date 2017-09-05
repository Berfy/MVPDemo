package common.share.wx;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXAppExtendObject;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import common.share.CShareListener;
import common.share.IShare;
import common.share.LoginCallBack;
import common.share.LoginInfo;
import common.share.PlatformConfig;
import common.share.PlatformConfig.Weixin;
import common.share.ShareAction;
import common.share.ShareImage;
import common.share.SharePlatform;
import common.share.ShareUserInfo;
import common.share.UserInfoCallback;

public class WXShareHelper implements IShare {
    private static final int THUMB_SIZE = 150;
    private Activity ctx;
    private ShareAction shareAction;
    private IWXAPI api;

    public WXShareHelper(Activity ctx) {
        this.ctx = ctx;
        Weixin weixin = (Weixin) PlatformConfig.getPlatform(SharePlatform.WEIXIN);
        api = WXAPIFactory.createWXAPI(ctx, weixin.appId);
    }


    @Override
    public void share(ShareAction shareAction) {
        this.shareAction = shareAction;
        // 是否分享到朋友圈
        boolean isTimeline = shareAction.getPlatform() == SharePlatform.WEIXIN_CIRCLE;

        String imgPath = null;
        if (null != shareAction.getShareImage()) {
            ShareImage shareImage = shareAction.getShareImage();
            imgPath = shareImage.getImgUrls().get(0);
        }

        boolean onlyImage = shareAction.isOnlyImage();
        if (onlyImage) {
            // 纯图片分享
            sendImage(imgPath, isTimeline);
        } else {
            sendMessage(shareAction.getTitle(), shareAction.getText(), imgPath, shareAction.getTargetUrl(), isTimeline);
        }
    }

    @Override
    public void getUserInfo(final UserInfoCallback callback) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                mHandler = new UIHandler(shareAction, callback);
                String uid = WXTokenKeeper.getUid(ctx);
                String accessToken = WXTokenKeeper.getAccessToken(ctx);
                String path = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid="
                        + uid;
                /*
                 * { "openid":"OPENID", "nickname":"NICKNAME", "sex":1,
				 * "province":"PROVINCE", "city":"CITY", "country":"COUNTRY",
				 * "headimgurl":
				 * "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0"
				 * , "privilege":[ "PRIVILEGE1", "PRIVILEGE2" ], "unionid":
				 * " o6_bmasdasdsad6_2sgVt7hMZOPfL"
				 * 
				 * }
				 */
                try {
                    String data = WxUtil.doHttpRequest(path);
                    if (TextUtils.isEmpty(data)) {
                        mHandler.fail(new Exception("获取用户信息失败,data=null"));
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(data);
                    String nickname = jsonObject.optString("nickname");
                    String headimgurl = jsonObject.optString("headimgurl");
                    ShareUserInfo shareUserInfo = new ShareUserInfo();
                    shareUserInfo.avater = headimgurl;
                    shareUserInfo.nickName = nickname;
                    mHandler.getInfoSuccess(shareUserInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.fail(e);
                }
            }
        }).start();

    }

    // ---------------------------分享
    private void sendText(String title, String description, boolean isTimeline) {
        WXTextObject textObj = new WXTextObject();
        textObj.text = title;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = description;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text");
        req.message = msg;
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;

        api.sendReq(req);

    }

    private void sendImage(Bitmap bmp, boolean isTimeline) {

        WXImageObject imgObj = new WXImageObject(bmp);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
        bmp.recycle();
        msg.thumbData = WxUtil.bmpToByteArray(thumbBmp, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    private void sendImage(String imgPath, boolean isTimeline) {

        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(imgPath);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        Bitmap bitmap = WxUtil.extractThumbNail(imgPath, THUMB_SIZE,
                THUMB_SIZE, true);
        msg.setThumbImage(bitmap);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = msg;
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    private void sendWebPage(String title, String description, Bitmap thumb, String targetUrl, boolean isTimeline) {

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = targetUrl;

        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = description;
        msg.thumbData = WxUtil.bmpToByteArray(thumb, true);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    private void sendAppMessage(String title, String description, String imgPath, boolean isTimeline) {
        final WXMediaMessage msg = new WXMediaMessage();
        msg.setThumbImage(WxUtil.extractThumbNail(imgPath, THUMB_SIZE, THUMB_SIZE, true));
        msg.title = title;
        msg.description = description;

        if (!TextUtils.isEmpty(imgPath)) {
            final WXAppExtendObject appdata = new WXAppExtendObject();
            appdata.filePath = imgPath;
            appdata.extInfo = "this is ext info";
            msg.mediaObject = appdata;
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("appdata");
        req.message = msg;
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    private void sendMessage(String title, String description, String imgPath, String targetUrl, boolean isTimeline) {

        WXMediaMessage msg = new WXMediaMessage();
        // 分享标题
        msg.title = title;
        // 分享文字
        msg.description = description;
        // 分享图片
        if (!TextUtils.isEmpty(imgPath)) {
            Bitmap bitmap = WxUtil.extractThumbNail(imgPath, THUMB_SIZE,
                    THUMB_SIZE, true);
            msg.setThumbImage(bitmap);
        }

        // 分享链接
        if (!TextUtils.isEmpty(targetUrl)) {
            WXWebpageObject webpage = new WXWebpageObject();
            webpage.webpageUrl = targetUrl;
            msg.mediaObject = webpage;
        } else {
            WXTextObject wxTextObject = new WXTextObject();
            wxTextObject.text = description;
            msg.mediaObject = wxTextObject;
            msg.description = description;
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = isTimeline ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    public void handleResponse(BaseResp resp) {

        if (null == shareAction || null == shareAction.getCallBack()) {
            return;
        }
        CShareListener callBack = shareAction.getCallBack();
        SharePlatform platform = shareAction.getPlatform();
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                callBack.onResult(platform);
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                callBack.onCancel(platform);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                callBack.onCancel(platform);
                break;
            default:
                break;
        }
    }

    // -------------------------login--------------------
    private LoginCallBack loginCallBack;

    public void auth(SendAuth.Resp authResp) {
        int errorCode = authResp.errCode;
        if (errorCode == 0) { // 用户同意
            if (TextUtils.isEmpty(authResp.code)) {
                if (null != loginCallBack) loginCallBack.onError(new Exception("code=null"));
                return;
            }
        } else if (errorCode == -4) { // 用户拒绝授权
            if (null != loginCallBack) loginCallBack.onCancel();
            return;
        } else if (errorCode == -2) { // 取消授权
            if (null != loginCallBack) loginCallBack.onCancel();
            return;
        }
        final String code = authResp.code;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler = new UIHandler(shareAction, loginCallBack);
                Weixin weixin = (Weixin) PlatformConfig.getPlatform(SharePlatform.WEIXIN);
                String path = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + weixin.appId + "&secret="
                        + weixin.appSecret + "&code=" + code + "&grant_type=authorization_code";
        /*
         * { "access_token":"ACCESS_TOKEN", "expires_in":7200,
		 * "refresh_token":"REFRESH_TOKEN", "openid":"OPENID", "scope":"SCOPE",
		 * "unionid":"o6_bmasdasdsad6_2sgVt7hMZOPfL" }
		 */

                try {
                    String data = WxUtil.doHttpRequest(path);
                    if (TextUtils.isEmpty(data)) {
                        mHandler.fail(new Exception("AccessToken=null"));
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(data);
                    String access_token = jsonObject.optString("access_token");
                    String openid = jsonObject.optString("openid");
                    WXTokenKeeper.saveAccessToken(ctx, openid, access_token);
                    LoginInfo loginInfo = new LoginInfo();
                    loginInfo.uid = openid;
                    loginInfo.accessToken = access_token;
                    mHandler.loginSuccess(loginInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.fail(e);
                }
            }
        }).start();
    }

    @Override
    public void login(SharePlatform sharePlatform, LoginCallBack loginCallBack) {
        this.loginCallBack = loginCallBack;
        // send oauth request
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_message,snsapi_userinfo,snsapi_friend,snsapi_contact";
        req.state = "none";
        api.sendReq(req);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void release() {
        ctx = null;
        shareAction = null;
        loginCallBack = null;
        api = null;
        if (null != mHandler) mHandler.release();
    }

    private UIHandler mHandler;

    private static class UIHandler extends Handler {
        private ShareAction shareAction;
        private LoginCallBack loginCallBack;
        private UserInfoCallback userInfoCallback;
        private final int msg_cancel = 1;
        private final int msg_error = 2;
        private final int msg_success = 3;

        public UIHandler(ShareAction shareAction, LoginCallBack loginCallBack) {
            super(Looper.getMainLooper());
            this.shareAction = shareAction;
            this.loginCallBack = loginCallBack;
        }

        public UIHandler(ShareAction shareAction, UserInfoCallback userInfoCallback) {
            super(Looper.getMainLooper());
            this.shareAction = shareAction;
            this.userInfoCallback = userInfoCallback;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case msg_cancel:
                    if (null != loginCallBack) loginCallBack.onCancel();
                    break;
                case msg_error:
                    if (null != loginCallBack) loginCallBack.onError((Exception) msg.obj);
                    if (null != userInfoCallback) userInfoCallback.fail((Exception) msg.obj);
                    break;
                case msg_success:
                    if (null != loginCallBack) {
                        SharePlatform sharePlatform = SharePlatform.WEIXIN;
                        if (null == shareAction) {
                            sharePlatform = SharePlatform.WEIXIN;
                        } else {
                            sharePlatform = shareAction.getPlatform();
                        }
                        loginCallBack.onComplete(sharePlatform, (LoginInfo) msg.obj);
                    }
                    if (null != userInfoCallback) userInfoCallback.success((ShareUserInfo) msg.obj);
                    break;
            }
        }

        public void cancel() {
            Message message = obtainMessage(msg_cancel);
            sendMessage(message);
        }

        public void fail(Exception e) {
            Message message = obtainMessage(msg_error);
            message.obj = e;
            sendMessage(message);
        }

        public void getInfoSuccess(ShareUserInfo userInfo) {
            Message message = obtainMessage(msg_success);
            message.obj = userInfo;
            sendMessage(message);
        }

        public void loginSuccess(LoginInfo loginInfo) {
            Message message = obtainMessage(msg_success);
            message.obj = loginInfo;
            sendMessage(message);
        }

        public void release() {
            if (hasMessages(msg_cancel)) {
                removeMessages(msg_cancel);
            }
            if (hasMessages(msg_error)) {
                removeMessages(msg_error);
            }
            if (hasMessages(msg_success)) {
                removeMessages(msg_success);
            }
        }
    }

}
