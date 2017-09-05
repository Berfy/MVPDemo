package com.wlb.agent.core.data.agentservice.response;

import com.wlb.agent.core.data.base.BaseResponse;

/**
 * 版本升级信息
 * @author 张全
 */
public class VersionResponse extends BaseResponse {
    public String versionName;
    public int versionCode;
    public String url;
    public String description;
    public String from;
}
