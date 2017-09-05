package com.wlb.agent.core.data.insurance.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.insurance.entity.InsuranceOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * 核保下单
 * 
 * @author 张全
 */
public class InsuranceOrderResponse extends BaseResponse {
	private List<InsuranceOrder> list;

	public  List<InsuranceOrder> getList(){
		if(null==list)list=new ArrayList<>();
		return list;
	}

}
