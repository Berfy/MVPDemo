package com.wlb.agent.ui.offer.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.wlb.agent.R;
import com.wlb.agent.core.data.offer.entity.InsurerConverage;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

import static android.view.View.GONE;

/**
 * 选择保险公司列表
 * Created by Berfy on 2017/7/12.
 */
public class ChooseInsurerConverageListAdapter extends ListAdapter<InsurerConverage> {

    public ChooseInsurerConverageListAdapter(Context context) {
        super(context, null, R.layout.adapter_choose_insurer_converage_list_item);
    }

    @Override
    public void setViewData(ViewHolder holder, InsurerConverage data) {
        holder.setVisibility(R.id.btn_bjmp, data.isShowBuJiMianPei() ? View.VISIBLE : GONE);
        holder.setBackgroundResource(R.id.btn_bjmp, data.isBuJiMianPeiSelect() ? R.drawable.ic_bujimianpei_select : R.drawable.ic_bujimianpei);
        holder.setBackgroundResource(R.id.btn_select, data.isSelect() ? R.drawable.ic_converage_press : R.drawable.ic_converage);

        holder.getView(R.id.btn_bjmp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.setBuJiMianPeiSelect(!data.isBuJiMianPeiSelect());
                notifyDataSetChanged();
            }
        });
        holder.getView(R.id.btn_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.setSelect(!data.isSelect());
                notifyDataSetChanged();
            }
        });
        holder.setVisibility(R.id.btn_select, GONE);
        if (data.getName().indexOf("交强险") != -1) {
            holder.setVisibility(R.id.btn_select, View.VISIBLE);
        } else if (data.getName().indexOf("机动车辆损失险") != -1) {
            holder.setVisibility(R.id.btn_select, View.VISIBLE);
        } else if (data.getName().indexOf("盗抢险") != -1) {
            holder.setVisibility(R.id.btn_select, View.VISIBLE);
        } else if (data.getName().indexOf("第三方特约险") != -1) {
            holder.setVisibility(R.id.btn_select, View.VISIBLE);
        } else if (data.getName().indexOf("自燃险") != -1) {
            holder.setVisibility(R.id.btn_select, View.VISIBLE);
        } else if (data.getName().indexOf("涉水险") != -1) {
            holder.setVisibility(R.id.btn_select, View.VISIBLE);
        } else if (data.getName().indexOf("指定修理厂险") != -1) {
            holder.setVisibility(R.id.btn_select, View.VISIBLE);
        } else if (data.getName().indexOf("交强险") != -1) {
            holder.setVisibility(R.id.btn_select, View.VISIBLE);
        }
        holder.setVisibility(R.id.layout_price, View.GONE);
        if (holder.getView(R.id.btn_select).getVisibility() == View.GONE) {
            holder.setVisibility(R.id.layout_price, View.VISIBLE);
            if (TextUtils.isEmpty(data.getPrice())) {
                holder.setText(R.id.tv_price, "不投保");
            } else {
                holder.setText(R.id.tv_price, data.getPrice());
            }
        }
        holder.setText(R.id.tv_name, data.getName());
    }

}
