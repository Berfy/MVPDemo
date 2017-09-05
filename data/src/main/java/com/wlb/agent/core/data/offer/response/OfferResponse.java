package com.wlb.agent.core.data.offer.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.offer.entity.OfferRecord;

import java.util.List;

/**
 * Created by Berfy on 2017/7/18.
 * 报价记录json
 */

public class OfferResponse extends BaseResponse {

    List<OfferRecord> data;

    public List<OfferRecord> getData() {
        return data;
    }

    public void setData(List<OfferRecord> data) {
        this.data = data;
    }
}
