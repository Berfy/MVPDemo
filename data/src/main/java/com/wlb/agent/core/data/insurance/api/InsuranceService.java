package com.wlb.agent.core.data.insurance.api;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.insurance.response.DrivingOrderResponse;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author 张全
 * 订单
 */

public interface InsuranceService {

    @GET
    Observable<DrivingOrderResponse> getDrivingOrders(@Url String url);

    @POST
    Observable<BaseResponse> verifyCode(@Url String url, @Body RequestBody body);

    @POST
    Observable<BaseResponse> modifyVerifyPhone(@Url String url,@Body RequestBody body);
}
