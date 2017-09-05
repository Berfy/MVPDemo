package com.wlb.agent.core.data.offer.entity;

/**
 * 报价分享保存给后台
 * Created by Berfy on 2017/7/17.
 *
 */
public class OfferShareSave {
    private String seqNo;//报价订单号
    private String companyCode;//保险公司代码
    private double premium;//推广费

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

    public double getPremium() {
        return premium;
    }

    public void setPremium(double premium) {
        this.premium = premium;
    }
}
