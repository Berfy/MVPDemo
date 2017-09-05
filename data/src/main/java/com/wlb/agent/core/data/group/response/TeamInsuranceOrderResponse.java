package com.wlb.agent.core.data.group.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.group.entity.TeamInsuranceOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiaHongYa
 */

public class TeamInsuranceOrderResponse extends BaseResponse {
    public long  member_user_id; //用户编号（目前没有用）
    public List<TeamInsuranceOrder> list=new ArrayList<TeamInsuranceOrder>();
}
