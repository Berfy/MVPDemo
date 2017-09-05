package com.wlb.agent.core.data.user.entity;

import java.io.Serializable;

/**
 * 职业认证
 * <p>
 * Created by 曹泽琛.
 */

public class ProfessionalInfo implements Serializable {
    public String company_name;//公司名称
    public String professional_name;//职位名称
    public String cert_url;//证件照
    private int  auth_status;
    public long update_time;//审核时间
    public String tip;//提示信息
    public AuthStatus getAuthStatus(){
        return  AuthStatus.getAuthStatus(auth_status);
    }
    public void setAuthStatus(AuthStatus authStatus){
        auth_status=authStatus.statusCode;
    }
}
