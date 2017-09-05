package common.share;

import java.io.Serializable;

/**
 * 分享用户信息
 *
 * @author 张全
 */
public class ShareUserInfo implements Serializable{
    /**
     * 用户昵称
     */
    public String nickName;
    /**
     * 用户头像
     */
    public String avater;

    @Override
    public String toString() {
        return "ShareUserInfo{" +
                "nickName='" + nickName + '\'' +
                ", avater='" + avater + '\'' +
                '}';
    }
}
