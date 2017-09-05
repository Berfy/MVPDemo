package com.wlb.agent.core.data.user;

import android.text.TextUtils;

import com.android.util.ext.SPUtil;
import com.android.util.http.NetClient;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.response.GsonParser;
import com.android.util.http.response.JsonObjectParser;
import com.android.util.http.task.Task;
import com.google.gson.Gson;
import com.wlb.agent.core.data.ApiHost;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.base.RequestBuilder;
import com.wlb.agent.core.data.base.SimpleJsonParser;
import com.wlb.agent.core.data.insurance.response.OrderNumResponse;
import com.wlb.agent.core.data.user.entity.AuthStatus;
import com.wlb.agent.core.data.user.entity.EducationInfo;
import com.wlb.agent.core.data.user.entity.IdAuthInfo;
import com.wlb.agent.core.data.user.entity.Ivehicle;
import com.wlb.agent.core.data.user.entity.QualificationInfo;
import com.wlb.agent.core.data.user.entity.ThirdParty;
import com.wlb.agent.core.data.user.response.AddBankCardInfoResponse;
import com.wlb.agent.core.data.user.response.AuthenticationCommResponse;
import com.wlb.agent.core.data.user.response.AuthenticationResponse;
import com.wlb.agent.core.data.user.response.BankCardResponse;
import com.wlb.agent.core.data.user.response.CarResponse;
import com.wlb.agent.core.data.user.response.CardInfoResponse;
import com.wlb.agent.core.data.user.response.IntegralStatusResponse;
import com.wlb.agent.core.data.user.response.PhotoResponse;
import com.wlb.agent.core.data.user.response.ProveUploadingResponse;
import com.wlb.agent.core.data.user.response.ThirdPartyBindResponse;
import com.wlb.agent.core.data.user.response.UserCodeResponse;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.core.data.user.response.WalletFlowDetailResponse;
import com.wlb.agent.core.data.user.response.WalletFlowResponse;
import com.wlb.agent.core.data.user.response.WalletResponse;
import com.wlb.agent.core.data.user.response.WalletWillCanResponse;
import com.wlb.agent.core.data.user.response.WithDrawalCheckResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author 张全
 */
public class UserClient {
    private static final String LAST_USER = "LAST_USER";
    private static final String GPS_CITY = "GPS_CITY";//定位城市


    public static enum CodeAction {
        //注册验证码
        REGIST("signup"),
        //登录验证码
        LOGIN("login"),
        //提现验证码
        WITHDRAW("withdraw"),
        //绑定手机号
        BINDPHONE("bindPhone"),
        //重置密码
        RESETPWD("resetpwd"),
        //绑定手机号
        ADDPHONE("addPhone"),
        //重置提现密码
        RESETWALLETPWD("resetWalletPwd"),
        //订单更换手机号
        ORDER_CHANGE_MOBILE("updatePhone");

        public String action;

        private CodeAction(String code) {
            this.action = code;
        }
    }

    /**
     * 获取验证码
     *
     * @param phone
     * @param callback
     * @return
     */
    private static Task doGetCode(String phone, String action, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_GETCODE,
                callback, new GsonParser<>(UserCodeResponse.class));

        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("phoneNo", phone);
            postJson.put("action", action);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        builder.setPostJsonData(postJson);

        return NetClient.getInstance().execute(builder.build());
    }

    public static Task doGetCode(String phone, CodeAction codeAction, ICallback callback) {
        return doGetCode(phone, codeAction.action, callback);
    }

    /**
     * 用户登录
     *
     * @param phone       手机号（必填）
     * @param pwd         密码（非必填）
     * @param verify_code 验证码（非必填）
     * @param callback
     * @return
     */
    public static Task doLogin(final String phone, String verify_code, String pwd, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_LOGIN, callback, new JsonObjectParser() {
            @Override
            public Object parseData(JSONObject dataJson) throws Exception {
                UserResponse userInfo = fromGson(dataJson.toString(), UserResponse.class);
                if (userInfo.isSuccessful()) {
                    setThirdPartyLogin(false);
                    userInfo.phone = phone;
                    userInfo.nick_name = dataJson.optString("account");
                    saveLoginUser(userInfo);
                }
                return userInfo;
            }
        });
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("phone", phone);
            postJson.put("pwd", pwd);
            postJson.put("verify_code", verify_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 用户登录3.0
     *
     * @param phone       手机号（必填）
     * @param verify_code 验证码（必填）
     * @param callback
     * @return
     */
    public static Task doLogin3(final String phone, String verify_code, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_LOGIN3, callback, new JsonObjectParser() {
            @Override
            public Object parseData(JSONObject dataJson) throws Exception {
                UserResponse userInfo = fromGson(dataJson.toString(), UserResponse.class);
                if (userInfo.isSuccessful()) {
                    setThirdPartyLogin(false);
                    userInfo.phone = phone;
                    userInfo.nick_name = dataJson.optString("account");
                    saveLoginUser(userInfo);
                }
                return userInfo;
            }
        });
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("phoneNo", phone);
            postJson.put("verifyCode", verify_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 第三方账户登录
     *
     * @param type        1：绑定qq 2：绑定微信 3：绑定sina微博
     * @param userId      第三方账户id
     * @param accessToken 第三方账户token
     * @param callback
     * @return
     */
    public static Task doThreeLogin(final int type, String userId, String accessToken, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_THREE_LOGIN, callback, new JsonObjectParser() {
            @Override
            public Object parseData(JSONObject dataJson) throws Exception {
                UserResponse userInfo = fromGson(dataJson.toString(), UserResponse.class);
                if (userInfo.isSuccessful()) {
                    setThirdPartyLogin(true);
                    setThirdPartyPlatform(type);
                    saveLoginUser(userInfo);
                }
                return userInfo;
            }
        });
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("type", type);
            postJson.put("user_Id", userId);
            postJson.put("access_Token", accessToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 用户注册
     *
     * @param phone      手机号
     * @param verifyCode 验证码
     * @param callback
     * @return
     */
    public static Task doRegist(final String phone, String verifyCode, ICallback callback) {
        String url = ApiHost.USER_REGISTER;
        RequestBuilder builder = RequestBuilder.postBuilder(url, callback,
                new JsonObjectParser() {
                    @Override
                    public Object parseData(JSONObject jsonObject)
                            throws Exception {
                        UserResponse response = fromGson(jsonObject.toString(), UserResponse.class);
                        if (response.isSuccessful()) {
                            response.phone = phone;
                            saveLoginUser(response);
                        }
                        return response;
                    }
                });

        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("phone", phone);
            postJson.put("verify_code", verifyCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 用户退出登录接口
     *
     * @param token
     * @param callback
     * @return
     */
    public static Task doLoginout(final String token, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_LOGINOUT, callback, new JsonObjectParser() {
            @Override
            public Object parseData(JSONObject dataJson) throws Exception {
                BaseResponse response = fromGson(dataJson.toString(), BaseResponse.class);
                if (response.isSuccessful()) {
                    logout();
                }
                return response;
            }
        });

        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        builder.setPostJsonData(postJson);

        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 更新资料
     *
     * @param user     用户信息
     * @param callback
     * @return
     */
    public static Task doUpdateUserInfo(final UserResponse user, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_UPDATEINFO, callback,
                new JsonObjectParser() {
                    @Override
                    public Object parseData(JSONObject jsonObject) throws Exception {
                        UserResponse response = fromGson(jsonObject.toString(), UserResponse.class);
                        if (response.isSuccessful()) {
                            updateLoginedUser(user);
                        }
                        return response;
                    }
                });
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("avatar", user.avatar);
            postJson.put("nick_name", user.nick_name);
            postJson.put("city", user.city);
            postJson.put("email", user.email);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 获取用户资料
     *
     * @param callback
     * @return
     */
    public static Task doGetUserInfo(ICallback callback) {
        RequestBuilder builder = RequestBuilder.getBuilder(ApiHost.USER_GETINFO, callback,
                new JsonObjectParser() {
                    @Override
                    public Object parseData(JSONObject jsonObject) throws Exception {
                        UserResponse response = fromGson(jsonObject.toString(), UserResponse.class);
                        if (response.isSuccessful()) {
                            updateLoginedUser(response);
                        }
                        return response;
                    }
                });
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 获取订单数
     *
     * @param callback
     * @return
     */
    public static Task doGetOrderNum(ICallback callback) {
        RequestBuilder builder = RequestBuilder.getBuilder(ApiHost.USER_ORDER_NUM, callback,
                new GsonParser<>(OrderNumResponse.class));
        builder.setPostJsonData(builder.getPostJson());
        return NetClient.getInstance().execute(builder.build());
    }


    /**
     * 钱包
     *
     * @param callback
     * @return
     */
    public static Task doGetWallet(ICallback callback) {
        RequestBuilder builder = RequestBuilder.getBuilder(ApiHost.USER_WALLET_SUMMARY, callback,
                new GsonParser<>(WalletResponse.class));
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 钱包流水
     *
     * @param lastId
     * @param pageCount 每页显示的条数
     * @param tradeType 流水的类型 10001 推广费入账 20002 消费   0  全部
     * @param callback
     * @return
     */
    public static Task doGetWalletFlowList(long lastId, int tradeType, int pageCount, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_WALLET_LIST, callback, new GsonParser<>(WalletFlowResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("lastId", lastId);
            postJson.put("tradeType", tradeType);
            postJson.put("pageCount", pageCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 钱包流水明细
     *
     * @param billId
     * @param callback
     * @return
     */
    public static Task doGetWalletFlowDetail(long billId, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_WALLET_DETAIL, callback, new GsonParser<>(WalletFlowDetailResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("billId", billId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 钱包提现
     *
     * @param money            提现金额
     * @param bank_id          银行卡ID
     * @param verificationCode 验证码
     * @param callback
     * @return
     */
    public static Task doWithDrawMoneny(double money, String verificationCode, long bank_id, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_WALLET_WITHDRAW, callback,
                new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("money", money);
            postJson.put("bank_id", bank_id);
            postJson.put("verificationCode", verificationCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 钱包提现3.0
     *
     * @param money            提现金额
     * @param bank_id          银行卡ID
     * @param verificationCode 验证码
     * @param callback
     * @return
     */
    public static Task doWithDrawMoneny3(String pwd, String money, long bank_id, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_WALLET_CHECKWITHDRAW, callback,
                new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("password", pwd);
            postJson.put("money", money);
            postJson.put("bank_id", bank_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 设置提现密码
     *
     * @param password 密码
     * @return
     */
    public static Task setWithDrawPwd(String password, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_WALLET_WITHDRAW_SET_PWD, callback,
                new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 修改提现密码
     *
     * @param oldPassword 老密码
     * @param password    新密码
     * @return
     */
    public static Task modifyWithDrawPwd(String oldPassword, String password, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_WALLET_WITHDRAW_MODIFY_PWD, callback,
                new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("oldPwd", oldPassword);
            postJson.put("newPwd", password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 重置提现密码
     *
     * @param oldPassword 老密码
     * @param password    新密码
     * @param vcode       验证码
     * @return
     */
    public static Task resetWithDrawPwd(String oldPassword, String password, String vcode, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_WALLET_WITHDRAW_RESET_PWD, callback,
                new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("password", oldPassword);
            postJson.put("repeatPassword", password);
            postJson.put("vcode", vcode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 检查是否设置提现密码和实名认证
     *
     * @param callback
     * @return
     */
    public static Task doWithDrawCheck(ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_WALLET_CHECK, callback,
                new GsonParser<>(WithDrawalCheckResponse.class));
        try {
            builder.setPostJsonData(builder.getPostJson());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 添加修改微信账号
     *
     * @param uid      用户openId
     * @param nick     昵称
     * @param callback
     * @return
     */
    public static Task addWxAccount(String uid, String nick, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_WALLET_ADD_WX_ACCOUNT, callback,
                new GsonParser<>(AddBankCardInfoResponse.class));
        try {
            JSONObject jsonObject = builder.getPostJson();
            jsonObject.put("nickName", nick);
            jsonObject.put("openid", uid);
            builder.setPostJsonData(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 待审核金额
     * @param status   审核订单的类型 -1 全部 , 0 审核中,1 审核通过,2审核未通过
     * @param callback
     * @return
     */
    public static Task getWalletWillCan(long lastId, int status, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.USER_WALLET_WILLCAN, callback,
                new GsonParser<>(WalletWillCanResponse.class));
        try {
            JSONObject jsonObject = builder.getPostJson();
            jsonObject.put("lastId", lastId);
            jsonObject.put("pageCount", 10);
            jsonObject.put("status", status);
            builder.setPostJsonData(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 我的银行卡
     */
    public static Task doGetBankCard(ICallback callback) {
        RequestBuilder builder = RequestBuilder.getBuilder(ApiHost.USER_BANK_CARD, callback, new GsonParser<>(BankCardResponse.class));
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 添加银行卡
     */
    public static Task doAddBankCard(String bank_no, String bank_owner, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.ADD_BANK_CARK, callback, new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("bank_no", bank_no);
            postJson.put("bank_owner", bank_owner);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 修改默认银行卡
     */
    public static Task doSetDefaultCard(long bank_id, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.SET_DEFAULT_CARD, callback, new SimpleJsonParser());

        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("bank_id", bank_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 实名认证
     *
     * @param name          姓名
     * @param certificateNo 身份证号
     * @param certUrl       身份证正、反面
     * @param callback
     * @return
     */
    public static Task doProveUploading(final String name, final String certificateNo, JSONArray certUrl, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.PROVE_INFO, callback,
                new JsonObjectParser() {
                    @Override
                    public Object parseData(JSONObject dataJson) throws Exception {
                        ProveUploadingResponse response = fromGson(dataJson.toString(), ProveUploadingResponse.class);
                        if (response.isSuccessful()) {
                            UserResponse loginedUser = getLoginedUser();
                            IdAuthInfo idAuthInfo = new IdAuthInfo();
                            idAuthInfo.real_name = name;
                            idAuthInfo.certificate_no = certificateNo;
                            AuthStatus authStatus = AuthStatus.getAuthStatus(response.id_auth_status);
                            idAuthInfo.setAuthStatus(authStatus);
                            loginedUser.id_auth_info = idAuthInfo;
                            updateLoginedUser(loginedUser);
                        }
                        return response;
                    }
                });
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("real_name", name);
            postJson.put("certificate_no", certificateNo);
            postJson.put("cert_url", certUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 认证信息
     *
     * @param callback
     * @return
     */
    public static Task doGetAuthemticationInfo(ICallback callback) {
        RequestBuilder builder = RequestBuilder.getBuilder(ApiHost.AUTHENTICATION_INFO, callback, new GsonParser<>(AuthenticationResponse.class));
        return NetClient.getInstance().execute(builder.build());
    }


    /**
     * 修改职业认证
     *
     * @param company_name      公司名称
     * @param professional_name 职位名称
     * @param cert_url          证件照 (图片base64)
     * @param callback
     * @return
     */
    public static Task doChangeProfessional(String company_name, String professional_name,
                                            String cert_url, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.AUTHENTICATION_PROFESSIONAL, callback, new GsonParser<>(AuthenticationCommResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("company_name", company_name);
            postJson.put("professional_name", professional_name);
            postJson.put("cert_url", cert_url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 修改教育认证
     *
     * @param info
     * @param callback
     * @return
     */
    public static Task doChangeEducation(EducationInfo info, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.AUTHENTICATION_EDUCATION, callback, new GsonParser<>(AuthenticationCommResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("school", info.school);
            postJson.put("subject", info.subject);
            postJson.put("educational", info.educational);
            postJson.put("enrollment_time", info.enrollment_time);
            postJson.put("graduation_time", info.graduation_time);
            postJson.put("cert_url", info.cert_url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 修改资格认证
     *
     * @param info
     * @param callback
     * @return
     */
    public static Task doChangeQualification(QualificationInfo info, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.AUTHENTICATION_QUALIFICATION, callback, new GsonParser<>(AuthenticationCommResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("certificate", info.certificate);
            postJson.put("cert_no", info.cert_no);
            postJson.put("award_time", info.award_time);
            postJson.put("cert_url", info.cert_url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 我的名片
     *
     * @param callback
     * @return
     */
    public static Task doGetCardInfo(ICallback callback) {
        RequestBuilder builder = RequestBuilder.getBuilder(ApiHost.CARD_INFO, callback, new GsonParser<>(CardInfoResponse.class));
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 当日积分状态信息
     *
     * @param callback
     * @return
     */
    public static Task doGetIntegralInfo(ICallback callback) {
        RequestBuilder builder = RequestBuilder.getBuilder(ApiHost.INTEGRAL_STATUS, callback, new GsonParser<>(IntegralStatusResponse.class));
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 执行任务通知服务端接口
     *
     * @param name     签到任务
     * @param callback
     * @return
     */
    public static Task doNotifyIntegral(String name, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.INTEGRAL_NOTIFY, callback,
                new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("name", name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 车辆列表
     *
     * @param type     0全部、10到期日降序，11 报价日期降序，20 未投保车辆，21 已投保
     * @param callback
     * @return
     */
    public static Task doGetCarList(int type, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.CARS_LIST, callback,
                new GsonParser<>(CarResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 删除车辆
     *
     * @param carInfo
     * @param callback
     * @return
     */
    public static Task doDeleteCar(Ivehicle carInfo, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.CAR_DELETE, callback,
                new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("vehicleId", carInfo.vehicleId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 搜索车辆
     *
     * @param keywords
     * @param callback
     * @return
     */
    public static Task doSearchCar(String keywords, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.CAR_SEARCH, callback,
                new GsonParser<>(CarResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("keywords", keywords);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    private static <T> T fromGson(String json, Class<T> cls) {
        return new Gson().fromJson(json.toString(), cls);
    }

    /**
     * 上传头像
     */
    public static Task doUploadPhoto(String img, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.UPLOAD_PHOTO, callback,
                new JsonObjectParser() {
                    @Override
                    public Object parseData(JSONObject dataJson) throws Exception {
                        PhotoResponse response = fromGson(dataJson.toString(), PhotoResponse.class);
                        if (response.isSuccessful()) {
                            UserResponse loginedUser = UserClient.getLoginedUser();
                            loginedUser.avatar = response.imgUrl;
                            updateLoginedUser(loginedUser);
                        }
                        return response;
                    }
                });
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("img", img);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);

        return NetClient.getInstance().execute(builder.build());

    }


    /**
     * 绑定第三方账号
     */
    public static Task doBindThirdParty(final UserResponse userInfo, final int thirdPartyType, String thirdPartyUserId, String accessToken, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.BIND_THIRD_PARTY, callback, new JsonObjectParser() {
            @Override
            public Object parseData(JSONObject dataJson) throws Exception {
                ThirdPartyBindResponse response = fromGson(dataJson.toString(), ThirdPartyBindResponse.class);
                if (response.isSuccessful()) {
                    UserResponse loginedUser = getLoginedUser();
                    List<ThirdParty> third_party = loginedUser.third_party;
                    if (null != third_party) {
                        for (ThirdParty item : third_party) {
                            if (item.type == thirdPartyType) {
                                item.setBind(true);
                                break;
                            }
                        }
                        updateLoginedUser(loginedUser);
                    }
                }
                return response;
            }
        });
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("token", userInfo.token);
            postJson.put("thirdPartyType", thirdPartyType);
            postJson.put("thirdPartyUserId", thirdPartyUserId);
            postJson.put("accessToken", accessToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 解除第三方綁定
     */

    public static Task doUnbindThirdParty(final int thirdPartyType, String thirdPartyUserId, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.UNBIND_THIRD_PARTY, callback, new JsonObjectParser() {
            @Override
            public Object parseData(JSONObject jsonObject) throws Exception {
                BaseResponse response = fromGson(jsonObject.toString(), BaseResponse.class);
                if (response.isSuccessful()) {
                    UserResponse loginedUser = getLoginedUser();
                    List<ThirdParty> third_party = loginedUser.third_party;
                    if (null != third_party) {
                        for (ThirdParty item : third_party) {
                            if (item.type == thirdPartyType) {
                                item.setBind(false);
                                break;
                            }
                        }
                        updateLoginedUser(loginedUser);
                    }
                }
                return response;
            }
        });
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("thirdPartyType", thirdPartyType);
            postJson.put("thirdPartyUserId", thirdPartyUserId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }


    /**
     * 找回密码
     */
    public static Task doResetPassword(String phone, String verify_code, String pwd, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.RESET_PASSWORD, callback, new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("phone", phone);
            postJson.put("verify_code", verify_code);
            postJson.put("pwd", pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 用户反馈意见
     */
    public static Task doFeedBack(String contact, String content, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.FEED_BACK, callback, new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("contact", contact);
            postJson.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 修改密码
     */
    public static Task doChangePassword(String old_pwd, String new_pwd, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.CHANGE_PASSWORD, callback, new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("old_pwd", old_pwd);
            postJson.put("new_pwd", new_pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 绑定手机号
     *
     * @param phone    手机号
     * @param code     验证码
     * @param callback
     * @return
     */
    public static Task doAddPhone(String phone, String code, ICallback callback) {
        return doBindPhone(ApiHost.BIND_PHONE, phone, code, "", callback);
    }

    /**
     * 修改手机号
     *
     * @param phone    手机号
     * @param code     验证码
     * @param psd      密码
     * @param callback
     * @return
     */
    public static Task doChangePhone(String phone, String code, String psd, ICallback callback) {
        return doBindPhone(ApiHost.MODIFY_PNONE, phone, code, psd, callback);
    }

    private static Task doBindPhone(String apiHost, final String phoneNo, String verifyCode, String pwd, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(apiHost, callback, new SimpleJsonParser() {
            @Override
            public Object parseData(JSONObject jsonObject) throws Exception {
                BaseResponse baseResponse = fromGson(jsonObject.toString(), BaseResponse.class);
                if (baseResponse.isSuccessful()) {
                    UserResponse loginedUser = getLoginedUser();
                    loginedUser.phone = phoneNo;
                    updateLoginedUser(loginedUser);
                }
                return baseResponse;
            }
        });
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("phoneNo", phoneNo);
            postJson.put("verifyCode", verifyCode);
            if (!TextUtils.isEmpty(pwd))
                postJson.put("pwd", pwd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 绑定个推推送用户到业务系统
     *
     * @param cid      ：个推推送用户id
     * @param callback
     * @return
     */
    public static Task doPushBind(String cid, ICallback callback) {
        UserResponse loginedUser = UserClient.getLoginedUser();
        if (null == loginedUser) {
            if (null != callback) {
                callback.failure(new NetException("User is null"));
                callback.end();
            }
            return null;
        }
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.PUSH_BIND, callback,
                new SimpleJsonParser());

        JSONObject postJson = builder.getPostJson();
        try {

            postJson.put("phoneNo", loginedUser.phone);
            postJson.put("cid", cid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        builder.setPostJsonData(postJson);

        return NetClient.getInstance().execute(builder.build());
    }

    // ##########################本地数据库##############################
    private static final String THIRD_PARTY_LOGIN = "THIRDPARTYLOGIN";
    private static final String THIRD_PARTY = "THIRD_PARTY";

    public static boolean isThirdPartyLogin() {
        return SPUtil.getBoolean(THIRD_PARTY_LOGIN);
    }

    private static void setThirdPartyLogin(boolean isThirdPartyLogin) {
        SPUtil.setBoolean(THIRD_PARTY_LOGIN, isThirdPartyLogin);
    }

    public static int getThirdPartyPlatform() {
        return SPUtil.getInt(THIRD_PARTY);
    }

    public static void setThirdPartyPlatform(int platform) {
        SPUtil.setInt(THIRD_PARTY, platform);
    }

    public static String getToken() {
        UserResponse loginedUser = getLoginedUser();
        if (null != loginedUser) {
            return loginedUser.token;
        }
        return null;
    }

    public static void logout() {
        UserResponse loginedUser = getLoginedUser();
        if (null != loginedUser) {
            saveLastLoginPhone(loginedUser.phone);
        }
        saveUser(null);
    }

    public static void saveLoginUser(UserResponse userInfo) {
        userInfo.isLogin = true;
        saveUser(userInfo);
    }

    public static void updateLoginedUser(UserResponse userInfo) {
        userInfo.isLogin = true;
        UserResponse loginedUser = getLoginedUser();
        if (null != loginedUser) {
            if (TextUtils.isEmpty(userInfo.phone)) {
                userInfo.phone = loginedUser.phone;
            }
            if (TextUtils.isEmpty(userInfo.token)) {
                userInfo.token = loginedUser.token;
            }
            if (TextUtils.isEmpty(userInfo.avatar)) {
                userInfo.avatar = loginedUser.avatar;
            }
            if (TextUtils.isEmpty(userInfo.nick_name)) {
                userInfo.nick_name = loginedUser.nick_name;
            }
            if (TextUtils.isEmpty(userInfo.email)) {
                userInfo.email = loginedUser.email;
            }
        }
        saveUser(userInfo);
    }

    public static void saveLastLoginPhone(String phone) {
        SPUtil.setString(LAST_USER, phone);
    }

    public static void saveGPSCity(String city) {
        SPUtil.setString(GPS_CITY, city);
    }

    public static String getLastLoginPhone() {
        return SPUtil.getString(LAST_USER);
    }

    public static String getGPSCity() {
        return SPUtil.getString(GPS_CITY);
    }

    /**
     * 获取登录的用户
     *
     * @return
     */
    public static UserResponse getLoginedUser() {
        String str = SPUtil.getString(KEY_USER);
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        UserResponse response = fromGson(str, UserResponse.class);
        return null != response && response.isLogin ? response : null;
    }

    private static void saveUser(UserResponse user) {
        if (null == user) {
            SPUtil.setString(KEY_USER, null);
            SPUtil.setBoolean(KEY_TUIGUANGFEI,false);
        } else {
            String str = new Gson().toJson(user);
            SPUtil.setString(KEY_USER, str);
        }
    }

    public static void saveTuifuangfei(boolean isShow) {
        SPUtil.setBoolean(KEY_TUIGUANGFEI, isShow);
    }

    public static boolean getTuifuangfei() {
        return SPUtil.getBoolean(KEY_TUIGUANGFEI);
    }

    public void clear(){
        SPUtil.remove(KEY_TUIGUANGFEI);
    }

    // ###############用户名片######
    public static String getUserInviteUrl() {
        UserResponse loginedUser = getLoginedUser();
        if (null != loginedUser) {
            return H5.INVITE + loginedUser.token;
        }
        return null;
    }

    /**
     * 是否显示团队人数
     *
     * @return
     */
    public static boolean showTeamCount() {
        return SPUtil.getBoolean(SHOW_TEAMCOUNT, true);
    }

    public static void setShowTeamCount(boolean showTeamCount) {
        SPUtil.setBoolean(SHOW_TEAMCOUNT, showTeamCount);
    }

    /**
     * 是否显示服务客户人数
     *
     * @return
     */
    public static boolean showCustomCount() {
        return SPUtil.getBoolean(SHOW_CUSTOMCOUNT, true);
    }

    public static void setShowCustomCount(boolean showCustomCount) {
        SPUtil.setBoolean(SHOW_CUSTOMCOUNT, showCustomCount);
    }

    /**
     * 是否显示业绩
     *
     * @return
     */
    public static boolean showAchievement() {
        return SPUtil.getBoolean(SHOW_ARCHIVEMENTS, true);
    }

    public static void setShowAchievementCount(boolean showAchiement) {
        SPUtil.setBoolean(SHOW_ARCHIVEMENTS, showAchiement);
    }

    /**
     * 是否显示等级
     *
     * @return
     */
    public static boolean showGrade() {
        return SPUtil.getBoolean(SHOW_GRADE, true);
    }

    public static void setShowGrade(boolean showGrade) {
        SPUtil.setBoolean(SHOW_GRADE, showGrade);
    }

    private static final String KEY_USER = "param_login_user";
    private static final String SHOW_TEAMCOUNT = "param_showteam";
    private static final String SHOW_CUSTOMCOUNT = "param_showclient";
    private static final String SHOW_ARCHIVEMENTS = "param_showsales";
    private static final String SHOW_GRADE = "param_showgrade";
    private static final String KEY_TUIGUANGFEI = "tuigaungfei";
}
