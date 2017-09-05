package com.wlb.agent.ui.user.adapter;

import android.content.Context;
import android.view.View;

import com.android.util.device.TimeUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.agentservice.entity.MessageInfo;

import java.util.List;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * 消息列表Adapter
 *
 * @author Berfy
 */
public class UserNotifyListAdapter extends ListAdapter<MessageInfo> {

    public UserNotifyListAdapter(Context context, List<MessageInfo> list, int layoutId) {
        super(context, list, layoutId);
    }

    @Override
    public void setViewData(ViewHolder holder, MessageInfo data) {
        holder.setText(R.id.tv_title, data.title);
        holder.setText(R.id.tv_time, TimeUtil.timeFormat(data.timestamp, "yyyy-MM-dd"));
//        holder.setVisibility(R.id.layout_detail, View.VISIBLE);
        if (data.msgType == 3) {//公告
//            holder.setVisibility(R.id.layout_detail, View.GONE);
            holder.setVisibility(R.id.tv_info, View.VISIBLE);
            holder.setText(R.id.tv_info, data.summary);
        } else if (data.msgType == 1 || data.msgType == 2) {
            holder.setVisibility(R.id.tv_info, View.GONE);
        }

    }
}
