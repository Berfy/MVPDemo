package com.wlb.agent.core.data.insurance.response;

import com.wlb.agent.core.data.base.BaseResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张全
 */

public class DrivingOrderResponse extends BaseResponse {

    public List<DrivingOrder> lis;

    public List<DrivingOrder> getList() {
        if(null==lis)lis=new ArrayList<>();
        return lis;
    }

    @Override
    public String toString() {
        return "DrivingOrderResponse{" +
                "lis=" + lis +
                '}';
    }

    public static class DrivingOrder implements Serializable {
        /**
         * id : 5
         * orderNo : good12792965347829864
         * orderMount : 99.0
         * userId : 1110
         * orderStatus : 1
         * orderCount : 1
         * createTime : 1484042079000
         * updateTime : 1484042079000
         * packageName : 99元3次代驾
         * packageCode : 0
         * useType : 0
         * packageAmount : 99
         * packageStart : 1483459200000
         * packageEnd : 1514995200000
         * phone : 15678998777
         * name : 黎明
         * cardNo : 362204198811122455
         * license : 京N4c004
         * packageType : 2
         * payUrl : https://test.wolaibao.com/wlb_official/h5/app/payDesk.html?token=NGfL3uCe5XcTMnd6gqOqrhz&orderNo=good12792965347829864
         * packageRegular : <ul><li>仅限北京地区</li><li>仅限本人使用</li></ul>
         * endTime : 1484063679000
         */

        private int id;
        private String orderNo;
        private double orderMount;
        private int userId;
        private int orderStatus;
        private int orderCount;
        private long createTime;
        private long updateTime;
        private String packageName;
        private int packageCode;
        private int useType;
        private int packageAmount;
        private long packageStart;
        private long packageEnd;
        private String phone;
        private String name;
        private String cardNo;
        private String license;
        private int packageType;
        private String payUrl;
        private String packageRegular;
        private long endTime;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public double getOrderMount() {
            return orderMount;
        }

        public void setOrderMount(double orderMount) {
            this.orderMount = orderMount;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getOrderStatus() {
            return orderStatus;
        }

        public void setOrderStatus(int orderStatus) {
            this.orderStatus = orderStatus;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(int orderCount) {
            this.orderCount = orderCount;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public long getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(long updateTime) {
            this.updateTime = updateTime;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public int getPackageCode() {
            return packageCode;
        }

        public void setPackageCode(int packageCode) {
            this.packageCode = packageCode;
        }

        public int getUseType() {
            return useType;
        }

        public void setUseType(int useType) {
            this.useType = useType;
        }

        public int getPackageAmount() {
            return packageAmount;
        }

        public void setPackageAmount(int packageAmount) {
            this.packageAmount = packageAmount;
        }

        public long getPackageStart() {
            return packageStart;
        }

        public void setPackageStart(long packageStart) {
            this.packageStart = packageStart;
        }

        public long getPackageEnd() {
            return packageEnd;
        }

        public void setPackageEnd(long packageEnd) {
            this.packageEnd = packageEnd;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCardNo() {
            return cardNo;
        }

        public void setCardNo(String cardNo) {
            this.cardNo = cardNo;
        }

        public String getLicense() {
            return license;
        }

        public void setLicense(String license) {
            this.license = license;
        }

        public int getPackageType() {
            return packageType;
        }

        public void setPackageType(int packageType) {
            this.packageType = packageType;
        }

        public String getPayUrl() {
            return payUrl;
        }

        public void setPayUrl(String payUrl) {
            this.payUrl = payUrl;
        }

        public String getPackageRegular() {
            return packageRegular;
        }

        public void setPackageRegular(String packageRegular) {
            this.packageRegular = packageRegular;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public String getOrderMsg(){
            String orderMsg=null;
            if(orderStatus==1){
                orderMsg="待支付";
            }else if(orderStatus==2){
                orderMsg="支付成功";
            }else if(orderStatus==3){
                orderMsg="已失效";
            }
            return orderMsg;
        }

        @Override
        public String toString() {
            return "DrivingOrder{" +
                    "id=" + id +
                    ", orderNo='" + orderNo + '\'' +
                    ", orderMount=" + orderMount +
                    ", userId=" + userId +
                    ", orderStatus=" + orderStatus +
                    ", orderCount=" + orderCount +
                    ", createTime=" + createTime +
                    ", updateTime=" + updateTime +
                    ", packageName='" + packageName + '\'' +
                    ", packageCode=" + packageCode +
                    ", useType=" + useType +
                    ", packageAmount=" + packageAmount +
                    ", packageStart=" + packageStart +
                    ", packageEnd=" + packageEnd +
                    ", phone='" + phone + '\'' +
                    ", name='" + name + '\'' +
                    ", cardNo='" + cardNo + '\'' +
                    ", license='" + license + '\'' +
                    ", packageType=" + packageType +
                    ", payUrl='" + payUrl + '\'' +
                    ", packageRegular='" + packageRegular + '\'' +
                    ", endTime=" + endTime +
                    '}';
        }
    }
}
