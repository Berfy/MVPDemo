package com.wlb.agent.core.data.agentservice.response;

import com.wlb.agent.core.data.agentservice.entity.NewsInfo;
import com.wlb.agent.core.data.base.BaseResponse;

import java.util.ArrayList;
import java.util.List;

public class NewsListResponse extends BaseResponse {
	private static final long serialVersionUID = -757081224363998235L;
	public List<NewsInfo> list = new ArrayList<NewsInfo>();
}
