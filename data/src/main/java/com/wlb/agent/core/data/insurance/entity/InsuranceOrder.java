package com.wlb.agent.core.data.insurance.entity;

import java.io.Serializable;

/**
 * 车险订单
 * @author 张全
 */
public class InsuranceOrder implements Serializable {
    private static final long serialVersionUID = 3129341981430067613L;
    public long policyId;
    public long vehicleId;//保单id
    public String orderNo;//订单号
    public String licenseNo;//车辆车牌号
    public String owner;//车主姓名
    public int statusCode;/// 1 核保中；2核保成功；3核保失败；4待支付；5投保成功；6支付过期;7付款成功
    public String statusMsg;
    public long timestamp;
    public String companyCode;//保险公司code
    public String companyName;//保险公司名称
    public double vehicleTotalPrice;//交强险总保费
    public double vehiclePrice;//交强险
    public double taxPrice; //车船税
    public double commercialTotalPrice;//商业险保费
    public String servicePhone;//客服电话
    public double packagePrice; // 套餐价格
    public double discountPrice; //订单优惠金额
    public double totalCommission; //总推广费
    public double orderPrice; //订单原始价格
    private int canCancel;
    private int uploadIDFlag;
    public long expirePayTime;
    public double totalPremium;
    public String payForward;//支付链接

    public int verifyCodeStatus;//0 未填写 1 已填写完成 2 手机号验证失败需要重新填写手机号
    public String verifyPhoneNo; //接收保协验证码手机号

    public boolean canCancel(){
        return canCancel==1;
    }
    public boolean uploadIDFlag(){
        return uploadIDFlag==1;
    }
    /**
     * 核保中
     * @return
     */
    public boolean isUnderwriting() {
        return statusCode == 1;
    }
    /**
     * 核保失败
     * @return
     */
    public boolean isUnderwritingFail() {
        return statusCode == 3;
    }

    /**
     * 核保成功,待支付
     * @return
     */
    public boolean isUnderwrigingSucccess() {
        return statusCode == 2;
    }

    /**
     * 线下支付处理中
     * @return
     */
    public boolean offlinePaying() {
        return statusCode == 4;
    }

    /**
     * 投保成功
     * @return
     */
    public boolean isInsuredSuccess() {
        return statusCode == 5;
    }

    /**
     * 支付过期
     * @return
     */
    public boolean isDisabled() {
        return statusCode == 6;
    }

    /**
     * 付款成功
     */
    public boolean isPaySucceed() {
        return statusCode == 7;
    }




}
