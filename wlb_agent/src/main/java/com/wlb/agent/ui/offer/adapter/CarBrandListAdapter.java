package com.wlb.agent.ui.offer.adapter;

import android.content.Context;

import com.wlb.agent.R;
import com.wlb.agent.core.data.offer.entity.CarBrand;

import common.widget.adapter.MultiListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * 车型列表
 * Created by Berfy on 2017/7/12.
 */
public class CarBrandListAdapter extends MultiListAdapter<CarBrand> {

    public CarBrandListAdapter(Context context) {
        super(context, new int[]{R.layout.adapter_car_bland_list_top_item, R.layout.adapter_car_bland_list_item});
    }

    @Override
    public void setViewData(int position, int type, ViewHolder holder, CarBrand data) {
        if (type == getPositions()[1]) {//车型列表
            if (data.isSelect()) {
                holder.setBackgroundResource(R.id.v_select, R.drawable.ic_car_bland_checked);
            } else {
                holder.setBackgroundResource(R.id.v_select, R.drawable.ic_car_bland_check);
            }
            holder.setText(R.id.tv_bland, data.getBrand());
        }
    }

}
