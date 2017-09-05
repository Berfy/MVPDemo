package com.wlb.agent.core.data.user.entity;

import java.io.Serializable;

/**
 * 佣金详情之佣金收入
 *
 * Created by 曹泽琛.
 */
public class CommissionDetailInfo implements Serializable{
    /**
     * 车牌号
     */
    public String licenseNo;
    /**
     * 车主
     */
    public String owner;
    /**
     * 被保人
     */
    public String insurant;
    /**
     * 保单号
     */
    public String policyNo;
    /**
     * 佣金
     */
    public double commission;
    /**
     * 佣金状态
     */
    public String policyAmountDescribe;

    /**
     * 卡号
     */
    public String bankCrad;
}
