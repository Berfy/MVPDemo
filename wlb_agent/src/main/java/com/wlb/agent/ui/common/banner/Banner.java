package com.wlb.agent.ui.common.banner;

/**
 * Created by Berfy on 2017/7/13.
 * 广告
 */

public class Banner {

	private String title;
	private int type;
	private String url;
	private String pageUrl;//跳转url

	public Banner(String title, int type, String url,String pageUrl) {
		this.title = title;
		this.type = type;
		this.url = url;
		this.pageUrl = pageUrl;
	}

	public Banner(String title, int type, String url) {
		this.title = title;
		this.type = type;
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
}
