package com.wlb.agent.core.data.user.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.entity.WalletFlowInfo;

import java.util.List;

/**
 * 钱包流水
 * @author 张全
 */
public class WalletFlowResponse extends BaseResponse {
    public List<WalletFlowInfo> list;
}
