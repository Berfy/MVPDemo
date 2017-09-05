package com.wlb.agent.core.data.user.entity;

import java.io.Serializable;

/**
 * 教育认证
 * <p>
 * Created by 曹泽琛.
 */

public class EducationInfo implements Serializable {
    public String school;//学校
    public String subject;//专业
    public String educational;//学历
    public long enrollment_time;//入学时间
    public long graduation_time;//毕业时间
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
