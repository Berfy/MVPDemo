package com.wlb.agent.core.data.agentservice.response;

import com.wlb.agent.core.data.agentservice.entity.MessageInfo;
import com.wlb.agent.core.data.base.BaseResponse;

import java.util.List;

/**
 * @author 张全
 */
public class MessageResponse extends BaseResponse {
    /**
     * 未读消息数
     */
    public int tipCount;
    public List<MessageInfo> list;
}
