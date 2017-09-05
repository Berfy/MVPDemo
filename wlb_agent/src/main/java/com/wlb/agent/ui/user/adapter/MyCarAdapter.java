package com.wlb.agent.ui.user.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.wlb.agent.R;
import com.wlb.agent.core.data.user.entity.Ivehicle;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * 我的车辆列表Adapter
 *
 * @author 张全
 */
public class MyCarAdapter extends ListAdapter<Ivehicle> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
            Locale.getDefault());

    public MyCarAdapter(Context context, List<Ivehicle> list, int layoutId) {
        super(context, list, layoutId);
    }

    @Override
    public void setViewData(ViewHolder holder, Ivehicle carInfo) {
        //车主
        holder.setText(R.id.item_owner, carInfo.owner);
        //车牌号
        holder.setText(R.id.item_licenceNo, carInfo.licenseNo);

        holder.setText(R.id.item_showText, carInfo.showText);
        //车型
        if (null != carInfo.model) {
            holder.setText(R.id.item_carModel, carInfo.model.vehicleSeries);
        }
        View phoneCall = holder.getView(R.id.item_phoneCall);
        if (TextUtils.isEmpty(carInfo.phoneNo)) {
            phoneCall.setVisibility(View.GONE);
        } else {
            phoneCall.setVisibility(View.VISIBLE);
            phoneCall.setTag(carInfo.phoneNo);
            phoneCall.setOnClickListener(onClickListener);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            String phone = (String) v.getTag();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            getContext().startActivity(intent);
        }
    };
}
