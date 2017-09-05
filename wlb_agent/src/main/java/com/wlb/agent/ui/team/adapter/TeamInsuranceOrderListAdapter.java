package com.wlb.agent.ui.team.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.wlb.agent.R;
import com.wlb.agent.core.data.group.entity.TeamInsuranceOrder;
import com.wlb.agent.ui.order.helper.InsuranceUtil;
import com.wlb.agent.ui.team.helper.TeamMemberOrder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import common.widget.adapter.ListAdapter;
import common.widget.adapter.ViewHolder;

/**
 * Created by JiaHongYa
 */

public class TeamInsuranceOrderListAdapter extends ListAdapter<TeamInsuranceOrder> {
    private TeamMemberOrder teamInsuranceOrder;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public TeamInsuranceOrderListAdapter(Context context, List<TeamInsuranceOrder> list, int layoutId, TeamMemberOrder teamOrder) {
        super(context, list, layoutId);
        this.teamInsuranceOrder=teamOrder;
    }

    @Override
    public void setViewData(ViewHolder holder, TeamInsuranceOrder orderData) {
        String content = orderData.owner + " - " + orderData.license_no;
        holder.setText(R.id.carName_license, content);//车牌号+车主姓名

        ImageView compnayIconView = holder.getView(R.id.company_icon);//保险公司图标
        InsuranceUtil.setImageOption(orderData.company_code,compnayIconView);

        if (orderData.traffic_premium > 0) {  //交强险总保费
            holder.setText(R.id.compulsory_cost, InsuranceUtil.buildYuan(orderData.traffic_premium));
        } else {
            holder.setText(R.id.compulsory_cost, "0.00元");
        }
        if (orderData.bussiness_premium > 0) {//商业险总保费
            holder.setText(R.id.commerce_cost, InsuranceUtil.buildYuan(orderData.bussiness_premium));
        } else {
            holder.setText(R.id.commerce_cost, "0.00元");
        }

        if (orderData.order_time > 0) {//下单时间
            holder.setText(R.id.enrollDate, "下单时间： "+dateFormat.format(orderData.order_time));
        }else{
            holder.setText(R.id.enrollDate, "下单时间： ");
        }
        //总计金额
        String totals = InsuranceUtil.buildYuan(orderData.total_premium);
        holder.setText(R.id.total_out, totals);

        holder.getView(R.id.reminder_but).setTag(orderData);
        holder.getView(R.id.reminder_but).setOnClickListener(onClickListener);
    }
    View.OnClickListener onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            TeamInsuranceOrder orderData=(TeamInsuranceOrder)v.getTag();
               switch (v.getId()){
                   case R.id.reminder_but:
                       teamInsuranceOrder.setTeamMemberOrder(orderData);
                       break;
               }
        }

    };
}
