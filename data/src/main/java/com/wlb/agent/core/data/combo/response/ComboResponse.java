package com.wlb.agent.core.data.combo.response;

import com.wlb.agent.core.data.base.BaseResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张全
 */

public class ComboResponse extends BaseResponse{

    /**
     * status : 0
     * msg : SUCCESS
     * recordCount : 3
     * list : [{"id":22,"packageName":"66元3次代驾","packageCode":1481533150666,"userId":8,"useId":9,"useType":4,"orderNo":null,"packageAmount":66,"packageStart":1481523929000,"packageEnd":1481523929000,"useFlag":1,"validFlag":2,"packageType":2,"createTime":1481533154000,"updateTime":1481533154000,"packageRegular":"66元3次代驾","originAmount":0,"realAmount":0},{"id":35,"packageName":"99元6次代驾","packageCode":1481533219622,"userId":8,"useId":9,"useType":4,"orderNo":null,"packageAmount":99,"packageStart":1481523929000,"packageEnd":1481523929000,"useFlag":1,"validFlag":2,"packageType":2,"createTime":1481533223000,"updateTime":1481533223000,"packageRegular":"99元享受安联6次代驾","originAmount":0,"realAmount":0},{"id":41,"packageName":"136元次代驾","packageCode":1481533376181,"userId":8,"useId":9,"useType":4,"orderNo":null,"packageAmount":136,"packageStart":1481523929000,"packageEnd":1481523929000,"useFlag":1,"validFlag":2,"packageType":2,"createTime":1481533379000,"updateTime":1481533379000,"packageRegular":"136元享受安联9次代驾","originAmount":0,"realAmount":0}]
     */

    private int recordCount;
    private List<ComboEntity> list;
    private int type;
    private String phone;
    private String cardNo;
    private String name;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(int recordCount) {
        this.recordCount = recordCount;
    }

    public List<ComboEntity> getList() {
        if(null==list)list=new ArrayList<>();
        return list;
    }

    public void setList(List<ComboEntity> list) {
        this.list = list;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static class ComboEntity implements Serializable {
        /**
         * id : 22
         * packageName : 66元3次代驾
         * packageCode : 1481533150666
         * userId : 8
         * useId : 9
         * useType : 4
         * orderNo : null
         * packageAmount : 66
         * packageStart : 1481523929000
         * packageEnd : 1481523929000
         * useFlag : 1
         * validFlag : 2
         * packageType : 2
         * createTime : 1481533154000
         * updateTime : 1481533154000
         * packageRegular : 66元3次代驾
         * originAmount : 0
         * realAmount : 0
         */

        private int id;
        private String packageName;
        private long packageCode;
        private int userId;
        private int useId;
        private int useType;
        private Object orderNo;
        private double packageAmount;
        private long packageStart;
        private long packageEnd;
        private int useFlag;
        private int validFlag;
        private int packageType;
        private long createTime;
        private long updateTime;
        private String packageRegular;
        private double originAmount;
        private double realAmount;
        private String url;
        private String serviceCall;

        public int isSelect;

        //

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public long getPackageCode() {
            return packageCode;
        }

        public void setPackageCode(long packageCode) {
            this.packageCode = packageCode;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getUseId() {
            return useId;
        }

        public void setUseId(int useId) {
            this.useId = useId;
        }

        public int getUseType() {
            return useType;
        }

        public void setUseType(int useType) {
            this.useType = useType;
        }

        public Object getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(Object orderNo) {
            this.orderNo = orderNo;
        }

        public double getPackageAmount() {
            return packageAmount;
        }

        public void setPackageAmount(double packageAmount) {
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

        public int getUseFlag() {
            return useFlag;
        }

        public void setUseFlag(int useFlag) {
            this.useFlag = useFlag;
        }

        public int getValidFlag() {
            return validFlag;
        }

        public void setValidFlag(int validFlag) {
            this.validFlag = validFlag;
        }

        public int getPackageType() {
            return packageType;
        }

        public void setPackageType(int packageType) {
            this.packageType = packageType;
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

        public String getPackageRegular() {
            return packageRegular;
        }

        public void setPackageRegular(String packageRegular) {
            this.packageRegular = packageRegular;
        }

        public double getOriginAmount() {
            return originAmount;
        }

        public void setOriginAmount(double originAmount) {
            this.originAmount = originAmount;
        }

        public double getRealAmount() {
            return realAmount;
        }

        public void setRealAmount(double realAmount) {
            this.realAmount = realAmount;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getServiceCall() {
            return serviceCall;
        }

        public void setServiceCall(String serviceCall) {
            this.serviceCall = serviceCall;
        }

        @Override
        public String toString() {
            return "ComboEntity{" +
                    "id=" + id +
                    ", packageName='" + packageName + '\'' +
                    ", packageCode=" + packageCode +
                    ", userId=" + userId +
                    ", useId=" + useId +
                    ", useType=" + useType +
                    ", orderNo=" + orderNo +
                    ", packageAmount=" + packageAmount +
                    ", packageStart=" + packageStart +
                    ", packageEnd=" + packageEnd +
                    ", useFlag=" + useFlag +
                    ", validFlag=" + validFlag +
                    ", packageType=" + packageType +
                    ", createTime=" + createTime +
                    ", updateTime=" + updateTime +
                    ", packageRegular='" + packageRegular + '\'' +
                    ", originAmount=" + originAmount +
                    ", realAmount=" + realAmount +
                    ", isSelect=" + isSelect +
                    '}';
        }
    }
}
