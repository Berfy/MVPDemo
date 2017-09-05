package com.wlb.agent.core.data.user.entity;

import java.io.Serializable;

/**
 * 车辆
 *
 * @author 张全
 */
public class Ivehicle implements  Serializable {

    public long vehicleId;// 车辆id
    public String licenseNo;// 车牌号 ，比如：京NHC790
    public String owner;// 车主姓名
    public long vehicleExpiredDate;
    public long businessExpiredDate;
    public String region;
    public int ensurable;
    public long lastQuotationTime;
    public long createTime;
    public String showText;
    public IvehicleModel model;
    public String phoneNo;

    public boolean isNormalStatus() {
        return ensurable == 1;
    }

}
