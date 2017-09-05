package com.wlb.agent.core.data.offer.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Berfy on 2017/7/28.
 * H5报价-分享传过来的选择的保险公司的报价信息
 */

public class OfferShareChooseData implements Serializable {

    private String vehicleId;//订单id
    private List<Insurer> company;

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public List<Insurer> getCompany() {
        return company;
    }

    public void setCompany(List<Insurer> company) {
        this.company = company;
    }
}
