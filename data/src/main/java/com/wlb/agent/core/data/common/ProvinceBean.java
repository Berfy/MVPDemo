package com.wlb.agent.core.data.common;

import java.util.List;

/**
 * Created by Berfy on 2017/7/7.
 * 省份
 */

public class ProvinceBean {

    private String name;
    private List<CityBean> sub;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityBean> getSub() {
        return sub;
    }

    public void setSub(List<CityBean> sub) {
        this.sub = sub;
    }
}
