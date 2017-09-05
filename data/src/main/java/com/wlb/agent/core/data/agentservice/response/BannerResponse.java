package com.wlb.agent.core.data.agentservice.response;

import com.wlb.agent.core.data.agentservice.entity.BannerEntity;
import com.wlb.agent.core.data.base.BaseResponse;

import java.util.List;

/**
 
 @author 曹泽琛
 */
public class BannerResponse extends BaseResponse {

	public List<BannerEntity> list;

	public List<BannerEntity> getList() {
		return list;
	}

	public void setList(List<BannerEntity> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "BannerResponse{" +
				"list=" + list +
				'}';
	}
}
