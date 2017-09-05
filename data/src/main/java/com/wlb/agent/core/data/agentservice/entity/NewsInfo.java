package com.wlb.agent.core.data.agentservice.entity;

import java.io.Serializable;

/**
 * 知识库信息
 * 
 * @author jiahongya
 * 
 */
public class NewsInfo implements Serializable{

	private static final long serialVersionUID = 5379430464052154825L;

	public long id;
	public String code;// 所属分类
	public String title;// 文章标题
	public String summary;// 文章简介
	public String icon;// 图标
	public String url;// 文章链接
}
