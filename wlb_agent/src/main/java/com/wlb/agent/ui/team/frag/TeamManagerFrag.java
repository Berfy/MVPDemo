package com.wlb.agent.ui.team.frag;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.util.ext.SPUtil;
import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.Constant;
import com.wlb.agent.R;
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.group.GroupClient;
import com.wlb.agent.core.data.group.response.TeamSummaryResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.ui.common.PushEvent;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.order.helper.InsuranceUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 团队管理
 * <p>
 * Created by 曹泽琛.
 */

public class TeamManagerFrag extends SimpleFrag {

    @BindView(R.id.team_change_name)
    TextView tvTeamName;//团队姓名
    @BindView(R.id.team_manager)
    TextView tvTeamManager;//团长姓名
    @BindView(R.id.team_people_day)
    TextView tvPeopleDay;//今日新增
    @BindView(R.id.team_people_total)
    TextView tvPeopleTotal;//合计
    @BindView(R.id.team_wallet_day)
    TextView tvWalletDay;//今日佣金
    @BindView(R.id.team_wallet_day_contribute)
    TextView tvWalletDayContribute;//今日佣金贡献
    @BindView(R.id.team_wallet_total)
    TextView tvWalletToatal;//合计佣金
    @BindView(R.id.team_wallet_total_contribute)
    TextView tvWalletToatalContribute;//合计佣金贡献
    @BindView(R.id.team_pay_order)
    TextView tvPayOrder;//订单个数
    @BindView(R.id.team_refresh)
    SwipeRefreshLayout teamRefresh;
    @BindView(R.id.team_air_layout)
    View teamAir;
    private Task getTeamSummaryTask;
    private static final String INVITE_TIP = "invite_tip";

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam("团队管理",
                TeamManagerFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.team_manager_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        if (!SPUtil.getBoolean(INVITE_TIP)) {
            teamAir.setVisibility(View.VISIBLE);
        }
        SPUtil.setBoolean(INVITE_TIP, true);
        if (null == UserClient.getLoginedUser()) {
            ToastUtil.show("请先登录");
            close();
            return;
        }
        getTitleBar().setRightBtnDrawable(R.drawable.team_invite);
        getTitleBar().setOnRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamAir.setVisibility(View.GONE);
                WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.title = "邀请好友";
                webViewParam.url = H5.INVITE_NEW;
                WebViewFrag.start(mContext, webViewParam);
            }
        });
        /*TeamSummaryResponse teamSummary = GroupClient.getTeamSummaruInfo();
        if (null != teamSummary) {
            setView(teamSummary);
        }*/
        doGetTeamSummary();
        teamRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    ToastUtil.show(R.string.net_noconnection);
                    teamRefresh.setRefreshing(false);
                    return;
                }
                doGetTeamSummary();
            }
        });
        addAction(Constant.IntentAction.TEAM_NAME);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        String action = event.getAction();
        if (containsAction(action)) {
            doGetTeamSummary();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != getTeamSummaryTask) {
            getTeamSummaryTask.stop();
        }
    }

    @OnClick({R.id.team_name_banner, R.id.team_people, R.id.team_wallet, R.id.team_pay, R.id.team_air_layout})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.team_name_banner://修改团队名称
                TeamNameFrag.start(mContext);
                break;
            case R.id.team_people://查看团队团员
                WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.title = "队员";
                webViewParam.url = H5.MEMBER_LIST;
                SimpleFragAct.SimpleFragParam startParam = WebViewFrag.getStartParam(webViewParam);
                startParam.coverTilteBar(true);
                WebViewFrag.start(mContext, startParam);
                break;
            case R.id.team_wallet://查看团队业绩
                webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.title = "团队业绩";
                webViewParam.url = H5.PERFORMNCE_LIST;
                startParam = WebViewFrag.getStartParam(webViewParam);
                startParam.coverTilteBar(true);
                WebViewFrag.start(mContext, startParam);
                break;
            case R.id.team_pay://催单
                TeamOrderListFrag.start(mContext, "0");
                break;
            case R.id.team_air_layout://帮助遮罩层
                teamAir.setVisibility(View.GONE);
                break;
        }
    }

    private void setView(TeamSummaryResponse teamSummary) {
        if (null != teamSummary) {
            if (!TextUtils.isEmpty(teamSummary.name)) {
                tvTeamName.setText(teamSummary.name);
                tvTeamName.setTextColor(getResources().getColor(R.color.c_393939));
            }
            tvTeamManager.setText(teamSummary.leader_nick);
            tvPeopleDay.setText(String.valueOf(teamSummary.member.statistic_daily));
            tvPeopleTotal.setText(String.valueOf(teamSummary.member.statistic_all));
            tvWalletDay.setText(InsuranceUtil.buildPrice(teamSummary.achievement.commission_daily));
            tvWalletDayContribute.setText(InsuranceUtil.buildPrice(teamSummary.achievement.dedicate_daily));
            tvWalletToatal.setText(InsuranceUtil.buildPrice(teamSummary.achievement.commission_total));
            tvWalletToatalContribute.setText(InsuranceUtil.buildPrice(teamSummary.achievement.dedicate_total));
            tvPayOrder.setText(teamSummary.order_payable + " 个订单");
        }
    }

    private boolean isGetTeamSummary;

    private void doGetTeamSummary() {
        if (isGetTeamSummary) {
            return;
        }
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            showToastShort(R.string.net_noconnection);
            teamRefresh.setRefreshing(false);
            return;
        }

        getTeamSummaryTask = GroupClient.doGetTeamSummary(new ICallback() {
            @Override
            public void start() {
                isGetTeamSummary = true;
            }

            @Override
            public void success(Object data) {
                TeamSummaryResponse response = (TeamSummaryResponse) data;
                if (response.isSuccessful()) {
                    setView(response);
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
                isGetTeamSummary = false;
                teamRefresh.setRefreshing(false);
            }
        });
    }
}
