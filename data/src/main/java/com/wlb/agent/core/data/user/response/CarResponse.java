package com.wlb.agent.core.data.user.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.entity.Ivehicle;

import java.util.List;

/**
 * 车辆接口响应信息
 * @author 张全
 */
public class CarResponse extends BaseResponse {
	private static final long serialVersionUID = -7400164646925364456L;
	// 车辆
	public List<Ivehicle> list;
}
