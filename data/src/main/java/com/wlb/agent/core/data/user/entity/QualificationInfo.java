package com.wlb.agent.core.data.user.entity;

import java.io.Serializable;

/**
 * 资格认证
 * <p>
 * Created by 曹泽琛.
 */

public class QualificationInfo implements Serializable {
    public String certificate;//资格证书名称
    public String cert_no;//证书号码
    public long award_time;//发证日期
    public String cert_url;//证件照
    private int auth_status;
    public long update_time;//审核时间
    public String tip;//提示消息
    public AuthStatus getAuthStatus(){
        return  AuthStatus.getAuthStatus(auth_status);
    }
    public void setAuthStatus(AuthStatus authStatus){
        auth_status=authStatus.statusCode;
    }
}
