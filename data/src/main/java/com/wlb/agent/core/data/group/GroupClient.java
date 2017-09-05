package com.wlb.agent.core.data.group;

import com.android.util.http.NetClient;
import com.android.util.http.callback.ICallback;
import com.android.util.http.response.GsonParser;
import com.android.util.http.task.Task;
import com.wlb.agent.core.data.ApiHost;
import com.wlb.agent.core.data.base.RequestBuilder;
import com.wlb.agent.core.data.base.SimpleJsonParser;
import com.wlb.agent.core.data.group.response.TeamInsuranceOrderResponse;
import com.wlb.agent.core.data.group.response.TeamSummaryResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JiaHongYa
 * 团队管理
 */

public class GroupClient {

    /**
     * 获取团队概况信息
     *
     * @param callback
     * @return
     */
    public static Task doGetTeamSummary(ICallback callback) {
        RequestBuilder builder = RequestBuilder.getBuilder(ApiHost.TEAM_SUMMARY, callback,new GsonParser<>(TeamSummaryResponse.class));
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 上传团队名称
     *
     * @param team_name 团队名称
     * @param callback
     */
    public static Task doModifyTeamName(String team_name, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.MODIFY_NAME, callback, new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("team_name", team_name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 团队成员待支付订单列表
     *
     * @param member_user_id 成员用户编号
     * @param callback
     */
    public static Task doGetTeamMemberOrder(String member_user_id, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.TEAMMEMBER_ORDER, callback,new GsonParser<>(TeamInsuranceOrderResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("member_user_id", member_user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 团队成员待支付订单催单
     *
     * @param order_no 成员用户订单编号
     * @param callback
     */
    public static Task doTeamMemberReminder(String order_no, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.TEAMMEMBER_REMINDER, callback, new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("order_no", order_no);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }
}
