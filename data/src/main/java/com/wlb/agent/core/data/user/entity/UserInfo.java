package com.wlb.agent.core.data.user.entity;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户信息
 *
 * @author 张全
 */
public class UserInfo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -2462989495338427780L;
    /**
     * 用户id
     */
    public String token;
    /**
     * 是否已登录
     */
    public boolean isLogin;
    /**
     *等级 1钻石， 2金牌，3银牌
     */
    public String my_grade;
    /**
     * 用户名
     */
    public String account;
    /**
     * 邀请码
     */
    public String inviteCode;
    /**
     * 姓名
     */
    public String name;
    /**
     * 身份证号
     */
    public String identityCard;
    /**
     * 银行名称
     */
    public String bankName;
    /**
     * 银行卡号
     */
    public String bankCard;
    /**
     * 通讯地址——详细地址
     */
    public String address;

    /**
     * 用户头像url
     */
    public String avatar;

    /**
     * 用户昵称
     */
    public String nick_name;
    /**
     * 是否加V
     */
    private int v_flag;

    /**
     * 积分
     */
    public int score;

    /**
     * 城市
     */
    public String city;

    /**
     * 上传证件相关
     * real_name : 姓名
     * certificate_no :身份证
     * cert_url： [url1, url2] 身份证正反面url
     */
    public IdAuthInfo id_auth_info;
    /**
     * 用户绑定的email
     */
    public String email;
    /**
     * 用户电话号码
     */
    public String phone;

    /**
     * 第三方账号相关
     * type : 1:QQ 2:微信 3：Sina
     * is_band：1已绑定  0未绑定
     */
    public List<ThirdParty> third_party = new ArrayList();

    public boolean isVFlag(){
        return v_flag==1;
    }

//    @Override
//    public UserInfo clone() throws CloneNotSupportedException {
//        return (UserInfo) super.clone();
//    }
//
//
//    @Override
//    public String getUid() {
//
//        return token;
//    }
//
//    @Override
//    public String getKey() {
//
//        return token;
//    }
//
//    @Override
//    public String getModelName() {
//
//        return DBModelType.USER.modelName;
//    }
//
//    @Override
//    public String toDBValue() {
//        JSONObject dataJson = new JSONObject();
//        try {
//            dataJson.put("token", token);
//            dataJson.put("isLogin", isLogin);
//            dataJson.put("my_grade", my_grade);
//            dataJson.put("avatar", avatar);
//            dataJson.put("nick_name", nick_name);
//            dataJson.put("v_flag", v_flag );
//            dataJson.put("score", score);
//            dataJson.put("city", city);
//            if (null != id_auth_info) {
//                dataJson.put("id_auth_info", new Gson().toJson(id_auth_info));
//            }
//            dataJson.put("email", email);
//            dataJson.put("phone", phone);
//            if (null != third_party) {
//                JSONArray jsonArray = new JSONArray();
//                for (ThirdParty item : third_party) {
//                    jsonArray.put(item.toJson());
//                }
//                dataJson.put("third_party", jsonArray);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        DBLog.log("UserInfo_toDBValue: " + dataJson);
//        return dataJson.toString();
//    }
//
//    public static UserInfo parse(JSONObject dataJson) throws Exception {
//        UserInfo userInfo = new UserInfo();
//        if (dataJson.has("token")) {
//            userInfo.token = dataJson.optString("token");
//        }
//        userInfo.isLogin = dataJson.optBoolean("isLogin");
//        userInfo.my_grade = dataJson.optString("my_grade");
//
//        if (dataJson.has("avatar")) {
//            userInfo.avatar = dataJson.optString("avatar");
//        }
//        if (dataJson.has("nick_name")) {
//            userInfo.nick_name = dataJson.optString("nick_name");
//        }
//        userInfo.v_flag = dataJson.optInt("v_flag") == 1;
//        userInfo.score = dataJson.optInt("score");
//        userInfo.city = dataJson.optString("city");
//        JSONObject authInfo = dataJson.optJSONObject("id_auth_info");
//        if (null != authInfo) {
//            userInfo.id_auth_info = new Gson().fromJson(authInfo.toString(),IdAuthInfo.class);
//        }
//        userInfo.email = dataJson.optString("email");
//        userInfo.phone = dataJson.optString("phone");
//        JSONArray thirdParty = dataJson.optJSONArray("third_party");
//        if (null != thirdParty) {
//            for (int i = 0; i < thirdParty.length(); i++) {
//                JSONObject jsonBind = thirdParty.optJSONObject(i);
//                userInfo.third_party.add(ThirdParty.parse(jsonBind));
//            }
//        }
//        return userInfo;
//    }
//
//    @Override
//    public UserInfo fromDBValue(String dbValue) {
//        UserInfo userInfo = null;
//        try {
//            userInfo = parse(new JSONObject(dbValue));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        DBLog.log("toDBValue: " + userInfo);
//        return userInfo;
//    }
//
//    @Override
//    public String toString() {
//        return "UserInfo{" +
//                "token='" + token + '\'' +
//                ", isLogin=" + isLogin +
//                ", account='" + account + '\'' +
//                ", inviteCode='" + inviteCode + '\'' +
//                ", name='" + name + '\'' +
//                ", identityCard='" + identityCard + '\'' +
//                ", bankName='" + bankName + '\'' +
//                ", bankCard='" + bankCard + '\'' +
//                ", address='" + address + '\'' +
//                ", avatar='" + avatar + '\'' +
//                ", nick_name='" + nick_name + '\'' +
//                ", v_flag=" + v_flag +
//                ", score=" + score +
//                ", city='" + city + '\'' +
//                ", id_auth_info=" + id_auth_info +
//                ", email='" + email + '\'' +
//                ", phone='" + phone + '\'' +
//                ", third_party=" + third_party +
//                '}';
//    }

    public boolean isUnBind(){
        return TextUtils.isEmpty(phone);
    }
}
