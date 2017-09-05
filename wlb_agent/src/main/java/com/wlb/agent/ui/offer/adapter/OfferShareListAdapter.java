package com.wlb.agent.ui.offer.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.android.util.ext.SoftInputUtil;
import com.android.util.ext.ToastUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.offer.entity.OfferShare;
import com.wlb.agent.util.StringUtil;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * 报价
 * Created by Berfy on 2017/7/12.
 */
public class OfferShareListAdapter extends ListAdapter<OfferShare> {

    public OfferShareListAdapter(Context context) {
        super(context, null, R.layout.adapter_offer_share_list_item);
    }

    @Override
    public void setViewData(ViewHolder holder, OfferShare data) {
        holder.setText(R.id.tv_name, data.getCompanyName());
        holder.setText(R.id.tv_total_price, StringUtil.subZeroAndDot(data.getPrice()));
        holder.setText(R.id.tv_tuiguangfei_price, StringUtil.subZeroAndDot(data.getMyPrice()));
        holder.setBackgroundResource(R.id.btn_checked, data.isSelect() ? R.drawable.ic_converage_press : R.drawable.ic_converage);
        holder.getView(R.id.layout_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                data.setSelect(!data.isSelect());
                SoftInputUtil.hideSoftInput((Activity) mContext);
                notifyDataSetChanged();
            }
        });
        updatePirce(data, holder);
        ((EditText) holder.getView(R.id.edit_syx_zhekou)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //算保费
                if (!TextUtils.isEmpty(charSequence.toString())) {
                    int zhekou = Integer.valueOf(charSequence.toString());
                    if (zhekou > 100) {
                        zhekou = 100;
                        ToastUtil.show(R.string.offer_zhekou_max_tip);
                        ((EditText) holder.getView(R.id.edit_syx_zhekou)).getText().delete(charSequence.length() - 1, charSequence.length());
                    }
                    data.setSyxZhekou(zhekou);
                    updatePirce(data, holder);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ((EditText) holder.getView(R.id.edit_jqx_zhekou)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //算保费
                if (!TextUtils.isEmpty(charSequence.toString())) {
                    int zhekou = Integer.valueOf(charSequence.toString());
                    data.setJqxZhekou(zhekou);
                    updatePirce(data, holder);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void updatePirce(OfferShare data, ViewHolder holder) {
        try {
            double total = data.getTotalPrice().getBusinessTotalPrice() * data.getSyxZhekou() / 100
                    + data.getTotalPrice().getVehiclePrice()
                    * data.getJqxZhekou() / 100 + data.getTotalPrice().getTaxPrice();
            holder.setText(R.id.tv_total_price, total + "");
            data.setPrice(total);
            data.setMyPrice(data.getTotalPrice().getTaxPrice() + data.getTotalPrice().getVehiclePrice()
                    + data.getTotalPrice().getBusinessTotalPrice() - total);
            holder.setText(R.id.tv_total_price, StringUtil.subZeroAndDot(data.getPrice()));
            holder.setText(R.id.tv_tuiguangfei_price, StringUtil.subZeroAndDot(data.getMyPrice()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
