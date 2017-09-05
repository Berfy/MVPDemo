package com.wlb.agent.ui.find.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wlb.agent.R;
import com.wlb.agent.core.data.agentservice.entity.NewsInfo;
import com.wlb.common.imgloader.ImageLoaderImpl;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * Created by Berfy on 2017/7/25.
 * 发现列表
 */
public class FindListAdapter extends ListAdapter<NewsInfo> {

    public FindListAdapter(Context context) {
        super(context, null, R.layout.adapter_find_list_item);
    }

    @Override
    public void setViewData(ViewHolder holder, NewsInfo data) {
        holder.setText(R.id.tv_content, data.title);
        holder.setText(R.id.tv_read_num, "");
        holder.setText(R.id.tv_zan_num, "");
        ImageLoader.getInstance().displayImage(data.icon, (ImageView) holder.getView(R.id.iv_icon), ImageLoaderImpl.cacheDiskOptions);
    }
}
