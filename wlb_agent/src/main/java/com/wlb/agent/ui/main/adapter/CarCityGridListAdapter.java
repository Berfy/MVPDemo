package com.wlb.agent.ui.main.adapter;

import android.content.Context;
import android.widget.AbsListView;

import com.android.util.device.DeviceUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.common.CarCityBean;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * 选择车牌城市键盘
 *
 * @author Berfy
 */
public class CarCityGridListAdapter extends ListAdapter<CarCityBean> {

    public CarCityGridListAdapter(Context context) {
        super(context, null, R.layout.adapter_car_city_list_item);
    }

    @Override
    public void setViewData(ViewHolder holder, CarCityBean data) {
        int blockWidth = (DeviceUtil.getScreenWidthPx(mContext)
                - DeviceUtil.dip2px(mContext, 20) - DeviceUtil.dip2px(mContext, 5) * (8 - 1)) / 8;
        holder.getConvertView().setLayoutParams(new AbsListView.LayoutParams(blockWidth, blockWidth));
        holder.setText(R.id.tv_car_city, data.getCarNum());
    }
}
