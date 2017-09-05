package com.wlb.agent.ui.user.frag;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.CouponClient;
import com.wlb.agent.core.data.user.response.CouponListResponse;
import com.wlb.agent.ui.common.CommonListFrag2;
import com.wlb.agent.ui.user.adapter.UserCouponListAdapter;

import java.util.List;

import common.widget.adapter.ListAdapter;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Berfy on 2017/7/24.
 * 我的优惠券
 */
public class UserCouponListFrag extends CommonListFrag2 {

    public static final String PARAM_STATUS = "status";
    public static final String PARAM_ORDER_PRICE = "orderPrice";//订单金额，判断是否满足条件
    private int mStatus;//0正常 1已使用 2已过期

    @Override
    protected void initParams(Bundle savedInstanceState) {
        if (null != getArguments()) {
            mStatus = getArguments().getInt(PARAM_STATUS);
        }
    }

    @Override
    protected Subscription executeTask(long lastId, Subscriber<BaseResponse> subscriber) {
        return CouponClient.doGetCouponList(mStatus, lastId).subscribe(subscriber);
    }

//    @Override
//    protected Task executeTask(long lastId, ICallback callback) {
//        CouponListResponse response = new CouponListResponse();
//        List<CouponListResponse.CouponEntity> couponEntities = new ArrayList<>();
//        for (int i = (int) lastId; i < lastId + 10; i++) {
//            CouponListResponse.CouponEntity couponEntity = new CouponListResponse.CouponEntity();
//            couponEntity.setCoupon_id(i);
//            couponEntity.setCoupon_name("哈哈哈" + i);
//            couponEntity.setDenomination("12" + i);
//            couponEntity.setEnd_time_use(System.currentTimeMillis());
//            couponEntity.setStatusX(0);
//            couponEntities.add(couponEntity);
//        }
//        response.setList(couponEntities);
//        callback.success(response);
//        callback.end();
//        return new Task(null, null);
//    }

    @Override
    protected ListAdapter initAdapterWithData(List mDataList) {
        UserCouponListAdapter adapter = new UserCouponListAdapter(mContext);
        adapter.refresh(mDataList);
        return adapter;
    }

    @Override
    protected ListData getDataListFromResponse(BaseResponse baseResponse) {
        CouponListResponse response = (CouponListResponse) baseResponse;
        System.out.println("----response=" + response);
        List<CouponListResponse.CouponEntity> list = response.getList();
        long lastId = 0;
        if (!list.isEmpty()) {
            lastId = list.get(list.size() - 1).getCoupon_id();
        }
        return new CommonListFrag2.ListData(list, lastId);
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CouponListResponse.CouponEntity couponEntity = (CouponListResponse.CouponEntity) getAdapter().getList().get(position);
        if (couponEntity.getStatusX() == 0) {

        }
    }
}
