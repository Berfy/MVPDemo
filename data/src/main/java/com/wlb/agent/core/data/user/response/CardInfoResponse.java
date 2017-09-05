package com.wlb.agent.core.data.user.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.entity.EducationInfo;
import com.wlb.agent.core.data.user.entity.IdAuthInfo;
import com.wlb.agent.core.data.user.entity.ProfessionalInfo;
import com.wlb.agent.core.data.user.entity.QualificationInfo;

/**
 * 我的名片
 * <p>
 * Created by 曹泽琛.
 */

public class CardInfoResponse extends BaseResponse{
    public String avatar;//用户头像
    public int v_flag;//是否加V
    public String city;
    public String email;//绑定的邮箱
    public String phone;//手机号

    public int member_count;//团队人数
    public int custom_count;//客户人数
    public double achievement_count;//业绩金额
    public String my_grade;//等级 1钻石， 2金牌，3银牌

    public String honor;//荣誉

    public IdAuthInfo id_auth_info=new IdAuthInfo();
    public ProfessionalInfo professional=new ProfessionalInfo();
    public EducationInfo education=new EducationInfo();
    public QualificationInfo qualification=new QualificationInfo();

    public boolean isVFlag(){
        return v_flag==1;
    }
}
