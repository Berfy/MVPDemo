package common.share;

/**
 * 获取用户信息回调接口
 * @author 张全
 */
public interface UserInfoCallback {
    void success(ShareUserInfo userInfo);
    void fail(Exception e);
}
