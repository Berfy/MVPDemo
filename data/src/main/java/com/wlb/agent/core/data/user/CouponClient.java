package com.wlb.agent.core.data.user;

import com.wlb.agent.core.data.ApiHost;
import com.wlb.agent.core.data.base.RestClient;
import com.wlb.agent.core.data.base.RestRequest;
import com.wlb.agent.core.data.user.response.CouponListResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author 张全
 * 我的优惠券接口
 */

public class CouponClient {
    /**
     * 我的优惠券
     * @param status 优惠券状态0-正常 1-已使用 2-已过期 不传参数为全部优惠券
     * @param lastId 分页上一页最后一条记录编号，第一页为0
     * @return
     */
    public static Observable<CouponListResponse> doGetCouponList(int status, long lastId){
        RestRequest.Builder builder = new RestRequest.Builder(ApiHost.COUPON_LIST)
                .addParam("status", status)
                .addParam("last_id", lastId)
                .addParam("page_count", 10);

        String url=builder.build().getUrl();
        CouponService couponApi = RestClient.getService(CouponService.class);
        return  couponApi.getCouponList(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 当前订单可用优惠券列表
     * @param orderNo
     * @return
     */
    public static Observable<CouponListResponse> doGetOrderCouponList(String orderNo){
        RestRequest.Builder builder = new RestRequest.Builder(ApiHost.ORDER_COUPON_LIST)
                .addParam("orderNo", orderNo);
        RestRequest request = builder.build();

        CouponService couponApi = RestClient.getService(CouponService.class);
        return  couponApi.getCouponList(request.getUrl())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 订单使用优惠券
     * @param orderNo
     * @param coupon_no
     * @return
     */
    public static Observable<CouponListResponse> doUseOrderCouponList(String orderNo,String coupon_no){
        RestRequest.Builder builder =new RestRequest.Builder(ApiHost.ORDER_USE_COUPON_LIST);

        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("orderNo", orderNo);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(coupon_no);
            postJson.put("coupon_no", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        RestRequest request = builder.build();
        String url = request.getUrl();
        RequestBody body = request.getRequestBody();

        CouponService couponApi = RestClient.getService(CouponService.class);
        return  couponApi.useOrderCouponList(url,body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 取消订单优惠券列表
     * @param orderNo
     * @param coupon_no
     * @return
     */
    public static Observable<CouponListResponse> doCancelOrderCouponList(String orderNo,String coupon_no){
        RestRequest.Builder builder =new RestRequest.Builder(ApiHost.ORDER_CANCEL_COUPON_LIST);

        JSONObject postJson = builder.getPostJson();
        try {
            postJson.put("orderNo", orderNo);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(coupon_no);
            postJson.put("coupon_no", jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        builder.setPostJsonData(postJson);
        RestRequest request = builder.build();
        String url = request.getUrl();
        RequestBody body = request.getRequestBody();

        CouponService couponApi = RestClient.getService(CouponService.class);
        return  couponApi.cancelOrderCouponList(url,body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
