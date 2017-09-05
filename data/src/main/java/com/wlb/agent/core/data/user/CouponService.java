package com.wlb.agent.core.data.user;

import com.wlb.agent.core.data.user.response.CouponListResponse;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author 张全
 * 我的优惠券接口
 */

public interface CouponService {

    @GET
    Observable<CouponListResponse> getCouponList(@Url String url);

    @POST
    Observable<CouponListResponse> useOrderCouponList(@Url String url, @Body RequestBody body);
    @POST
    Observable<CouponListResponse> cancelOrderCouponList(@Url String url, @Body RequestBody body);
}
