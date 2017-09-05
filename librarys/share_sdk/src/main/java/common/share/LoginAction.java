package common.share;

import android.app.Activity;

import java.io.Serializable;

public class LoginAction implements Serializable {
    private static final long serialVersionUID = 6583292979011307931L;

    private SharePlatform platform;
    private LoginCallBack callBack;

    public SharePlatform getPlatform() {
        return platform;
    }

    public void setPlatform(SharePlatform platform) {
        this.platform = platform;
    }

    public LoginCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(LoginCallBack callBack) {
        this.callBack = callBack;
    }

    public void login(Activity ctx) {
        ShareApi.getInstance().login(ctx, this);
    }
    public void release(){
        callBack=null;
        platform=null;
        ShareApi.getInstance().release();
    }

}
