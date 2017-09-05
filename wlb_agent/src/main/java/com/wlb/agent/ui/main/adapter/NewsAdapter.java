package com.wlb.agent.ui.main.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.wlb.agent.R;
import com.wlb.agent.core.data.agentservice.entity.NewsInfo;
import com.wlb.common.imgloader.ImageFactory;

import java.util.List;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

public class NewsAdapter extends ListAdapter<NewsInfo> {
    private int DEF_ICON = R.drawable.takeaward_def_photos;
    private DisplayImageOptions options;

    public NewsAdapter(Context context, List<NewsInfo> list, int layoutId) {
        super(context, list, layoutId);
        options = ImageFactory.getImageOptions(DEF_ICON);
    }

    @Override
    public void setViewData(ViewHolder holder, NewsInfo data) {

        holder.setText(R.id.item_title, data.title);
        ImageView imageView = holder.getView(R.id.item_img);
        ImageFactory.getUniversalImpl().getImg(data.icon, imageView,
                null, options);
    }
}
