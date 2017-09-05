package com.wlb.agent.core.data.user.entity;

import java.io.Serializable;

/**
 * 当日积分状态信息
 *
 * Created by 曹泽琛.
 */

public class IntegralStatusInfo implements Serializable {

    public int type;//任务类型
    public String name;//任务名称
    public int score;//分值
    public String desc;//任务描述
    private int status;// 完成状态 0-未完成 1-已完成

    public boolean isCompleted(){
        return status==1;
    }

}
