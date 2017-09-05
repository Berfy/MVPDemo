package common.share;

import android.content.Intent;

public interface IShare {
    /**
     * 登录
     *
     * @param loginCallBack
     */
    void login(SharePlatform sharePlatform, LoginCallBack loginCallBack);

    /**
     * 分享
     *
     * @param shareAction
     */
    void share(ShareAction shareAction);

    /**
     * 获取用户信息
     *
     * @param callback
     * @return
     */
    void getUserInfo(UserInfoCallback callback);

    /**
     * 回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    void onActivityResult(int requestCode, int resultCode, Intent data);

    /**
     * 回收资源
     */
    void release();
}
