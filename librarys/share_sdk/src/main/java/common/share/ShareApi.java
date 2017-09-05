package common.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.widget.Toast;

import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.tencent.tauth.Tencent;

import java.util.List;

import common.share.sina.AccessTokenKeeper;
import common.share.sina.SinaShareHelper;
import common.share.wx.WXShareHelper;

public class ShareApi {
    private static ShareApi mInstance;
    private IShare mSharePlatform;
    private final static String pkg_QQ = "com.tencent.mobileqq";
    private final static String pkg_WEIXIN = "com.tencent.mm";

    private ShareApi() {

    }

    public static ShareApi getInstance() {
        if (null == mInstance) {
            synchronized (ShareApi.class) {
                if (null == mInstance) {
                    mInstance = new ShareApi();
                }
            }
        }
        return mInstance;
    }

    public void share(Activity ctx, ShareAction shareAction) {
        mSharePlatform = createSharePlatform(ctx, shareAction.getPlatform());
        mSharePlatform.share(shareAction);
    }

    public void login(Activity ctx, LoginAction loginAction) {
        mSharePlatform = createSharePlatform(ctx, loginAction.getPlatform());
        mSharePlatform.login(loginAction.getPlatform(), loginAction.getCallBack());
    }

    private IShare createSharePlatform(Activity ctx, SharePlatform platform) {
        IShare mSharePlatform = null;

        switch (platform) {
            case QQ:
            case QZONE:
                mSharePlatform = new QQShareHelper(ctx);
                if (!mInstance.isQQInstalled(ctx)) {
                    Toast.makeText(ctx, "请先安装QQ", Toast.LENGTH_SHORT).show();
                }
                break;
            case SINA:
                mSharePlatform = new SinaShareHelper(ctx);
                break;
            case WEIXIN:
            case WEIXIN_CIRCLE:
                mSharePlatform = new WXShareHelper(ctx);
                if (!mInstance.isWeiXinInstalled(ctx)) {
                    Toast.makeText(ctx, "请先安装微信", Toast.LENGTH_SHORT).show();
                }
        }

        return mSharePlatform;
    }

    public IShare getSharePlatform() {
        return mSharePlatform;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null != mSharePlatform) mSharePlatform.onActivityResult(requestCode, resultCode, data);
    }

    public void release() {
        if (null != mSharePlatform) mSharePlatform.release();
        mSharePlatform = null;
    }

    public LoginInfo getLoginInfo(Context ctx, SharePlatform platform) {
        LoginInfo loginInfo = null;
        if (platform == SharePlatform.QQ
                || platform == SharePlatform.QZONE
                ) {

//            loginInfo = new LoginInfo();
//            loginInfo.uid = "F1ED5C6AADB15F2DDB195F063DA4AB25";
//            loginInfo.accessToken = "76C7B4FCA1D433789B51C67CBDF3CBAF";

            PlatformConfig.QQZone qqPlatform = (PlatformConfig.QQZone) PlatformConfig.getPlatform(SharePlatform.QQ);
            Tencent mTencent = Tencent.createInstance(qqPlatform.appId, ctx);
            String uid = mTencent.getOpenId();
            String accessToken = mTencent.getAccessToken();
            if (!TextUtils.isEmpty(accessToken) && mTencent.isSessionValid()) {
                loginInfo = new LoginInfo();
                loginInfo.uid = uid;
                loginInfo.accessToken = accessToken;
            }
        } else if (platform == SharePlatform.SINA) {
//            loginInfo = new LoginInfo();
//            loginInfo.uid = "2339137650";
//            loginInfo.accessToken = "2.00cYmSYC3f1YoC365bd805a0mD8gBD";

            Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(ctx);
            String uid = accessToken.getUid();
            String token = accessToken.getToken();
            if (!TextUtils.isEmpty(uid)
                    && !TextUtils.isEmpty(token)
                    && accessToken.isSessionValid()) {
                loginInfo = new LoginInfo();
                loginInfo.uid = uid;
                loginInfo.accessToken = token;
            }
        }
        return loginInfo;
    }

    /**
     * 获取用户信息
     *
     * @param ctx
     * @param platform
     * @param callback
     */
    public void getUserInfo(Context ctx, SharePlatform platform, UserInfoCallback callback) {
        IShare sharePlatform = createSharePlatform((Activity) ctx, platform);
        sharePlatform.getUserInfo(callback);
    }

    /**
     * 是否已安装微信客户端
     *
     * @param ctx
     * @return
     */
    public static boolean isWeiXinInstalled(Context ctx) {
        return isInstalled(ctx, pkg_WEIXIN);
    }

    /**
     * 是否已安装QQ客户端
     *
     * @param ctx
     * @return
     */
    public static boolean isQQInstalled(Context ctx) {
        return isInstalled(ctx, pkg_QQ);
    }

    private static boolean isInstalled(Context ctx, String pkg) {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = ctx.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            ApplicationInfo applicationInfo = resolveInfo.activityInfo.applicationInfo;
            String packageName = applicationInfo.packageName;
            if (packageName.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

}
