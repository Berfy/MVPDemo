package com.wlb.agent.core.data.offer.entity;

import java.util.List;

/**
 * 报价
 * Created by Berfy on 2017/7/17.
 * 待完善
 */

public class Offer {

    private String id;//保险公司id
    private String name;//保险公司
    private String oldPrice;//原价
    private String price;//折后价格
    private double jqxZhekou;//交强险折扣率
    private double syxZhekou;//商业险折扣率
    private String jqxPrice;//交强险
    private String ccsPrice;//车船税
    private String syxTotalPrice;//商业险合计
    private List<OfferSyx> syxList;//商业险列表

    public Offer(String id, String name, String oldPrice, String price, double jqxZhekou, double syxZhekou, String jqxPrice, String ccsPrice, String syxTotalPrice, List<OfferSyx> syxList) {
        this.id = id;
        this.name = name;
        this.oldPrice = oldPrice;
        this.price = price;
        this.jqxZhekou = jqxZhekou;
        this.syxZhekou = syxZhekou;
        this.jqxPrice = jqxPrice;
        this.ccsPrice = ccsPrice;
        this.syxTotalPrice = syxTotalPrice;
        this.syxList = syxList;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public double getJqxZhekou() {
        return jqxZhekou;
    }

    public void setJqxZhekou(double jqxZhekou) {
        this.jqxZhekou = jqxZhekou;
    }

    public double getSyxZhekou() {
        return syxZhekou;
    }

    public void setSyxZhekou(double syxZhekou) {
        this.syxZhekou = syxZhekou;
    }

    public String getJqxPrice() {
        return jqxPrice;
    }

    public void setJqxPrice(String jqxPrice) {
        this.jqxPrice = jqxPrice;
    }

    public String getCcsPrice() {
        return ccsPrice;
    }

    public void setCcsPrice(String ccsPrice) {
        this.ccsPrice = ccsPrice;
    }

    public String getSyxTotalPrice() {
        return syxTotalPrice;
    }

    public void setSyxTotalPrice(String syxTotalPrice) {
        this.syxTotalPrice = syxTotalPrice;
    }

    public List<OfferSyx> getSyxList() {
        return syxList;
    }

    public void setSyxList(List<OfferSyx> syxList) {
        this.syxList = syxList;
    }

    public String getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(String oldPrice) {
        this.oldPrice = oldPrice;
    }
}
