package common.share;


/**
 * 登录回调
 */
public interface LoginCallBack {
    public void onComplete(SharePlatform platform, LoginInfo loginInfo);

    public void onError(Throwable e);

    public void onCancel();

}
