package com.wlb.agent.core.data.insurance.entity;

/**
 * Created by Berfy on 2017/8/1.
 * 订单数
 */

public class OrderNum {

    private int orderCount;// 该类型的订单数量
    private int status;//订单状态

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
