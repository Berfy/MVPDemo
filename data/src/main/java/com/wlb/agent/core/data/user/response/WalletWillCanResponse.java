package com.wlb.agent.core.data.user.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.entity.WalletFlowInfo;

import java.util.List;

/**
 * Created by Berfy on 2017/7/27.
 * 待审核金额明细
 */
public class WalletWillCanResponse extends BaseResponse {

    public List<WalletFlowInfo> list;
}
