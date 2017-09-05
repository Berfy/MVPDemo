package com.wlb.agent.ui.team.frag;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.group.GroupClient;
import com.wlb.agent.core.data.group.entity.TeamInsuranceOrder;
import com.wlb.agent.core.data.group.response.TeamInsuranceOrderResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.team.adapter.TeamInsuranceOrderListAdapter;
import com.wlb.agent.ui.team.helper.TeamMemberOrder;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import common.widget.LoadingBar;
import common.widget.dialog.loading.LoadingDialog;
import common.widget.listview.material.SwipeRefreshListView;

/**
 * Created by JiaHongYa
 * <p>
 * 待出单 催单
 */

public class TeamOrderListFrag extends SimpleFrag implements View.OnClickListener, TeamMemberOrder {

    @BindView(R.id.listview)
    SwipeRefreshListView mRefreshListView;
    @BindView(R.id.loadingBar)
    LoadingBar loadingBar;
    private static final String MEMBERUSERID = "member_user_id";
    private String memberUserId;
    private Task doReminderOrderTask;
    private Task doTeamOrderListTask;
    private List<TeamInsuranceOrder> orderList;
    private TeamInsuranceOrderListAdapter mAdapter;
    private boolean showLoadingBar = true;

    public static void start(Context context, String memberUserId) {
        Bundle bundle = new Bundle();
        bundle.putString(MEMBERUSERID, memberUserId);
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam("待出单", TeamOrderListFrag.class, bundle);
        SimpleFragAct.start(context, param);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.team_insurance_order_list_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (null == UserClient.getLoginedUser()) {
            ToastUtil.show("请先登录");
            close();
            return;
        }
        memberUserId = getArguments().getString(MEMBERUSERID);
        getTitleBar().setRightText("全部催单");
        TextView mRightText = getTitleBar().mRightTxtView;
        mRightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != orderList) {
                    if (orderList.size() == 0) {
                        ToastUtil.show("你的团队还没有订单哦！");
                        return;
                    } else {
                        reminderOrder("0");
                    }
                }

            }
        });
        mRefreshListView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    ToastUtil.show(R.string.net_noconnection);
                    mRefreshListView.setRefreshing(false);
                } else {
                    doGetTeamOrderList(memberUserId);
                }
            }
        });

        mRefreshListView.setDividerWithHeight(R.drawable.transparent, 10, true);
        doGetTeamOrderList(memberUserId);
    }

    private boolean isTeamOrderList;

    private void doGetTeamOrderList(String memberUserId) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            if (showLoadingBar) loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.NOCONNECTION);
            return;
        }
        if (isTeamOrderList) {
            return;
        }
        if (showLoadingBar) loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.START);
        doTeamOrderListTask = GroupClient.doGetTeamMemberOrder(memberUserId, new ICallback() {
            @Override
            public void start() {
                isTeamOrderList = true;
            }

            @Override
            public void success(Object data) {
                TeamInsuranceOrderResponse response = (TeamInsuranceOrderResponse) data;
                if (response.isSuccessful()) {
                    orderList = response.list;
                    setView(orderList);
                    if (orderList.isEmpty()) {
                        loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.EMPTY);
                    } else {
                        if (showLoadingBar)
                            loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.SUCCESS);
                        showLoadingBar = false;
                    }

                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                loadFail();
            }

            @Override
            public void end() {
                isTeamOrderList = false;
                mRefreshListView.setRefreshing(false);
            }
        });
    }

    private void loadFail() {
        showToastShort("获取失败");
        loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.RELOAD);
    }


    private boolean isReminderOrder;

    private void reminderOrder(String orderId) {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        if (isReminderOrder) {
            return;
        }
        final LoadingDialog dialog = LoadingDialog.showCancelableDialog(mContext, "催单中...");
        doReminderOrderTask = GroupClient.doTeamMemberReminder(orderId, new ICallback() {
            @Override
            public void start() {
                isReminderOrder = true;
            }

            @Override
            public void success(Object data) {
                BaseResponse response = (BaseResponse) data;
                if (response.isSuccessful()) {
                    ToastUtil.show("催单成功");
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void failure(NetException e) {
                ToastUtil.show(R.string.req_fail);
            }

            @Override
            public void end() {
                isReminderOrder = false;
                dialog.dissmiss();
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != doReminderOrderTask) {
            doReminderOrderTask.stop();
        }
        if (null != doTeamOrderListTask) {
            doTeamOrderListTask.stop();
        }
    }

    public void setView(List<TeamInsuranceOrder> orderListData) {
        mAdapter = new TeamInsuranceOrderListAdapter(mContext, orderListData, R.layout.team_insurance_order_list_item, this);
        mRefreshListView.setAdapter(mAdapter);
    }

    @OnClick(R.id.loadingBar)
    public void onClick(View v) {
        if (v == loadingBar) {
            doGetTeamOrderList(memberUserId);
        }
    }

    @Override
    public void setTeamMemberOrder(TeamInsuranceOrder teamInsurance) {
        reminderOrder(teamInsurance.order_no);
    }
}
