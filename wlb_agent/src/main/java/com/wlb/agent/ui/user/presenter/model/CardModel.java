package com.wlb.agent.ui.user.presenter.model;

import com.wlb.agent.core.data.user.entity.EducationInfo;
import com.wlb.agent.core.data.user.entity.IdAuthInfo;
import com.wlb.agent.core.data.user.entity.ProfessionalInfo;
import com.wlb.agent.core.data.user.entity.QualificationInfo;

/**
 * @author 张全
 */

public class CardModel {
    public String avatar;//用户头像
    public boolean v_flag;//是否加V
    public String email;//绑定的邮箱
    public String phone;//手机号

    public int member_count;//团队人数
    public int custom_count;//客户人数
    public double achievement_count;//业绩金额
    public String my_grade;//等级 1钻石， 2金牌，3银牌

    public IdAuthInfo id_auth_info;

    public String honor;//荣誉

    public ProfessionalInfo professionalInfo = new ProfessionalInfo();//职业信息
    public EducationInfo educationInfo = new EducationInfo();//教育信息
    public QualificationInfo qualificationInfo = new QualificationInfo();//资格信息
}
