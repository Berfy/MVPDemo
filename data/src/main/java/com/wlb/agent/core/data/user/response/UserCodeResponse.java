package com.wlb.agent.core.data.user.response;

import com.wlb.agent.core.data.base.BaseResponse;

/**
 * Created by 曹泽琛.
 */

public class UserCodeResponse extends BaseResponse {
    //该手机号是否已注册  1已注册   0未注册
    private int isRegisted;
    public boolean isRegisted(){
        return isRegisted==1;
    }
}
