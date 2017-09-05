package com.wlb.agent.ui.user.adapter;

import android.content.Context;
import android.view.View;

import com.wlb.agent.R;
import com.wlb.agent.core.data.user.entity.WalletFlowInfo;
import com.wlb.agent.ui.user.frag.WalletFlowFrag;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * 钱包流水adapter
 * <p/>
 * Created by 曹泽琛.
 */
public class WalletFlowListAdapter extends ListAdapter<WalletFlowFrag.WalletFlowWrapper> {

    //    private SimpleDateFormat totFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    private SimpleDateFormat detailFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());

    public WalletFlowListAdapter(Context context, List<WalletFlowFrag.WalletFlowWrapper> list, int layoutId) {
        super(context, list, layoutId);
    }

    @Override
    public void setViewData(ViewHolder holder, WalletFlowFrag.WalletFlowWrapper data) {
        if (data.showYearMonth) {
            holder.setVisibility(R.id.item_ym, View.VISIBLE);
            holder.setText(R.id.item_ym, data.yearMonth);
        } else {
            holder.setVisibility(R.id.item_ym, View.GONE);
        }

        WalletFlowInfo flowInfo = data.flowInfo;
        holder.setText(R.id.flow_time, detailFormat.format(flowInfo.timestamp));
        holder.setText(R.id.flow_name, flowInfo.billDescribe);
        holder.getView(R.id.flow_money).setVisibility(View.GONE);
        holder.getView(R.id.flow_money_add).setVisibility(View.GONE);
        if (1 == flowInfo.billType) {
            holder.getView(R.id.flow_money_add).setVisibility(View.VISIBLE);
            holder.setText(R.id.flow_money_add, "+ " + flowInfo.amount);
        } else if (2 == flowInfo.billType) {
            holder.getView(R.id.flow_money).setVisibility(View.VISIBLE);
            holder.setText(R.id.flow_money, "- " + flowInfo.amount);
        }
    }
}
