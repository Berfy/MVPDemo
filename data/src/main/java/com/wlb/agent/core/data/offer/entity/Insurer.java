package com.wlb.agent.core.data.offer.entity;

import java.io.Serializable;

/**
 * 保险公司
 * Created by Berfy on 2017/7/12.
 * 待完善
 */

public class Insurer implements Serializable {

    private String id;
    private String name;
    private boolean isSelect;
    private String seqNo;//保险公司id
    private String companyCode;//保险公司代码
    private double businessTotalPrice;//商业险报价
    private double vehiclePrice;//交强险报价
    private double taxPrice;//车船税报价

    public Insurer(String id, String name, boolean isSelect) {
        this.id = id;
        this.name = name;
        this.isSelect = isSelect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public double getBusinessTotalPrice() {
        return businessTotalPrice;
    }

    public void setBusinessTotalPrice(double businessTotalPrice) {
        this.businessTotalPrice = businessTotalPrice;
    }

    public double getVehiclePrice() {
        return vehiclePrice;
    }

    public void setVehiclePrice(double vehiclePrice) {
        this.vehiclePrice = vehiclePrice;
    }

    public double getTaxPrice() {
        return taxPrice;
    }

    public void setTaxPrice(double taxPrice) {
        this.taxPrice = taxPrice;
    }
}
