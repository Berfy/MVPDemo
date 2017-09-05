package com.wlb.agent.core.data.offer.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.offer.entity.OfferShare;

import java.util.List;

/**
 * Created by Berfy on 2017/8/2.
 * 报价分享列表
 */
public class OfferShareResponse extends BaseResponse {

    private List<OfferShare> list;

    public List<OfferShare> getList() {
        return list;
    }

    public void setList(List<OfferShare> list) {
        this.list = list;
    }
}
