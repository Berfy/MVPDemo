package com.wlb.agent.core.data.insurance.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.combo.response.ComboResponse;
import com.wlb.agent.core.data.user.response.CouponListResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单详情
 *
 * @author 张全
 */

public class InsuranceOrderDetail extends BaseResponse {

    public InsurancePriceResponse insuranceInfo;
    public long policyId;
    public long vehicleId;
    public String seqNo;
    public String orderNo;//订单号
    public String regionName;//投保城市
    public int statusCode;/// 1 核保中；2核保成功；3核保失败；4待支付；5投保成功；6支付过期;7付款成功
    public String statusMsg;
    public String vehiclePolicyNo;//交强险保单号；
    public String businessPolicyNo;//商业险保单号
    public String tradeNo;
    public long timestamp;
    private int canCancel;
    public int payWay;//支付状态 0不支持支付；1在线支付给保险公司 ；2在线支付给我来保；3线下支付
    public String payForward;//支付链接
    public String receiptTitle;//发票抬头
    public long expirePayTime;

    public String companyCode;//保险公司code
    public String companyName;//保险公司名称
    public double vehicleTotalPrice;//交强险保费
    public double commercialTotalPrice;//商业险保费
    public String servicePhone;//客服电话

    public String licenseNo;//车辆车牌号

    public String owner;//车主姓名
    public String cardNo;//车主身份证号
    public String ownerPhoneNo;//车主电话

    public String insurant;//被保人姓名
    public String insurantCardNo;//被保人身份证号
    public String insurantPhoneNo;//被保人电话

    public String insure;//投保人姓名
    public String insureCardNo;//投保人身份证号
    public String insurePhoneNo;//投保人电话

    public String carModel;//车辆型号
    public String vin;//车架号
    public String engineNo;//发动机号
    public long enrollDate;//车辆初登日期
    public long vehicleStartDate;//交强险起期
    public long vehicleEndDate;//交强险止期
    public long businessStartDate;//商业险起期
    public long businessEndDate;//商业险止期
    private int uploadIDFlag;//是否上传身份证
    public String payMsg;//支付分享信息

    //保单影像
    public List<String> policyImg;

    public int verifyCodeStatus;//0 未填写 1 已填写完成 2 手机号验证失败需要重新填写手机号
    public String verifyPhoneNo; //接收保协验证码手机号

    public double orderPrice; //订单原始价格
    public double discountPrice; //订单优惠金额
    public double packagePrice; // 套餐价格
    public double totalPremium;

    //套餐使用人
    public String goodPhone; //手机号
    public String goodCardNo;//身份证号
    public String goodName;//姓名

    public String ciRate;// 交强险折扣率
    public String biRate;// 商业险折扣率

    //优惠券
    public List<CouponListResponse.CouponEntity> couponList = new ArrayList<>();
    //套餐列表
    public List<ComboResponse.ComboEntity> packageList = new ArrayList<>();


    public boolean canCancel() {
        return canCancel == 1;
    }

    public boolean uploadIDFlag() {
        return uploadIDFlag == 1;
    }

    /**
     * 核保中
     *
     * @return
     */
    public boolean isUnderwriting() {
        return statusCode == 1;
    }

    /**
     * 核保失败
     *
     * @return
     */
    public boolean isUnderwritingFail() {
        return statusCode == 3;
    }

    /**
     * 核保成功,待支付
     *
     * @return
     */
    public boolean isUnderwrigingSucccess() {
        return statusCode == 2;
    }

    /**
     * 线下支付处理中
     *
     * @return
     */
    public boolean offlinePaying() {
        return statusCode == 4;
    }

    /**
     * 投保成功
     *
     * @return
     */
    public boolean isInsuredSuccess() {
        return statusCode == 5;
    }

    /**
     * 支付过期
     *
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
