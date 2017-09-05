package com.wlb.agent.core.data.user.response;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.entity.Score;

import java.util.List;

/**
 * Created by Berfy on 2017/7/25.
 * 积分json
 */

public class ScoreResponse extends BaseResponse {

    List<Score> data;

    public ScoreResponse(List<Score> data) {
        this.data = data;
    }

    public List<Score> getData() {
        return data;
    }

    public void setData(List<Score> data) {
        this.data = data;
    }
}
