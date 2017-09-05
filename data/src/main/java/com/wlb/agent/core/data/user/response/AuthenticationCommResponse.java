package com.wlb.agent.core.data.user.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.entity.AuthStatus;

/**
 * 修改职业认证
 *
 * Created by 曹泽琛.
 */

public class AuthenticationCommResponse extends BaseResponse {
    private int auth_status;
    public AuthStatus getAuthStatus(){
        return  AuthStatus.getAuthStatus(auth_status);
    }
    public void setAuthStatus(AuthStatus authStatus){
        auth_status=authStatus.statusCode;
    }
}
