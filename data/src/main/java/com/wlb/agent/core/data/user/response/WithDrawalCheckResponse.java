package com.wlb.agent.core.data.user.response;

import com.wlb.agent.core.data.base.BaseResponse;

/**
 * Created by Berfy on 2017/7/31.
 * 提现检查是否设置提现密码和实名认证
 */
public class WithDrawalCheckResponse extends BaseResponse {

    public int needPWD;//是否需要设置密码
    public int needAuth;//是否需要认证
}
