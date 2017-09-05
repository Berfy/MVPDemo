package com.wlb.agent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 系统常量
 *
 * @author 张全
 */
public final class Constant {

    private static final String ACTION = "com.wlb.intent.action";
    public static ExecutorService EXECUTOR = Executors.newFixedThreadPool(10);

    /**
     * Intent 动作
     */
    public interface IntentAction {

        String UPDATE = ACTION + ".update";

        /**
         * ###################首页消息
         */
        String MSG_READ = ACTION + ".msg.read";

        /**
         * ##################用户模块################
         */
        // 注册
        String USER_REGIST = ACTION + ".user.regist";
        //我的任务--第一次查价
        String VEHICLE_PRICE = ACTION + ".vechicle.price";
        //更新个人所在城市
        String USER_CITY = ACTION + ".user.city";
        //更新钱包
        String WALLET_UPDATE = ACTION + ".wallet.update";
        //银行卡添加成功
        String BANKCARD_ADD = ACTION + ".bankcard.add";

        /**
         * ##################车险模块################
         */
        // 订单支付成功
        String ORDER_BUY_SUCCESS = ACTION + ".order.buy.success";
        // 订单支付失败
        String ORDER_BUY_FAIL = ACTION + ".order.buy.fail";
        //取消支付
        String ORDER_BUY_CANCEL = ACTION + ".order.buy.cancel";
        // 订单列表空-去出单
        String ORDER_GO = ACTION + ".order.go";

        /**
         * ###################团队管理
         */
        //修改团队名
        String TEAM_NAME = ACTION + ".team.name";
        /**
         * ###################套餐+优惠券
         */
        //超值套餐
        String COMBO = ACTION + "combo.constant";
        //优惠券
        String COUPON = ACTION + "discount.coupon";
        String CONTACTINFO = ACTION + "contact.info";
    }


}
