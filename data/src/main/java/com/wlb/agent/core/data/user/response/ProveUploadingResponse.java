package com.wlb.agent.core.data.user.response;

import com.wlb.agent.core.data.base.BaseResponse;

/**
 * 实名认证
 *
 * Created by 曹泽琛.
 */

public class ProveUploadingResponse extends BaseResponse {
    //是否实名认证   0未认证，1 审核中  2审核失败 3已认证
    public int id_auth_status;
}
