package com.wlb.agent.core.data.agentservice.entity;

import java.io.Serializable;

/**
 
 @author 曹泽琛
 */
public class BannerEntity implements Serializable {

	private static final long serialVersionUID = -3291642112784557364L;
	public long id;
	public String thumbImage;//图片url
	public String webUrl;//跳转链接
	public String title;//图片标题

	@Override
	public String toString() {
		return "BannerEntity{" +
				"id=" + id +
				", thumbImage='" + thumbImage + '\'' +
				", webUrl='" + webUrl + '\'' +
				", title='" + title + '\'' +
				'}';
	}
}
