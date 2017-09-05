package com.wlb.agent.core.data.insurance.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 险种
 *
 * @author 张全
 */
public class Insurance implements Serializable {

    public String insuranceCode;// 险种代码
    public String insuranceName;// 险种名称
    private int selected;
    private int exempt;
    private int supportExempt;
    private int exemptStatus;
    public int amount;
    public String amountStr;
    public List<InsuranceOption> options;
    public String des;
    public int buyRate;//购买比率
    public double price;
    public double diffPrice;
    public double exemptPrice;
    public double taxPrice;
    public boolean isPrimary;//是否主险

    public Insurance(String insuranceName, int amount, double price) {
        this.insuranceName = insuranceName;
        this.amount = amount;
        this.price = price;
    }

    private InsuranceOption curOption;

    /**
     * 是否已投保
     *
     * @return
     */
    public boolean isSelected() {
        return selected == 1;
    }

    /**
     * 是否支持不计免赔
     *
     * @return
     */
    public boolean supportExempt() {
        return supportExempt == 1;
    }

    /**
     * 是否购买不计免赔
     *
     * @return
     */
    public boolean isBJMPChecked() {
        return exempt == 1;
    }

    /**
     * 不计免赔是否购买成功
     *
     * @return
     */
    public boolean exemptPriceSusccessful() {
        return supportExempt() && isBJMPChecked();
    }

    /**
     * 获取当前投保选项
     *
     * @return
     */
    public InsuranceOption getCurOption() {
        if (null == curOption)
            curOption = getOption(amount);
        return curOption;
    }

//
//
//
//	public double price; // 该险种的保费
//	public  double exemptPrice;//不计免赔价格
//	public  boolean exemptPriceSusccessful;//不计免赔报价（成功或失败）
//	public double diffPrice;// 已上次保费的差价
//	public double price_tax;// 交强险中的车船税
//
//	public List<InsuranceOption> insuranceOptions = new ArrayList<InsuranceOption>();
//	public boolean selected;// 投保或不投保
//	public InsuranceOption curOption;// 投保金额
//	public boolean isAdditional;// 是否为附加险
//	public boolean supportBJMP = true;// 是否支持购买不计免赔
//
//	public boolean isEnabled = true;// 是否可点击
//	public boolean isBJMPChecked;// 不计免赔是否已勾选
//
//
//	public String des;//详情
//	public int buyRate;//购买比率

    public InsuranceOption getOption(int key) {
        for (InsuranceOption option : options) {
            if (option.key == key) {
                return option;
            }
        }
        return null;
    }

    // ######################################################

    /**
     * 是否为车损险
     *
     * @return true 车损险
     */
    public boolean isDAMAGE() {
        return insuranceCode.equals("damage");
    }

}
