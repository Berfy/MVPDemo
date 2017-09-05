package com.wlb.agent.core.data.offer.entity;

/**
 * 车型
 * Created by Berfy on 2017/7/12.
 * 待完善
 */

public class CarBrand {

    private String id;
    private String brand;
    private String price;
    private boolean isSelect;

    public CarBrand(String id, String brand, String price, boolean isSelect) {
        this.id = id;
        this.brand = brand;
        this.price = price;
        this.isSelect = isSelect;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String bland) {
        this.brand = bland;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
