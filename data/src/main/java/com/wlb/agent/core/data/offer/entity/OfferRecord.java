package com.wlb.agent.core.data.offer.entity;

/**
 * 报价记录
 * Created by Berfy on 2017/7/17.
 * 待完善
 */

public class OfferRecord {

    private String id;//报价Id
    private String carNumber;//车牌号
    private String offerTime;//报价时间
    private String name;//客户姓名
    private String carBrand;//型号

    public OfferRecord(String id, String carNumber, String offerTime, String name, String carBrand) {
        this.id = id;
        this.carNumber = carNumber;
        this.offerTime = offerTime;
        this.name = name;
        this.carBrand = carBrand;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getOfferTime() {
        return offerTime;
    }

    public void setOfferTime(String offerTime) {
        this.offerTime = offerTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }
}
