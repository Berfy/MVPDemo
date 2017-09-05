package com.wlb.agent.core.data.offer.entity;

/**
 * 保险险种
 * Created by Berfy on 2017/7/12.
 * 待完善
 */

public class InsurerConverage {

    private String id;
    private String name;
    private String toubao_price;//投保金额,0或空不投保
    private String price;//保费
    private boolean isShowBuJiMianPei;//是否显示不计免赔
    private boolean isSelect;//是否选择
    private boolean isBuJiMianPeiSelect;//是否选择不计免赔

    public InsurerConverage(String id, String name, String toubao_price, String price, boolean isShowBuJiMianPei,
                            boolean isBuJiMianPeiSelect, boolean isSelect) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.toubao_price = toubao_price;
        this.isShowBuJiMianPei = isShowBuJiMianPei;
        this.isBuJiMianPeiSelect = isBuJiMianPeiSelect;
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

    public String getToubao_price() {
        return toubao_price;
    }

    public void setToubao_price(String toubao_price) {
        this.toubao_price = toubao_price;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isShowBuJiMianPei() {
        return isShowBuJiMianPei;
    }

    public void setShowBuJiMianPei(boolean showBuJiMianPei) {
        isShowBuJiMianPei = showBuJiMianPei;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isBuJiMianPeiSelect() {
        return isBuJiMianPeiSelect;
    }

    public void setBuJiMianPeiSelect(boolean buJiMianPeiSelect) {
        isBuJiMianPeiSelect = buJiMianPeiSelect;
    }
}
