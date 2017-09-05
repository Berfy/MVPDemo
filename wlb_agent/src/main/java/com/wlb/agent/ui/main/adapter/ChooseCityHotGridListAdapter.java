package com.wlb.agent.ui.main.adapter;

import android.content.Context;
import android.widget.AbsListView;

import com.android.util.device.DeviceUtil;
import com.android.util.log.LogUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.common.CityBean;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * 选择城市弹出框-热门城市
 *
 * @author Berfy
 */
public class ChooseCityHotGridListAdapter extends ListAdapter<CityBean> {

    public ChooseCityHotGridListAdapter(Context context) {
        super(context, null, R.layout.adapter_choose_city_search_hot_list_item);
    }

    @Override
    public void setViewData(ViewHolder holder, CityBean data) {
        int blockWidth = (DeviceUtil.getScreenWidthPx(mContext)
                - DeviceUtil.dip2px(mContext, 20) - DeviceUtil.dip2px(mContext, 5) * (4 - 1)) / 4;
        LogUtil.e("键盘块宽高", blockWidth + "    公式(" + (DeviceUtil.getScreenWidthPx(mContext) + "-" + DeviceUtil.dip2px(mContext, 20)
                + "-" + DeviceUtil.dip2px(mContext, 10) * 10 + ") / 4"));
        holder.getConvertView().setLayoutParams(new AbsListView.LayoutParams(blockWidth, (int) (blockWidth / 2.5)));
        holder.setText(R.id.tv_city, data.getName());
    }
}
