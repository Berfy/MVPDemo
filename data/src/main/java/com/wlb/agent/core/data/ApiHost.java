package com.wlb.agent.core.data;


/**
 * 服务器接口
 *
 * @author 张全
 */
public class ApiHost {

    //接口域名
    private static final String apiHost = BuildConfig.apiHost;

    /**
     * ###########基础服务接口########
     */
    //上传文件
    public static String UPLOAD_FILE = apiHost + "common/uploadImgs.json";
    // 请求banner数据
    public static String COMMON_BANNER = apiHost + "activity/indexBanner.json";
    //版本升级
    public static String APP_UPDATE = apiHost + "app/update.json";
    //H5版本升级
    public static String APP_H5_UPDATE = apiHost + "app/newAppPackage.json";
    //未读消息数目
    public static String MSG_UNREADCOUNT = apiHost + "message/messageTipCount.json";
    //消息列表
    public static String MSG_LIST = apiHost + "message/messageInfo.json";
    //已读消息
    public static String MSG_READ = apiHost + "message/readMessage.json";
    // 知识库分类
    public static String LIBRARY_CLASSIFY = apiHost + "news/category.json";
    // 知识库分类列表
    public static String LIBRARY_CLASSIFY_LIST = apiHost + "news/list.json";

    /**
     * ############用户相关########
     */
    // 用户登陆
    public static String USER_LOGIN = apiHost + "user/login.json";
    // 用户登陆3.0
    public static String USER_LOGIN3 = apiHost + "user/verifyCodeLogin.json";
    //第三方账号登录
    public static String USER_THREE_LOGIN = apiHost + "user/third_party/login.json";
    // 用户退出登录
    public static String USER_LOGINOUT = apiHost + "user/logout.json";
    // 获取短信验证码
    public static String USER_GETCODE = apiHost + "msg/verifyCode.json";
    // 用户注册
    public static String USER_REGISTER = apiHost + "user/register.json";
    // 完善资料
    public static String USER_UPDATEINFO = apiHost + "user/info/modify.json";
    //绑定第三方账号
    public static String BIND_THIRD_PARTY = apiHost + "user/bindThirdParty.json";
    //解除绑定第三方账号
    public static String UNBIND_THIRD_PARTY = apiHost + "user/unbindThirdParty.json";
    //找回密码
    public static String RESET_PASSWORD = apiHost + "user/password/reset.json";
    //修改密码
    public static String CHANGE_PASSWORD = apiHost + "user/password/modify.json";
    //绑定手机号
    public static String BIND_PHONE = apiHost + "user/addPhone.json";
    //修改手机号
    public static String MODIFY_PNONE = apiHost + "user/modifyPhone.json";
    //反馈意见
    public static String FEED_BACK = apiHost + "common/feedback.json";
    // 绑定用户
    public static String PUSH_BIND = apiHost + "user/pushBind.json";
    // 钱包流水汇总
    public static String USER_WALLET_SUMMARY = apiHost + "wallet/info.json";
    // 钱包流水
    public static String USER_WALLET_LIST = apiHost + "wallet/myBill.json";
    // 钱包流水明细
    public static String USER_WALLET_DETAIL = apiHost + "wallet/billDetail.json";
    // 钱包提现
    public static String USER_WALLET_WITHDRAW = apiHost + "wallet/withdraw.json";
    // 钱包提现3.0
    public static String USER_WALLET_CHECKWITHDRAW = apiHost + "wallet/checkWithdrawal.json";
    // 设置提现密码
    public static String USER_WALLET_WITHDRAW_SET_PWD = apiHost + "wallet/addPassword.json";
    // 修改提现密码
    public static String USER_WALLET_WITHDRAW_MODIFY_PWD = apiHost + "wallet/updatePassword.json";
    // 重置提现密码
    public static String USER_WALLET_WITHDRAW_RESET_PWD = apiHost + "wallet/resetPassword.json";
    //待审核金额
    public static String USER_WALLET_WILLCAN = apiHost + "wallet/myBill.json";
    // 添加微信账号
    public static String USER_WALLET_ADD_WX_ACCOUNT = apiHost + "user/addWXCard.json";
    //判断是否设置提现密码和认证
    public static String USER_WALLET_CHECK = apiHost + "wallet/checkWithdrawal.json";
    //获取用户订单数
    public static String USER_ORDER_NUM = apiHost + "user/orderCount.json";
    // 获取个人信息
    public static String USER_GETINFO = apiHost + "user/info.json";
    //联系人
    public static String CONTACTS = apiHost + "team/member/contact.json";
    //首页Banner
    public static String BANNER = apiHost + "activity/indexBanner.json";

    // 上传照片
    public static String UPLOAD_PHOTO = apiHost + "user/bindAvatar.json";
    //实名认证
    public static String PROVE_INFO = apiHost + "user/info/verify.json";
    //认证信息
    public static String AUTHENTICATION_INFO = apiHost + "user/authentication/info.json";
    //修改职业认证
    public static String AUTHENTICATION_PROFESSIONAL = apiHost + "user/authentication/professional/modify.json";
    //修改教育认证
    public static String AUTHENTICATION_EDUCATION = apiHost + "user/authentication/education/modify.json";
    //修改资格认证
    public static String AUTHENTICATION_QUALIFICATION = apiHost + "user/authentication/qualification/modify.json";
    //我的名片
    public static String CARD_INFO = apiHost + "user/card/info.json";
    //我的银行卡
    public static String USER_BANK_CARD = apiHost + "user/bankcard.json";
    //添加银行卡
    public static String ADD_BANK_CARK = apiHost + "user/bankcard/add.json";
    //修改默认银行卡
    public static String SET_DEFAULT_CARD = apiHost + "user/bankcard/set_default.json";
    //团队概况信息
    public static String TEAM_SUMMARY = apiHost + "team/summary.json";
    //修改团队名称
    public static String MODIFY_NAME = apiHost + "team/modify_name.json";
    //团队成员待支付订单列表
    public static String TEAMMEMBER_ORDER = apiHost + "team/achievement/order_payable.json";
    //团队成员待支付订单催单
    public static String TEAMMEMBER_REMINDER = apiHost + "team/achievement/order_reminder.json";
    //当日积分状态信息
    public static String INTEGRAL_STATUS = apiHost + "points/daily.json";
    //执行任务通知服务端接口
    public static String INTEGRAL_NOTIFY = apiHost + "points/task_notify.json";

    /*报价分享*/
    //报价分享保存优惠金额
    public static String OFFER_SHARE_SAVE = apiHost + "insurance/premium.json";
    //报价分享列表
    public static String OFFER_SHARE_LIST = apiHost + "insurance/sharePriceBySeqNos.json";

    /**
     * ############用户车辆########
     */
    // 获取用户车辆列表
    public static String CARS_LIST = apiHost + "vehicle/myVehicles.json";
    // 删除车辆
    public static String CAR_DELETE = apiHost + "vehicle/deleteVehicle.json";
    //搜索车辆
    public static String CAR_SEARCH = apiHost + "vehicle/search.json";

    /**
     * ############车险相关########
     */
    // 订单预支付
    public static String INSURANCE_PREPAY = apiHost + "insurance/insuranceBuy.json";
    // 车险订单列表
    public static String INSURANCE_ORDER_LIST = apiHost + "insurance/insurancePolicy.json";
    // 车险订单详情
    public static String INSURANCE_ORDER_DETAIL = apiHost + "insurance/insurancePolicyDetail.json";
    //取消保单
    public static String INSURANCE_CANCEL = apiHost + "insurance/insuraceCancel.json";
    //保单搜索列表
    public static String INSURANCE_SEEK = apiHost + "insurance/search.json";
    //订单状态变更提醒
    public static String INSURANCE_STATUS_REMINDER = apiHost + "insurance/status_reminder.json";
    //订单状态变更提醒已读
    public static String INSURANCE_STATUS_READ = apiHost + "insurance/status_read.json";
    //代驾订单列表
    public static String ORDER_DRIVING_LIST = apiHost + "order/orderList.json";
    //订单保协验证码输入
    public static String ORDER_VERIFY_CODE = apiHost + "insurance/verifyCode.json";
    //订单保协验证码手机号修改
    public static String ORDER_MODIFY_PHONE = apiHost + "insurance/modifyVerifyPhone.json";
    //上传身份证信息
    public static String ORDER_UPLOAD_PHOTO = apiHost + "insurance/uploadPhoto.json";
    //上传验车照
    public static String ORDER_VEHICLECHECK_PHOTO_PHONE = apiHost + "insurance/uploadVehicleCheckPhoto.json";

    /**
     * ############优惠券########
     */
    //我的优惠券
    public static String COUPON_LIST = apiHost + "coupon/list.json";
    //当前订单可用优惠券列表
    public static String ORDER_COUPON_LIST = apiHost + "coupon/order_useable.json";
    //订单使用优惠券列表
    public static String ORDER_USE_COUPON_LIST = apiHost + "coupon/order_preuse.json";
    //订单取消优惠券列表
    public static String ORDER_CANCEL_COUPON_LIST = apiHost + "coupon/order_cancel.json";
    /**
     * ############套餐########
     */
    //订单可购买套餐
    public static String ORDER_COMBO_LIST = apiHost + "goods/package.json";
    //我的套餐
    public static String COMBO_LIST = apiHost + "goods/myGoodPackage.json";
    //选择商品套餐
    public static String USE_COMBO_LIST = apiHost + "goods/preBuyGoodPackage.json";


    /**
     * 保险公司图片
     *
     * @param companyCode
     * @return
     */
    public static String getCompanyIconUrl(String companyCode) {
        return BuildConfig.companyIconHost + companyCode + ".png";
    }
}
