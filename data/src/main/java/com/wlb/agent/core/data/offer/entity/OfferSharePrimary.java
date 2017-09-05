package com.wlb.agent.core.data.offer.entity;

/**
 * Created by Berfy on 2017/8/2.
 * 报价分享-保险主险
 */
public class OfferSharePrimary {

    private String insuranceName;//险种名称
    private int selected;//是否选中购买
    private int exempt;//是否不计免赔
    private double exemptPrice;//不计免赔额
    private double amount;//保额
    private double price;//保费
    private int buyRate;//折扣率
    private double taxPrice;//车船税

    public OfferSharePrimary(String insuranceName, double price) {
        this.insuranceName = insuranceName;
        this.price = price;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(int buyRate) {
        this.buyRate = buyRate;
    }

    public double getTaxPrice() {
        return taxPrice;
    }

    public void setTaxPrice(double taxPrice) {
        this.taxPrice = taxPrice;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public int getExempt() {
        return exempt;
    }

    public void setExempt(int exempt) {
        this.exempt = exempt;
    }

    public double getExemptPrice() {
        return exemptPrice;
    }

    public void setExemptPrice(double exemptPrice) {
        this.exemptPrice = exemptPrice;
    }
}
