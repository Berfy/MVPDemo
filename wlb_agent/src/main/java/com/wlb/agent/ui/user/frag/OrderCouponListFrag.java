package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.util.ext.ToastUtil;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.CouponClient;
import com.wlb.agent.core.data.user.response.CouponListResponse;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.common.SimpleFragAct;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import common.widget.dialog.loading.LoadingDialog;
import rx.Subscriber;
import rx.Subscription;

/**
 * 订单优惠券
 * @author 张全
 * 修改Berfy 2017.7.28
 */

public class OrderCouponListFrag extends CouponListFrag {
    private static final String PARAM = "ORDERNO";
    private String orderNo;

    public static void start(Context context, String orderNo) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM, orderNo);
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam(R.string.user_coupon, OrderCouponListFrag.class);
        param.paramBundle = bundle;
        SimpleFragAct.start(context, param);
    }

    @Override
    protected void initParams(Bundle savedInstanceState) {
        super.initParams(savedInstanceState);
        orderNo = getArguments().getString(PARAM);
        getListView().setPullRefreshEnabled(false);
    }

    @Override
    protected Subscription executeTask(long lastId, Subscriber<BaseResponse> subscriber) {
        return CouponClient.doGetOrderCouponList(orderNo).subscribe(subscriber);
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<CouponListResponse.CouponEntity> list = getAdapter().getList();
        CouponListResponse.CouponEntity couponEntity = list.get(position);
        if (couponEntity.getCheck_flag()==1) {
           cancelCoupon(couponEntity);
        }else{
            useCoupon(couponEntity);
        }
    }

    private boolean isUseCoupon;

    private void useCoupon(final CouponListResponse.CouponEntity couponEntity) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isUseCoupon) return;
        final LoadingDialog loadingDialog = LoadingDialog.showCancelableDialog(mContext, "请稍候");
        Subscription subscription = CouponClient.doUseOrderCouponList(orderNo, couponEntity.getCoupon_no())
                .subscribe(new Subscriber<CouponListResponse>() {
                    @Override
                    public void onStart() {
                        isUseCoupon = true;
                    }

                    @Override
                    public void onCompleted() {
                        isUseCoupon = false;
                        loadingDialog.dissmiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isUseCoupon = false;
                        loadingDialog.dissmiss();
                        ToastUtil.show(R.string.req_fail);
                    }

                    @Override
                    public void onNext(CouponListResponse response) {
                        if (response.isSuccessful()) {
                            EventBus.getDefault().post(new PushEvent(Constant.IntentAction.COUPON));
                            close();
                        } else {
                            ToastUtil.show(response.msg);
                        }
                    }
                });
        addSubscription(subscription);
    }

    private boolean isCancelCoupon;
    private void cancelCoupon(final CouponListResponse.CouponEntity couponEntity) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isCancelCoupon) return;
        final LoadingDialog loadingDialog = LoadingDialog.showCancelableDialog(mContext, "请稍候");
        Subscription subscription =CouponClient.doCancelOrderCouponList(orderNo, couponEntity.getCoupon_no())
                .subscribe(new Subscriber<CouponListResponse>() {
                    @Override
                    public void onStart() {
                        isCancelCoupon = true;
                    }

                    @Override
                    public void onCompleted() {
                        isCancelCoupon = false;
                        loadingDialog.dissmiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isCancelCoupon = false;
                        loadingDialog.dissmiss();
                        ToastUtil.show(R.string.req_fail);
                    }

                    @Override
                    public void onNext(CouponListResponse response) {
                        if (response.isSuccessful()) {
//                            couponEntity.setCheck_flag(0);
//                            getAdapter().notifyDataSetChanged();
                            EventBus.getDefault().post(new PushEvent(Constant.IntentAction.COUPON));
                            close();
                        } else {
                            ToastUtil.show(response.msg);
                        }
                    }
                });
        addSubscription(subscription);
    }

}
