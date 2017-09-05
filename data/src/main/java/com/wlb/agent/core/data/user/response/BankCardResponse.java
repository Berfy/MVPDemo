package com.wlb.agent.core.data.user.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.entity.BankCardInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiaHongYa
 */

public class BankCardResponse extends BaseResponse {
    public List<BankCardInfo> list=new ArrayList<BankCardInfo>();
}
