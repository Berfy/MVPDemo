package com.wlb.agent.ui.user.adapter;

import android.content.Context;
import android.widget.RelativeLayout;

import com.android.util.device.DeviceUtil;
import com.android.util.device.TimeUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.response.CouponListResponse;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

import static com.wlb.agent.R.id.tv_timeto;

/**
 * Created by Berfy on 2017/7/24.
 * 优惠券列表
 */
public class UserCouponListAdapter extends ListAdapter<CouponListResponse.CouponEntity> {

    public UserCouponListAdapter(Context context) {
        super(context, null, R.layout.adapter_user_coupon_list_item);
    }

    @Override
    public void setViewData(ViewHolder holder, CouponListResponse.CouponEntity data) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.getView(R.id.layout_bg).getLayoutParams();
        layoutParams.width = DeviceUtil.getScreenWidthPx(mContext) - DeviceUtil.dip2px(mContext, 40);
        layoutParams.height = (int) (layoutParams.width / 3.8);
        holder.getView(R.id.layout_bg).setLayoutParams(layoutParams);
        holder.setText(R.id.tv_name, data.getCoupon_name());
        holder.setText(R.id.tv_timeto, TimeUtil.timeFormat(data.getEnd_time_use(), "yyyy-MM-dd"));
        holder.setText(R.id.tv_rule, data.getDesc());

        switch (data.getStatusX()) {
            case 0://正常
                if (data.getCoupon_name().equals("车险现金券")) {
                    holder.setBackgroundResource(R.id.layout_bg, R.drawable.ic_coupon_bg_red);
                } else {
                    holder.setBackgroundResource(R.id.layout_bg, R.drawable.ic_coupon_bg_blue);
                }
                break;
            case 1://已使用
                holder.setBackgroundResource(R.id.layout_bg, R.drawable.ic_coupon_bg_used);
                break;
            case 2://过期
                holder.setBackgroundResource(R.id.layout_bg, R.drawable.ic_coupon_bg_timeout);
                break;
        }


        holder.setText(R.id.tv_name, data.getCoupon_name());
        holder.setText(tv_timeto, TimeUtil.timeFormat(data.getEnd_time_use(), "yyyy-MM-dd"));
        holder.setText(R.id.tv_price, data.getDenomination());
    }
}
