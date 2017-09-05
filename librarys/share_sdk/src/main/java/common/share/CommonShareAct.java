package common.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * <pre>
 *    <activity
 *             android:name="common.share.CommonShareAct"
 *             android:configChanges="keyboardHidden|orientation|screenSize"
 *             android:screenOrientation="portrait"
 *             android:theme="@android:style/Theme.Translucent.NoTitleBar"
 *             android:windowSoftInputMode="stateHidden|adjustResize" />
 * </pre>
 *
 * @author zhangquan
 */
public class CommonShareAct extends Activity {
    private static final String KEY_SHARE = "SHARE_ACTION";
    private static final String KEY_LOGIN = "KEY_LOGIN";
    private IShare mSharePlatform;
    public static ShareAction mShareAction;
    public static LoginAction mLoginAction;

    public static void start(Context ctx, ShareAction shareAction) {
        Intent intent = new Intent(ctx, CommonShareAct.class);
        // intent.putExtra(KEY_SHARE, shareAction);
        CommonShareAct.mShareAction = shareAction;
        ctx.startActivity(intent);
    }

    public static void start(Context ctx, LoginAction loginAction) {
        Intent intent = new Intent(ctx, CommonShareAct.class);
        // intent.putExtra(KEY_LOGIN, loginAction);
        CommonShareAct.mLoginAction = loginAction;
        ctx.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharePlatform sharePlatform = null;
        // Bundle extras = getIntent().getExtras();
        // if (extras.containsKey(KEY_SHARE)) {
        // mShareAction = (ShareAction) extras.getSerializable(KEY_SHARE);
        // sharePlatform = mShareAction.getPlatform();
        // } else if (extras.containsKey(KEY_LOGIN)) {
        // mLoginAction = (LoginAction) extras.getSerializable(KEY_LOGIN);
        // sharePlatform = mLoginAction.getPlatform();
        // }
        if (null != mLoginAction) {
            sharePlatform = mLoginAction.getPlatform();
        }
        if (null != mShareAction) {
            sharePlatform = mShareAction.getPlatform();
        }

        if (sharePlatform == SharePlatform.QQ
                || sharePlatform == SharePlatform.QZONE) {
            mSharePlatform = new QQShareHelper(this);
        }

        if (null != mShareAction) {// 分享
            mSharePlatform.share(mShareAction);
        } else if (null != mLoginAction) { // 登录
            mSharePlatform.login(sharePlatform, mLoginAction.getCallBack());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != mSharePlatform) mSharePlatform.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // mShareAction = null;
        // mLoginAction = null;
    }
}
