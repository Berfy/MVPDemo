package com.wlb.agent.core.data.base;

import java.io.Serializable;


/**
 * 接口响应信息基类
 *
 * @author 张全
 */
public class BaseResponse implements Serializable {
    private int status;// 服务器响应状态码
    public String msg;

    public boolean isSuccessful() {
        return status == 0;
    }
}
