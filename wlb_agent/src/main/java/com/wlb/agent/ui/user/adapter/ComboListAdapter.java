package com.wlb.agent.ui.user.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import com.wlb.agent.R;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.core.data.combo.response.ComboResponse.ComboEntity;

import java.text.SimpleDateFormat;
import java.util.List;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * 套餐列表
 * Created by JiaHongYa
 */

public class ComboListAdapter extends ListAdapter<ComboEntity> {
    private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    public ComboListAdapter(Context context, List<ComboEntity> list, int layoutId) {
        super(context, list, layoutId);
    }

    @Override
    public void setViewData(ViewHolder holder, final ComboEntity data) {
        holder.setText(R.id.item_realAmount, "￥" + data.getPackageAmount());
        holder.setText(R.id.item_originAmount, "原价" + data.getOriginAmount());
        holder.getView(R.id.item_detail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(data.getUrl())){
                    WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                    webViewParam.url=data.getUrl();
                    WebViewFrag.start(v.getContext(),webViewParam);
                }
            }
        });
        holder.setText(R.id.item_name,data.getPackageName());
        holder.setText(R.id.item_rule,"规则："+data.getPackageRegular());
        holder.setText(R.id.item_endTime,"有效期至："+dateFormat.format(data.getPackageEnd()));
        boolean selected=data.isSelect==1;
        holder.setVisibility(R.id.item_selected,selected?View.VISIBLE:View.GONE);
        if(!TextUtils.isEmpty(data.getServiceCall())){
            holder.setVisibility(R.id.item_serviceCall,View.VISIBLE);
            holder.setTag(R.id.item_serviceCall,data.getServiceCall());
            holder.getView(R.id.item_serviceCall).setOnClickListener(onClickListener);
        }else{
            holder.setVisibility(R.id.item_serviceCall,View.GONE);
        }
    }

    View.OnClickListener onClickListener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            String phone = (String) v.getTag();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            getContext().startActivity(intent);
        }
    };
}
