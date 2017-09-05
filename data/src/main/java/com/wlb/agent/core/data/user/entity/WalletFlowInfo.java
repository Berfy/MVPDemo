package com.wlb.agent.core.data.user.entity;

import java.io.Serializable;

/**
 * 钱包流水
 *
 * @author 曹泽琛
 */
public class WalletFlowInfo implements Serializable {

    /**
     * ID
     */
    public long billId;
    /**
     * 1表示挣钱；2表示扣钱
     */
    public int billType;
    /**
     * 1佣金；2提现
     */
    public int category;
    /**
     * 金额
     */
    public double amount;// 佣金总额
    /**
     * 时间戳
     */
    public long timestamp;// 操作时间

    /**
     * 操作描述
     */
    public String billDescribe;
}
