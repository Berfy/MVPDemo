package com.wlb.agent.core.data.group.response;

import com.wlb.agent.core.data.base.BaseResponse;

import java.io.Serializable;

/**
 * 团队概况信息
 * <p>
 * Created by 曹泽琛.
 */

public class TeamSummaryResponse extends BaseResponse implements Serializable {

    public String name;//团队名
    public String leader_nick;//团长姓名
    public String avatar;//头像
    public Member member;
    public Achievement achievement;
    public int order_payable;//订单数

    public static class Member {
        /**
         * statistic_daily : 0
         * statistic_all : 1
         */
        public int statistic_daily;//今日新增
        public int statistic_all;//合计用户
    }

    public static class Achievement {
        /**
         * commission_daily : 0.0
         * commission_total : 492.82
         * dedicate_daily : 0.0
         * dedicate_total : 0.0
         */

        public double commission_daily;//今日佣金
        public double commission_total;//佣金总计
        public double dedicate_daily;//今日贡献
        public double dedicate_total;//贡献总计
    }
}
