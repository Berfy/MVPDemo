package com.wlb.agent.core.data.offer.entity;

import java.util.List;

/**
 * 报价分享
 * Created by Berfy on 2017/7/17.
 * 待完善
 */

public class OfferShare {

    private String seqNo;//报价订单号
    private String companyName;//保险公司
    private String companyCode;//保险公司代码
    private String licenseNo;//车牌号
    private String owner;//车主
    private OfferShareTotalPrice totalPrice;
    private List<OfferSharePrimary> primary;//主险列表
    private List<OfferSharePrimary> additional;//附加险列表
    private OfferSharePrimary vehicle;//交强险
    private boolean isSelect;//是否选中
    private int syxZhekou;//商业险折扣率
    private int jqxZhekou;//交强险折扣率
    private double myPrice;//推广费
    private double premium;//优惠后价格
    private double price;//保费总额

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public OfferShareTotalPrice getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(OfferShareTotalPrice totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OfferSharePrimary> getPrimary() {
        return primary;
    }

    public void setPrimary(List<OfferSharePrimary> primary) {
        this.primary = primary;
    }

    public List<OfferSharePrimary> getAdditional() {
        return additional;
    }

    public void setAdditional(List<OfferSharePrimary> additional) {
        this.additional = additional;
    }

    public OfferSharePrimary getVehicle() {
        return vehicle;
    }

    public void setVehicle(OfferSharePrimary vehicle) {
        this.vehicle = vehicle;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getSyxZhekou() {
        return syxZhekou;
    }

    public void setSyxZhekou(int syxZhekou) {
        this.syxZhekou = syxZhekou;
    }

    public int getJqxZhekou() {
        return jqxZhekou;
    }

    public void setJqxZhekou(int jqxZhekou) {
        this.jqxZhekou = jqxZhekou;
    }

    public double getPremium() {
        return premium;
    }

    public void setPremium(double premium) {
        this.premium = premium;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public double getMyPrice() {
        return myPrice;
    }

    public void setMyPrice(double myPrice) {
        this.myPrice = myPrice;
    }
}
