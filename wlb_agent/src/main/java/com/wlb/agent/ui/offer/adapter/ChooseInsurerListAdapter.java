package com.wlb.agent.ui.offer.adapter;

import android.content.Context;

import com.wlb.agent.R;
import com.wlb.agent.core.data.offer.entity.Insurer;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * 选择保险公司列表
 * Created by Berfy on 2017/7/12.
 */
public class ChooseInsurerListAdapter extends ListAdapter<Insurer> {

    public ChooseInsurerListAdapter(Context context) {
        super(context, null, R.layout.adapter_choose_insurer_list_item);
    }

    @Override
    public void setViewData(ViewHolder holder, Insurer data) {
        if (data.isSelect()) {
            holder.setBackgroundResource(R.id.v_select, R.drawable.ic_check_choose_insurer_checked);
        } else {
            holder.setBackgroundResource(R.id.v_select, R.drawable.ic_check_choose_insurer);
        }
        if (data.getName().equals("1")) {
            holder.setBackgroundResource(R.id.v_icon, R.drawable.ic_pingan);
        } else if (data.getName().equals("2")) {
            holder.setBackgroundResource(R.id.v_icon, R.drawable.ic_renbao);
        } else if (data.getName().equals("3")) {
            holder.setBackgroundResource(R.id.v_icon, R.drawable.ic_taipingyang);
        }
    }

}
