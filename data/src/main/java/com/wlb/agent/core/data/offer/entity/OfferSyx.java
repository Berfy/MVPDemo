package com.wlb.agent.core.data.offer.entity;

/**
 * 报价-商业险
 * Created by Berfy on 2017/7/17.
 * 待完善
 */

public class OfferSyx {

    private String name;//保险名称
    private int type;//0主险 1附加险
    private String price;//保费
    private String converage;//保额

    public OfferSyx(String name, int type, String price, String converage) {
        this.name = name;
        this.type = type;
        this.price = price;
        this.converage = converage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getConverage() {
        return converage;
    }

    public void setConverage(String converage) {
        this.converage = converage;
    }
}
