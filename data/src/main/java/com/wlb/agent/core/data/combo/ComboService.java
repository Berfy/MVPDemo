package com.wlb.agent.core.data.combo;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.combo.response.ComboResponse;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

/**
 * @author 张全
 * 我的套餐接口
 */

public interface ComboService {

    /**
     * 获取套餐列表
     * @param url
     * @param requestBody
     * @return
     */
    @POST
    Observable<ComboResponse> getComoboPackageList(@Url String url, @Body RequestBody requestBody);

    /**
     * 获取我的套餐列表
     * @param url
     * @return
     */
    @GET
    Observable<ComboResponse> getUserPackageList(@Url String url);

    /**
     * 选择商品套餐
     * @param url
     * @param requestBody
     * @return
     */
    @POST
    Observable<BaseResponse> usePackageList(@Url String url, @Body RequestBody requestBody);

}
