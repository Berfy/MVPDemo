package com.wlb.agent.core.data.combo;

import com.wlb.agent.core.data.ApiHost;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.base.RestClient;
import com.wlb.agent.core.data.base.RestRequest;
import com.wlb.agent.core.data.combo.response.ComboResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author 张全
 * 我的套餐接口
 */

public class ComboClient {
    private static ComboService getService() {
        return RestClient.getService(ComboService.class);
    }

    /**
     * 获取我的套餐列表
     *
     * @return
     */
    public static Observable<ComboResponse> doGetUserComboList() {
        RestRequest request = new RestRequest.Builder(ApiHost.COMBO_LIST).build();
        String url = request.getUrl();
        return getService().getUserPackageList(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取订单可购买的套餐列表
     *
     * @param orderNo
     * @return
     */
    public static Observable<ComboResponse> doOrderComboList(String orderNo) {
        RestRequest.Builder builder = new RestRequest.Builder(ApiHost.ORDER_COMBO_LIST);
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("orderNo", orderNo);
            postJson.put("category", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        RestRequest request = builder.build();
        return getService().getComoboPackageList(request.getUrl(), request.getRequestBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 使用订单
     *
     * @param orderNo
     * @param type
     * @param comboEntities
     * @param phone
     * @param name
     * @param cardNo
     * @return
     */
    public static Observable<BaseResponse> doUseComboList(String orderNo, List<ComboResponse.ComboEntity> comboEntities, int type, String name, String cardNo, String phone) {
        RestRequest.Builder builder = new RestRequest.Builder(ApiHost.USE_COMBO_LIST);
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("orderNo", orderNo);
            postJson.put("type", type);
            JSONArray jsonArray = new JSONArray();
            if (null != comboEntities && !comboEntities.isEmpty()) {
                for (ComboResponse.ComboEntity comboEntity : comboEntities) {
                    jsonArray.put(comboEntity.getPackageName());
                }
            }
            postJson.put("list", jsonArray);
            postJson.put("name", name);
            postJson.put("phone", phone);
            postJson.put("cardNo", cardNo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        RestRequest request = builder.build();
        return getService().usePackageList(request.getUrl(), request.getRequestBody())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
