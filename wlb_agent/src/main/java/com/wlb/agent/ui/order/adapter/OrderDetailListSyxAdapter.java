package com.wlb.agent.ui.order.adapter;

import android.content.Context;

import com.wlb.agent.R;
import com.wlb.agent.core.data.insurance.entity.Insurance;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * Created by Berfy on 2017/7/18.
 * 报价列表-商业险列表
 */
public class OrderDetailListSyxAdapter extends ListAdapter<Insurance> {

    public OrderDetailListSyxAdapter(Context context) {
        super(context, null, R.layout.adapter_order_detail_list_syx_item);
    }

    @Override
    public void setViewData(ViewHolder holder, Insurance data) {
//        if (data.isBJMPChecked()) {
//            holder.setText(R.id.tv_bao_price, "");
//            holder.setText(R.id.tv_name, mContext.getString(R.string.bjmp) + "(" + data.insuranceName + ")");
//            holder.setText(R.id.tv_price, data.exemptPrice + "");
//        } else {
        holder.setText(R.id.tv_bao_price, data.amount + "");
        holder.setText(R.id.tv_price, data.price + "");
        holder.setText(R.id.tv_name, data.insuranceName);
        if (data.insuranceName.contains("玻璃险")) {
            holder.setText(R.id.tv_bao_price, data.amount == 1 ? "国产" : "进口");
        } else if (data.insuranceName.contains("无法找到第三方")) {
            holder.setText(R.id.tv_bao_price, "");
        }
//        }
    }
}
