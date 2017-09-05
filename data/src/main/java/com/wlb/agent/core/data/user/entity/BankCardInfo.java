package com.wlb.agent.core.data.user.entity;

import java.io.Serializable;

/**
 * 银行卡信息
 * Created by JiaHongYa
 */

public class BankCardInfo implements Serializable {
    /**
     *银行卡id
     */
    public long bank_id;
    /**
     *银行卡名称
     */
    public String bank_name;
    /**
     *银行卡号
     */
    public String bank_no;
    /**
     *持卡人姓名
     */
    public String bank_owner;
    /**
     * 是否为默认银行卡 1是0 否
     */
    public int def_flag;
    /**
     * 银行卡类型 BANK银行 WX 微信账户 XFB 支付宝账户  账户为支付宝或
     */
    public String cardType;
    /**
     * 银行logo
     */
    public String bank_logo;

    public boolean isDefault(){
        return def_flag==1;
    }
    public void setDefault(boolean def){
        def_flag=def?1:0;
    }

    public String getCardType() {
        return cardType;
    }
}
