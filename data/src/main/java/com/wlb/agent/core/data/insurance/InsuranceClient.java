package com.wlb.agent.core.data.insurance;

import com.android.util.http.NetClient;
import com.android.util.http.callback.ICallback;
import com.android.util.http.response.GsonParser;
import com.android.util.http.task.Task;
import com.wlb.agent.core.data.ApiHost;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.base.RequestBuilder;
import com.wlb.agent.core.data.base.RestClient;
import com.wlb.agent.core.data.base.RestRequest;
import com.wlb.agent.core.data.base.SimpleJsonParser;
import com.wlb.agent.core.data.insurance.api.InsuranceService;
import com.wlb.agent.core.data.insurance.response.DrivingOrderResponse;
import com.wlb.agent.core.data.insurance.response.InsuranceCancelResponse;
import com.wlb.agent.core.data.insurance.response.InsuranceOrderDetail;
import com.wlb.agent.core.data.insurance.response.InsuranceOrderResponse;
import com.wlb.agent.core.data.insurance.response.PrepayResponse;
import com.wlb.agent.core.data.insurance.response.TeamOrderListResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 车险接口
 *
 * @author 张全
 */
public class InsuranceClient {

    /**
     * 保单搜索
     */
    public static Task searchOrder(long lastId, String keyword, ICallback callback) {
        RequestBuilder builder = RequestBuilder.getBuilder(ApiHost.INSURANCE_SEEK,
                callback, new GsonParser<>(InsuranceOrderResponse.class));
        builder.addParam("lastId", lastId);
        builder.addParam("pageCount", 10);
        builder.addParam("keyword", keyword);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 订单预支付
     *
     * @param platform 支付平台 0线下支付、1微信、2支付宝
     * @param callback
     * @return
     */
    public static Task doPrepay(int platform, long policyId, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.INSURANCE_PREPAY,
                callback, new GsonParser<>(PrepayResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("channel", platform);
            postJson.put("policyId", policyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 获取车险订单列表
     *
     * @param callback
     * @param jsonArray //1待核保；2核保通过；3核保失败；4待支付；5支付过期；6支付完成出单;7支付完成未出单
     */
    public static Task doGetOrders(long lastId, JSONArray jsonArray, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.INSURANCE_ORDER_LIST,
                callback, new GsonParser<>(InsuranceOrderResponse.class));

        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("lastId", lastId);
            postJson.put("pageCount", 10);
            postJson.put("statusCode", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 获取代驾订单列表
     *
     * @param status
     * @param lastId
     * @return
     */
    public static Observable<DrivingOrderResponse> doGetDrivingList(int status, long lastId) {
        RestRequest.Builder builder = new RestRequest.Builder(ApiHost.ORDER_DRIVING_LIST)
                .addParam("orderStatus", status)
                .addParam("lastId", lastId)
                .addParam("pageCount", 10);

        String url = builder.build().getUrl();
        InsuranceService insuranceService = RestClient.getService(InsuranceService.class);
        return insuranceService.getDrivingOrders(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取车险订单详情
     *
     * @param callback
     * @return
     */
    public static Task doGetOrderDetail(String policyId,
                                        ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(
                ApiHost.INSURANCE_ORDER_DETAIL, callback, new GsonParser<>(InsuranceOrderDetail.class));

        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("policyId", policyId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);

        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 取消保单
     */
    public static Task doInsuranceCancel(long policyId, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.INSURANCE_CANCEL, callback, new GsonParser<>(InsuranceCancelResponse.class));
        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("policyId", policyId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);

        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 订单状态变更提醒
     *
     * @param
     * @return
     */
    public static Task doGetInsuranceStatus(ICallback callback) {
        RequestBuilder builder = RequestBuilder.getBuilder(ApiHost.INSURANCE_STATUS_REMINDER, callback, new GsonParser<>(TeamOrderListResponse.class));
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 订单状态变更提醒已读
     */
    public static Task doInsuranceStatusRead(JSONArray status_read, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.INSURANCE_STATUS_READ, callback, new SimpleJsonParser());

        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("status_read", status_read);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 订单核保失败上传照片
     *
     * @param type 1车主身份证 2 投保人 3被保人
     */
    public static Task uploadPhoto(String policyId, List<String> imgs, int type, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.ORDER_UPLOAD_PHOTO, callback, new SimpleJsonParser());

        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("policyId", policyId);
            JSONArray jsonArray = new JSONArray();
            for (String img : imgs) {
                jsonArray.put(img);
            }
            postJson.put("imgs", jsonArray);
            postJson.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 订单核保失败上传验车照片
     */
    public static Task uploadVehicleCheckPhoto(String policyId, List<String> imgs, ICallback callback) {
        RequestBuilder builder = RequestBuilder.postBuilder(ApiHost.ORDER_VEHICLECHECK_PHOTO_PHONE, callback, new SimpleJsonParser());

        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("policyId", policyId);
            JSONArray jsonArray = new JSONArray();
            for (String img : imgs) {
                jsonArray.put(img);
            }
            postJson.put("img_url", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        return NetClient.getInstance().execute(builder.build());
    }

    /**
     * 订单保协验证码输入
     *
     * @param policyId
     * @param verifyCode
     * @return
     */
    public static Observable<BaseResponse> doVerifyCode(long policyId, String verifyCode) {
        RestRequest.Builder builder = new RestRequest.Builder(ApiHost.ORDER_VERIFY_CODE);

        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("policyId", policyId);
            postJson.put("verifyCode", verifyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        RestRequest request = builder.build();
        String url = request.getUrl();
        RequestBody body = request.getRequestBody();

        InsuranceService insuranceService = RestClient.getService(InsuranceService.class);
        return insuranceService.verifyCode(url, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 订单保协验证码手机号修改
     *
     * @param policyId
     * @param phoneNo
     * @return
     */
    public static Observable<BaseResponse> doModifyVerifyPhone(long policyId, String phoneNo, String verifyCode) {
        RestRequest.Builder builder = new RestRequest.Builder(ApiHost.ORDER_MODIFY_PHONE);

        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("policyId", policyId);
            postJson.put("phoneNo", phoneNo);
            postJson.put("verifyCode", verifyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        RestRequest request = builder.build();
        String url = request.getUrl();
        RequestBody body = request.getRequestBody();

        InsuranceService insuranceService = RestClient.getService(InsuranceService.class);
        return insuranceService.modifyVerifyPhone(url, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
