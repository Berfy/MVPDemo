package com.wlb.agent.core.data.offer;

import com.android.util.http.NetClient;
import com.android.util.http.callback.ICallback;
import com.android.util.http.response.GsonParser;
import com.android.util.http.task.Task;
import com.wlb.agent.core.data.ApiHost;
import com.wlb.agent.core.data.base.RequestBuilder;
import com.wlb.agent.core.data.base.SimpleJsonParser;
import com.wlb.agent.core.data.offer.response.OfferShareResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Berfy on 2017/7/31.
 * 报价相关接口
 */
public class OfferClient {

    public static Task saveOfferSharePremiumPrice(String listJson, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.OFFER_SHARE_SAVE,
                callback, new SimpleJsonParser());
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("list", new JSONArray(listJson));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    public static Task getOfferShareList(String seqNos, ICallback callback) {
        RequestBuilder builder = RequestBuilder.getBuilder(ApiHost.OFFER_SHARE_LIST
                        + "?seqNos=" + seqNos,
                callback, new GsonParser<>(OfferShareResponse.class));
//        JSONObject postJson = builder.getPostJson();
//        try {
//            postJson.put("seqNos", seqNos);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }
}
