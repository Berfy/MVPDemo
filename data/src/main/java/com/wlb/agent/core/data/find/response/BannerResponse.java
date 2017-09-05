package com.wlb.agent.core.data.find.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.find.entity.Banner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Berfy on 2017/7/28.
 * 广告轮播图
 */
public class BannerResponse extends BaseResponse {

    public List<Banner> list;

    public List<Banner> getList() {
        if (null == list) {
            return new ArrayList<>();
        } else {
            return list;
        }
    }
}
