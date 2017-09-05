package com.wlb.agent.core.data.user.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.entity.EducationInfo;
import com.wlb.agent.core.data.user.entity.ProfessionalInfo;
import com.wlb.agent.core.data.user.entity.QualificationInfo;

/**
 * 认证信息
 * <p>
 * Created by 曹泽琛.
 */

public class AuthenticationResponse extends BaseResponse {
    public ProfessionalInfo professional=new ProfessionalInfo() ;//职业
    public EducationInfo education=new EducationInfo() ;//教育
    public QualificationInfo qualification=new QualificationInfo() ;//资格
}
