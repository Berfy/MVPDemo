package com.wlb.agent.ui.user.adapter;

import android.content.Context;
import android.view.View;

import com.wlb.agent.R;
import com.wlb.agent.core.data.offer.entity.OfferRecord;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * Created by Berfy on 2017/7/24.
 */

public class UserOfferRecordListAdapter extends ListAdapter<OfferRecord> {

    private OnLookListener mOnLookListener;

    public UserOfferRecordListAdapter(Context context, OnLookListener onLookListener) {
        super(context, null, R.layout.adapter_user_offer_record_list_item);
        mOnLookListener = onLookListener;
    }

    @Override
    public void setViewData(ViewHolder holder, OfferRecord data) {
        holder.getView(R.id.btn_look).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mOnLookListener) {
                    mOnLookListener.look(data);
                }
            }
        });
    }

    public interface OnLookListener {
        void look(OfferRecord offerRecord);
    }
}
