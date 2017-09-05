package com.wlb.agent.core.data;

import com.android.util.LContext;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.response.UserResponse;

/**
 * @author 张全
 */
public final class H5 {

    //首页
    public static String HOMEPAGE = DataConfig.h5Host + "wlb_html/index.html#/";
    //报价首页
    public static String HOMEPAGE_OFFER = DataConfig.h5Host + "wlb_html/index/index.html#/";
    //用户协议
    public static String REGIST_LICENCE = DataConfig.h5Host + "wlb_html/common/membership.html";
    //邀请页面
    public static String INVITE = DataConfig.h5Host + "wlb_html/invite/invite.html?inviteCode=";
    //团队管理-邀请
    public static String INVITE_NEW = DataConfig.h5Host + "wlb_html/invite/inviteLine.html";
    //城市选择
    public static String SELECT_CITY = DataConfig.h5Host + "wlb_html/common/city.html";
    //钱包规则
    public static String WALLET_RULE = DataConfig.h5Host + "wlb_html/common/wallet_rule.html";
    //添加车辆
    public static String CAR_ADD = DataConfig.h5Host + "wlb_html/vehicle/more.html";
    //成员列表
    public static String MEMBER_LIST = DataConfig.h5Host + "wlb_html/team/member.html";
    //业绩列表
    public static String PERFORMNCE_LIST = DataConfig.h5Host + "wlb_html/team/performance.html";
    //车险套餐
    public static String INSURANCE_LIST = DataConfig.h5Host + "wlb_html/price/preoffer.html";
    //机动车综合商业说明书
    public static String VEHICLE_SPECIFICATION = DataConfig.h5Host + "wlb_html/common/exceptionSpecifications.html";
    //他人支付
    public static String POLICY_PAY = DataConfig.h5Host + "wlb_html/policy/policypay.html?orderNo=";
    //重新选择险种
    public static String AGAIN_OFFERA_PRICE = DataConfig.h5Host + "wlb_html/price/offerabb.html?vehicleId=";
    //上传证件
    public static String UPLOAD_PAPERS = DataConfig.h5Host + "wlb_html/price/upload.html?orderNo=";
    //新手引导
    public static String USER_GUIDE = DataConfig.h5Host + "wlb_html/common/guide.html";
    //已有联系人
    public static String CONTACT_PERSON = DataConfig.h5Host + "wlb_html/common/contactPerson.html";
    //发展团员榜单
    public static String MEMBER_NUMS_RANK = DataConfig.h5Host + "wlb_html/team/memberNumsRank.html";
    //保费排行榜单
    public static String MEMBER_PREMIUMS_RANK = DataConfig.h5Host + "wlb_html/team/memberPremiumsRank.html";
    //佣金排行榜单
    public static String MEMBER_COMMISSIONS_RANK = DataConfig.h5Host + "wlb_html/team/memberCommissionsRank.html";
    //精品商品订单详情
    public static String RECOMMAND_GOOD_INFO = DataConfig.h5Host + "wlb_html/goods/recommandGood_info.html?orderNo=";
    //精选商品
    public static String RECOMMAND_GOODS = DataConfig.h5Host + "wlb_html/goods/recommandGood.html";
    //报价分享
    public static String OFFER_SHARE = DataConfig.h5Host + "wlb_html/price/priceShare.html";
    //测试网测试
    public static String OFFER_SHARE_TEST = DataConfig.h5SharePage + "wlb_html/price/priceShare.html";

    public static void reset(){
        HOMEPAGE = DataConfig.h5Host + "wlb_html/index.html#/";
    }

    public static String checkUrl(String url) {
        StringBuffer urlBuilder = new StringBuffer(url);
        if (!url.contains("?")) {
            urlBuilder.append("?");
        } else {
            urlBuilder.append("&");
        }
        urlBuilder.append("app=1");
        UserResponse loginedUser = UserClient.getLoginedUser();
        if (null != loginedUser) {
            urlBuilder.append("&token=" + (null != loginedUser ? loginedUser.token : ""));
            urlBuilder.append("&appVersion=" + LContext.versionCode);
        }
        return urlBuilder.toString();
    }
}
