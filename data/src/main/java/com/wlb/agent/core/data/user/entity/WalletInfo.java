package com.wlb.agent.core.data.user.entity;

import java.io.Serializable;

/**
 * 钱包
 * <p/>
 * Created by 曹泽琛.
 */
public class WalletInfo implements Serializable {
    /**
     * 累计收益
     */
    public double total_earn;
    /**
     * 累计提现金额
     */
    public double total_draw;
    /**
     * 当前余额
     */
    public double curren_balance;
    /**
     * 可提现金额
     */
    public double available_money;
    /**
     * 正在审核中的金额
     */
    public double processing_money;
    /**
     * 绑定的银行卡数
     */
    public int bind_bank_count;
    /**
     * 银行卡信息
     */
    public BankCardInfo def_draw_bank;
}
