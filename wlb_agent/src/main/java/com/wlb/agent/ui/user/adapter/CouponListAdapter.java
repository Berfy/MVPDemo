package com.wlb.agent.ui.user.adapter;

import android.content.Context;
import android.view.View;

import com.wlb.agent.R;
import com.wlb.agent.core.data.user.response.CouponListResponse;

import java.text.SimpleDateFormat;
import java.util.List;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * Created by JiaHongYa
 */

public class CouponListAdapter extends ListAdapter<CouponListResponse.CouponEntity> {
    private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    public CouponListAdapter(Context context, List<CouponListResponse.CouponEntity> list, int layoutId) {
        super(context, list, layoutId);
    }

    @Override
    public void setViewData(ViewHolder holder, CouponListResponse.CouponEntity data) {
        holder.setText(R.id.item_money,data.getDenomination());
        holder.setText(R.id.item_name,data.getCoupon_name());
        String endTimeStr=null;
        if(data.getEnd_time_use()>0){
             endTimeStr = dateFormat.format(data.getEnd_time_use());
        }
        holder.setText(R.id.item_date,"有效期至"+endTimeStr);
        holder.setText(R.id.item_rule,"使用规则："+data.getDesc());
        holder.setVisibility(R.id.item_selected,data.getCheck_flag()==1? View.VISIBLE:View.GONE);
    }
}
