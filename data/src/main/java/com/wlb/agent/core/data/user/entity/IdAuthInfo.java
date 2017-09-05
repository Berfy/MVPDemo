package com.wlb.agent.core.data.user.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 实名认证
 * Created by JiaHongYa
 */

public class IdAuthInfo implements Serializable {
    public String real_name;//姓名
    public String certificate_no;//身份证
    public List<String> cert_url=new ArrayList<>();//身份证正反面
    private int id_auth_status;
    public String tip;

    public AuthStatus getAuthStatus(){
       return  AuthStatus.getAuthStatus(id_auth_status);
    }
    public void setAuthStatus(AuthStatus authStatus){
        id_auth_status=authStatus.statusCode;
    }
}
