package com.wlb.agent.ui.common.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import com.android.util.device.DeviceUtil;
import com.wlb.agent.R;
import com.wlb.agent.util.StringUtil;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * 6位密码输入框
 *
 * @author Berfy
 */
public class Pwd6KeyBoardGridListAdapter extends ListAdapter<String> {

    private int mMargin;//边距
    private int mSpaceWidth;//横纵间隔
    private int mNumColumns;//列数
    private int mMaxLineHeight;//最大行高

    public Pwd6KeyBoardGridListAdapter(Context context, int margin, int spaceWidth, int numColumns, int maxLineHeight) {
        super(context, null, R.layout.adapter_pwd6_keyboard_list_item);
        mMargin = margin;
        mSpaceWidth = spaceWidth;
        mNumColumns = numColumns;
        mMaxLineHeight = maxLineHeight;
    }

    @Override
    public void setViewData(ViewHolder holder, String data) {
        RelativeLayout layout_grid = holder.getView(R.id.layout_grid);
        int blockWidth = (DeviceUtil.getScreenWidthPx(mContext)
                - mMargin - mSpaceWidth * (mNumColumns - 1)) / mNumColumns;
        AbsListView.LayoutParams layoutParams = (AbsListView.LayoutParams) layout_grid.getLayoutParams();
        if (null != layoutParams) {
            layoutParams.width = blockWidth;
            layoutParams.height = mMaxLineHeight == 0 ? blockWidth : (blockWidth > mMaxLineHeight ? mMaxLineHeight : blockWidth);
            layout_grid.setLayoutParams(layoutParams);
            layout_grid.setGravity(Gravity.CENTER);
        } else {
            layout_grid.setLayoutParams(new AbsListView.LayoutParams(blockWidth,
                    mMaxLineHeight == 0 ? blockWidth : (blockWidth > mMaxLineHeight ? mMaxLineHeight : blockWidth)));
            layout_grid.setGravity(Gravity.CENTER);
        }
        holder.setEnabled(R.id.tv_car_number, true);
        holder.setVisibility(R.id.tv_car_delete, View.GONE);
        holder.setVisibility(R.id.tv_car_number, View.VISIBLE);
        holder.setVisibility(R.id.iv_icon, View.VISIBLE);
        holder.setText(R.id.tv_car_number, data);
        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams)holder.getView(R.id.tv_car_delete).getLayoutParams();
        layoutParams1.width = RelativeLayout.LayoutParams.MATCH_PARENT;
        layoutParams1.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        if (StringUtil.isNumeric(data)) {
            holder.setBackgroundResource(R.id.iv_icon, R.drawable.ic_home_car_number_keyborad_number);
        } else if (data.equals("delete")) {
            holder.setText(R.id.tv_car_number, "");
            holder.setVisibility(R.id.tv_car_delete, View.VISIBLE);
            holder.setBackgroundResource(R.id.iv_icon, R.drawable.ic_home_car_number_keyborad_delete);
            layoutParams1.width = DeviceUtil.dip2px(mContext, 20);
            layoutParams1.height = DeviceUtil.dip2px(mContext, 20);
        } else if (data.equals(" ")) {
            holder.setVisibility(R.id.tv_car_number, View.GONE);
            holder.setVisibility(R.id.iv_icon, View.GONE);
        } else if (data.equals("确定")) {
            holder.setBackgroundResource(R.id.iv_icon, R.drawable.ic_home_car_number_keyborad_ok);
            holder.getConvertView().setLayoutParams(new AbsListView.LayoutParams(blockWidth * 2,
                    blockWidth));
        } else {
            holder.setBackgroundResource(R.id.iv_icon, R.drawable.ic_home_car_number_keyborad_en);
        }
    }
}
