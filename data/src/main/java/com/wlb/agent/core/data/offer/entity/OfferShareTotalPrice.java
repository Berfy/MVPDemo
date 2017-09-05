package com.wlb.agent.core.data.offer.entity;

/**
 * Created by Berfy on 2017/8/2.
 * 报价分享-保险总额
 */

public class OfferShareTotalPrice {
    private double businessPrice;//商业险
    private double vehiclePrice;//交强险
    private double taxPrice;//车船税
    private double businessTotalPrice;//商业险总额
    private double vehicleTotalPrice;//交强险车船税总额

    public double getBusinessPrice() {
        return businessPrice;
    }

    public void setBusinessPrice(double businessPrice) {
        this.businessPrice = businessPrice;
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

    public double getBusinessTotalPrice() {
        return businessTotalPrice;
    }

    public void setBusinessTotalPrice(double businessTotalPrice) {
        this.businessTotalPrice = businessTotalPrice;
    }

    public double getVehicleTotalPrice() {
        return vehicleTotalPrice;
    }

    public void setVehicleTotalPrice(double vehicleTotalPrice) {
        this.vehicleTotalPrice = vehicleTotalPrice;
    }
}
