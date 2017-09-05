package com.wlb.agent.ui.user.adapter;

import android.content.Context;

import com.android.util.device.TimeUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.entity.WalletFlowInfo;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * Created by Berfy on 2017/7/21.
 * 待审核金额明细列表
 */
public class WalletWillCanListAdapter extends ListAdapter<WalletFlowInfo> {

    public WalletWillCanListAdapter(Context context) {
        super(context, null, R.layout.adapter_user_wallet_will_can_list_item);
    }

    @Override
    public void setViewData(ViewHolder holder, WalletFlowInfo data) {
        holder.setText(R.id.tv_order_number, data.billId + "");
        holder.setText(R.id.tv_cost, data.billType == 1 ? "+" : "-" + data.amount);
        holder.setText(R.id.tv_order_time, TimeUtil.timeFormat(data.timestamp, "yyyy-MM-dd HH:mm:ss"));
        holder.setText(R.id.tv_status, data.billDescribe);
    }
}
