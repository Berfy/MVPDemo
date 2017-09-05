package com.wlb.agent.ui.order.adapter;

import android.content.Context;
import android.view.View;

import com.android.util.device.TimeUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.insurance.entity.InsuranceOrder;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.order.helper.InsuranceUtil;

import java.util.List;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * Created by Berfy on 2017/7/18.
 */

public class OrderListAdapter extends ListAdapter<InsuranceOrder> {

    private OnSubmitClickListener mOnSubmitClickListener;

    public OrderListAdapter(Context context, List<InsuranceOrder> list, OnSubmitClickListener onSubmitClickListener) {
        super(context, list, R.layout.adapter_order_list_item);
        mOnSubmitClickListener = onSubmitClickListener;
    }

    @Override
    public void setViewData(ViewHolder holder, InsuranceOrder data) {
        holder.setText(R.id.tv_order_number, data.orderNo);
        holder.setVisibility(R.id.layout_tip, View.VISIBLE);
        holder.setVisibility(R.id.v_notice, View.VISIBLE);
        holder.setVisibility(R.id.btn_submit, View.VISIBLE);
        switch (data.statusCode) {
            case 0:
                break;
            case 2://待支付
            case 4://核保成功
                holder.setText(R.id.tv_status, mContext.getString(R.string.order_status2));
                holder.setText(R.id.btn_submit, mContext.getString(R.string.order_go_pay));
                holder.setText(R.id.tv_notice, mContext.getString(R.string.order_order_status2_tip1));
                break;
            case 1://核保中
            case 3://核保失败
                if (data.statusCode == 1) {
                    holder.setText(R.id.tv_status, mContext.getString(R.string.order_status1));
                } else if (data.statusCode == 3) {
                    holder.setText(R.id.tv_status, mContext.getString(R.string.order_status0));
                }
                if (data.uploadIDFlag()) {// 上传照片 显示上传照片
                    holder.setText(R.id.btn_submit, mContext.getString(R.string.order_go_upload));
                    holder.setText(R.id.tv_notice, mContext.getString(R.string.order_order_status0_tip));
                } else if (data.isUnderwritingFail()) {// 3 核保失败  底部显示从新核保按钮
                    holder.setText(R.id.tv_notice, "");
                    holder.setVisibility(R.id.btn_submit, View.GONE);
                    holder.setText(R.id.tv_notice, data.statusMsg);
                } else {//核保中
                    holder.setVisibility(R.id.btn_submit, View.GONE);
                    holder.setText(R.id.tv_notice, mContext.getString(R.string.order_order_status1_tip1) + mContext.getString(R.string.about_phone));
                }
                break;
            case 5://核保成功
                holder.setText(R.id.tv_status, mContext.getString(R.string.order_status4));
                holder.setVisibility(R.id.v_notice, View.GONE);
                holder.setVisibility(R.id.btn_submit, View.GONE);
                holder.setVisibility(R.id.layout_tip, View.GONE);
                break;
            case 7://支付成功 出单中
                holder.setText(R.id.tv_status, mContext.getString(R.string.order_status3));
                holder.setText(R.id.btn_submit, mContext.getString(R.string.order_go_submit));
                holder.setText(R.id.tv_notice, mContext.getString(R.string.order_sendcode_tip));
                break;
            case 6://支付过期
                holder.setText(R.id.tv_status, mContext.getString(R.string.order_status6));
                holder.setText(R.id.tv_notice, data.statusMsg);
                holder.setVisibility(R.id.btn_submit, View.GONE);
                break;
        }
        //保险公司icon
        InsuranceUtil.setImageOption(data.companyCode, holder.getView(R.id.iv_icon));

        holder.setText(R.id.tv_carnum, data.licenseNo);
        holder.setText(R.id.tv_name, data.owner);
        holder.setText(R.id.tv_tuiguangfei, data.totalCommission + "");
        holder.setText(R.id.tv_price_total, data.totalPremium + "");
        holder.setText(R.id.tv_order_time, TimeUtil.timeFormat(data.timestamp, "yyyy-MM-dd HH:mm:ss"));
        holder.setVisibility(R.id.layout_tuiguangfei, UserClient.getTuifuangfei() ? View.VISIBLE : View.GONE);

        holder.getView(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnSubmitClickListener)
                    mOnSubmitClickListener.submit(data);
            }
        });
    }

    public interface OnSubmitClickListener {
        void submit(InsuranceOrder order);
    }
}
