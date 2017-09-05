package com.wlb.agent.ui.offer.adapter;

import android.content.Context;
import android.view.View;

import com.wlb.agent.R;
import com.wlb.agent.core.data.offer.entity.InsurerConverage;
import com.wlb.agent.core.data.offer.entity.Offer;

import java.util.ArrayList;
import java.util.List;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;
import common.widget.listview.ScrollListView;

/**
 * 报价
 * Created by Berfy on 2017/7/12.
 */
public class OfferListAdapter extends ListAdapter<Offer> {

    private OnBuyListener mOnBuyListener;

    public OfferListAdapter(Context context, OnBuyListener onBuyListener) {
        super(context, null, R.layout.adapter_offer_list_item);
        mOnBuyListener = onBuyListener;
    }

    @Override
    public void setViewData(ViewHolder holder, Offer data) {
        holder.setText(R.id.tv_old_price, data.getOldPrice());
        holder.setText(R.id.tv_price, data.getPrice());
        holder.setText(R.id.tv_name, data.getName());
        if (data.getName().equals(mContext.getString(R.string.offer_pingan))) {
            holder.setBackgroundResource(R.id.v_icon, R.drawable.ic_pingan);
        } else if (data.getName().equals(mContext.getString(R.string.offer_taipingyang))) {
            holder.setBackgroundResource(R.id.v_icon, R.drawable.ic_taipingyang);
        } else if (data.getName().equals(mContext.getString(R.string.offer_renmin))) {
            holder.setBackgroundResource(R.id.v_icon, R.drawable.ic_renbao);
        }

        OfferListSyxAdapter adapter = new OfferListSyxAdapter(mContext);
        ((ScrollListView)holder.getView(R.id.listView)).setAdapter(adapter);
        List<InsurerConverage> converages = new ArrayList<>();
        converages.add(new InsurerConverage("1", mContext.getString(R.string.csx_short), "100000", "1500", false, false, false));
        converages.add(new InsurerConverage("1", mContext.getString(R.string.dszx_short), "100000", "1000", false, false, false));
        converages.add(new InsurerConverage("1", mContext.getString(R.string.csx_b), "100", "10", false, false, false));
        converages.add(new InsurerConverage("1", mContext.getString(R.string.dszx_b), "100", "10", false, false, false));
        adapter.refresh(converages);
        holder.getView(R.id.btn_price_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.setVisibility(R.id.layout_detail, holder.getView(R.id.layout_detail).getVisibility()
                        == View.VISIBLE ? View.GONE : View.VISIBLE);


            }
        });
        holder.getView(R.id.btn_buy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnBuyListener) {
                    mOnBuyListener.buy(data);
                }
            }
        });
    }

    public interface OnBuyListener {
        void buy(Offer offer);
    }

}
