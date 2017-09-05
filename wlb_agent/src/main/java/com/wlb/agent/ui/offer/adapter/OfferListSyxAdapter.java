package com.wlb.agent.ui.offer.adapter;

import android.content.Context;

import com.wlb.agent.R;
import com.wlb.agent.core.data.offer.entity.InsurerConverage;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * Created by Berfy on 2017/7/18.
 * 报价列表-商业险列表
 */
public class OfferListSyxAdapter extends ListAdapter<InsurerConverage> {

    public OfferListSyxAdapter(Context context) {
        super(context, null, R.layout.adapter_offer_list_syx_item);
    }

    @Override
    public void setViewData(ViewHolder holder, InsurerConverage data) {
        boolean isZhuXian = false;
        if (data.getName().contains("不计免赔")) {
            isZhuXian = false;
            holder.setText(R.id.tv_type, mContext.getString(R.string.fujiaxian));
        } else {
            isZhuXian = true;
            holder.setText(R.id.tv_type, mContext.getString(R.string.zhuxian));
        }
        holder.setBackgroundResource(R.id.tv_type, isZhuXian ? R.color.common_blue : R.color.common_green);
        holder.setText(R.id.tv_name, data.getName());
        holder.setText(R.id.tv_bao_price, data.getToubao_price());
        holder.setText(R.id.tv_price, data.getPrice());
    }
}
