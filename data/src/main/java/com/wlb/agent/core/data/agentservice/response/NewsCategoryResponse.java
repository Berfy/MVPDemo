package com.wlb.agent.core.data.agentservice.response;

import com.wlb.agent.core.data.agentservice.entity.NewsCategory;
import com.wlb.agent.core.data.base.BaseResponse;

import java.util.ArrayList;
import java.util.List;

public class NewsCategoryResponse extends BaseResponse {
	private static final long serialVersionUID = -357928252395359132L;

	public List<NewsCategory> list = new ArrayList<NewsCategory>();

}
