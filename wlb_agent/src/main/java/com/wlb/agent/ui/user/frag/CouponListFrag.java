package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.CouponClient;
import com.wlb.agent.core.data.user.response.CouponListResponse;
import com.wlb.agent.ui.common.CommonListFrag2;
import com.wlb.agent.ui.user.adapter.UserCouponListAdapter;
import com.wlb.common.SimpleFragAct;

import java.util.List;

import common.widget.adapter.ListAdapter;
import rx.Subscriber;
import rx.Subscription;

/**
 * 优惠券列表
 * Created by JiaHongYa
 * Berfy修改 2017.7.28
 */
public class CouponListFrag extends CommonListFrag2 {
    public static SimpleFragAct.SimpleFragParam getStartParam() {
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam("我的优惠券", CouponListFrag.class);
        return param;
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected void initParams(Bundle savedInstanceState) {
        getActivity().getWindow().setBackgroundDrawable(null);
        getTitleBar().setBackgroundColor(Color.WHITE);

        setDividerWithHeight(R.drawable.transparent, 10, true);
        setBackgroundColor(getColor(R.color.c_eff3f6));
    }

    @Override
    protected Subscription executeTask(long lastId, Subscriber<BaseResponse> subscriber) {
        return CouponClient.doGetCouponList(-1,0).subscribe(subscriber);
    }

    @Override
    protected ListAdapter initAdapterWithData(List mDataList) {
        UserCouponListAdapter adapter = new UserCouponListAdapter(mContext);
        adapter.setList(mDataList);
        return adapter;
    }

    @Override
    protected ListData getDataListFromResponse(BaseResponse baseResponse) {
        CouponListResponse response = (CouponListResponse) baseResponse;
        System.out.println("----response="+response);
        List<CouponListResponse.CouponEntity> list = response.getList();
        long lastId = 0;
        if (!list.isEmpty()) {
            lastId = list.get(list.size() - 1).getCoupon_id();
        }
        return new CommonListFrag2.ListData(list, lastId);
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
