package com.wlb.agent.ui.user.adapter;

import android.content.Context;

import com.wlb.agent.R;
import com.wlb.agent.core.data.user.entity.Score;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * Created by Berfy on 2017/7/24.
 * 积分列表
 */
public class UserScoreListAdapter extends ListAdapter<Score> {

    public UserScoreListAdapter(Context context) {
        super(context, null, R.layout.adapter_user_score_list_item);
    }

    @Override
    public void setViewData(ViewHolder holder, Score data) {
        holder.setText(R.id.tv_title, data.getTitle());
        holder.setText(R.id.tv_time, data.getTime());
        holder.setText(R.id.tv_score, (data.getType() == 0 ? "-" : "+") + data.getScore());
    }
}
