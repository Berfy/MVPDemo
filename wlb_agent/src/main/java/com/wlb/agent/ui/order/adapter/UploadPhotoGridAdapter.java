package com.wlb.agent.ui.order.adapter;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.android.util.device.DeviceUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wlb.agent.R;
import com.wlb.common.imgloader.ImageLoaderImpl;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * Created by Berfy on 2017/7/19.
 * 上传照片
 */

public class UploadPhotoGridAdapter extends ListAdapter<String> {

    public UploadPhotoGridAdapter(Context context) {
        super(context, null, R.layout.adapter_order_upload_photolist_item);
    }

    @Override
    public int getCount() {
        return getList().size() + 1;
    }

    @Override
    public void setViewData(ViewHolder holder, String data) {
        int blockWidth = (DeviceUtil.getScreenWidthPx(mContext)
                - DeviceUtil.dip2px(mContext, 20) - DeviceUtil.dip2px(mContext, 10) * (3 - 1)) / 3;
        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) holder.getConvertView().getLayoutParams();
        if (null != layoutParams) {
            layoutParams.width = blockWidth;
            layoutParams.height = blockWidth;
            holder.getConvertView().setLayoutParams(layoutParams);
        } else {
            holder.getConvertView().setLayoutParams(new AbsListView.LayoutParams(blockWidth, blockWidth));
        }
        if (holder.getPos() == getCount() - 1) {
            holder.getView(R.id.iv_icon).setBackgroundResource(R.drawable.ic_order_upload_photo);
        } else {
            ImageLoader.getInstance().displayImage(data, (ImageView) holder.getView(R.id.iv_icon), ImageLoaderImpl.cacheDiskOptions);
        }
    }
}
