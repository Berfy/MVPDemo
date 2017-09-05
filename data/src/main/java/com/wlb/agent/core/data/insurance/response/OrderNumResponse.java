package com.wlb.agent.core.data.insurance.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.insurance.entity.OrderNum;

import java.util.List;

/**
 * Created by Berfy on 2017/8/1.
 * 订单数
 */

public class OrderNumResponse extends BaseResponse {

    private List<OrderNum> list;

    public List<OrderNum> getList() {
        return list;
    }

    public void setList(List<OrderNum> list) {
        this.list = list;
    }
}
