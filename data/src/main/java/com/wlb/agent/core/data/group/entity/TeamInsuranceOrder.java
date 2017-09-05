package com.wlb.agent.core.data.group.entity;

import java.io.Serializable;

/**
 * Created by JiaHongYa
 */

public class TeamInsuranceOrder implements Serializable {
    public String order_no ;//我来保订单编号
    public String owner;//车主姓名
    public String license_no;//车牌号
    public String company_code;//投保保险公司
    public double bussiness_premium;//商业险总保费
    public double traffic_premium;// 交强险+车船税保费
    public long order_time;//下单时间
    public double total_premium;//总保费
}
