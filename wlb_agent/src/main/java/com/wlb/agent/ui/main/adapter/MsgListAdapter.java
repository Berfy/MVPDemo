package com.wlb.agent.ui.main.adapter;

import android.content.Context;
import android.view.View;

import com.wlb.agent.R;
import com.wlb.agent.core.data.agentservice.entity.MessageInfo;

import java.text.SimpleDateFormat;
import java.util.List;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * 消息列表Adapter
 *
 * @author 张全
 */
public class MsgListAdapter extends ListAdapter<MessageInfo> {
    private SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");

    public MsgListAdapter(Context context, List<MessageInfo> list, int layoutId) {
        super(context, list, layoutId);
    }

    @Override
    public void setViewData(ViewHolder holder, MessageInfo data) {
        if (!data.isReaded() && (data.msgType == 1 || data.msgType == 2)) {
            holder.setVisibility(R.id.item_readTag, View.VISIBLE);
            holder.setVisibility(R.id.item_unread_txt, View.VISIBLE);
        } else {
            holder.setVisibility(R.id.item_readTag, View.INVISIBLE);
            holder.setVisibility(R.id.item_unread_txt, View.GONE);
        }
        holder.setText(R.id.item_title, data.title);
        holder.setText(R.id.item_summary, data.summary);
        holder.setText(R.id.item_time, dateFormat.format(data.timestamp));

    }
}
