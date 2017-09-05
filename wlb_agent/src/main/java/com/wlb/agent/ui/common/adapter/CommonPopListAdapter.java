package com.wlb.agent.ui.common.adapter;

import android.content.Context;

import com.wlb.agent.R;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * Created by Berfy on 2017/7/25.
 * 通用的弹出菜单列表
 */
public class CommonPopListAdapter extends ListAdapter<String> {

    public CommonPopListAdapter(Context context) {
        super(context, null, R.layout.view_textview);
    }

    @Override
    public void setViewData(ViewHolder holder, String data) {
        holder.setText(R.id.tv_item, data);
    }
}
