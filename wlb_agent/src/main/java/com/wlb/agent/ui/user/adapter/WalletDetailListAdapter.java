package com.wlb.agent.ui.user.adapter;

import android.content.Context;

import com.android.util.device.TimeUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.entity.WalletFlowInfo;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * Created by Berfy on 2017/7/21.
 * 收支明细列表
 */
public class WalletDetailListAdapter extends ListAdapter<WalletFlowInfo> {

    public WalletDetailListAdapter(Context context) {
        super(context, null, R.layout.adapter_user_wallet_detail_list_item);
    }

    @Override
    public void setViewData(ViewHolder holder, WalletFlowInfo data) {
        holder.setText(R.id.tv_title, data.billDescribe);
        holder.setText(R.id.tv_time, TimeUtil.timeFormat(data.timestamp, "yyyy-MM-dd HH:mm:ss"));
        String status = "";
        switch (data.category) {
            case 1:
                status = mContext.getString(R.string.wallet_detail_status1);
                break;
            case 2:
                status = mContext.getString(R.string.wallet_detail_status2);
                break;
        }
        holder.setText(R.id.tv_status, status);
        switch (data.billType) {
            case 1:
                holder.setTextColor(R.id.tv_price, R.color.common_blue);
                holder.setText(R.id.tv_price, "+" + data.amount);
                break;
            case 2:
                holder.setTextColor(R.id.tv_price, R.color.common_red);
                holder.setText(R.id.tv_price, "-" + data.amount);
                break;
        }
        holder.setText(R.id.tv_price, (data.billType == 1 ? "+" : "-") + data.amount);

    }
}
