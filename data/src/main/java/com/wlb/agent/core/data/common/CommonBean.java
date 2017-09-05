package com.wlb.agent.core.data.common;

/**
 * Created by Berfy on 2017/7/10.
 * 通用数据
 */

public class CommonBean<E> {
    public CommonBean(E data) {
        this.data = data;
    }

    private int type;//页面类型，区分多个列表页面类型
    private E data;

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
