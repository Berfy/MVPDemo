package com.wlb.agent.core.data.find;

import com.android.util.http.NetClient;
import com.android.util.http.callback.ICallback;
import com.android.util.http.response.GsonParser;
import com.android.util.http.task.Task;
import com.wlb.agent.core.data.ApiHost;
import com.wlb.agent.core.data.base.RequestBuilder;
import com.wlb.agent.core.data.find.response.BannerResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Berfy on 2017/7/31.
 */

public class FindClient {

    public static Task getBanner(ICallback callback){
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.BANNER,
                callback, new GsonParser<>(BannerResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("type", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        builder.setPostJsonData(postJson);

        return NetClient.getInstance().execute(builder.build());
    }
}
