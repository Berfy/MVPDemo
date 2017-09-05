package com.wlb.agent.ui.user.frag;

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
import com.wlb.agent.core.data.H5;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.AuthStatus;
import com.wlb.agent.core.data.user.entity.IdAuthInfo;
import com.wlb.agent.core.data.user.entity.WalletInfo;
import com.wlb.agent.core.data.user.response.UserResponse;
import com.wlb.agent.core.data.user.response.WalletResponse;
import com.wlb.agent.ui.common.WebViewFrag;
import com.wlb.agent.ui.order.helper.InsuranceUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;
import com.wlb.common.SimpleFragAct.SimpleFragParam;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 钱包汇总信息
 *
 * @author caozechen
 */
public class WalletSummaryFrag extends SimpleFrag implements View.OnClickListener {

    private Task getWalletTask;
    private WalletInfo walletInfo;
    private SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.user_bankCard)
    TextView tv_bankCard;//我的银行卡
    @BindView(R.id.user_currenBalance)
    TextView tv_currenBalance;//账户余额
    @BindView(R.id.user_totalEarn)
    TextView tv_totalEarn;//总计佣金
    @BindView(R.id.user_canDrawMoney)
    TextView tv_canDrawMoney;//当前可提现佣金
    @BindView(R.id.user_processingMoney)
    TextView tv_processingMoney;//处理中佣金

    public static SimpleFragParam getStartParam() {
        return new SimpleFragParam(R.string.usercenter_wallet,
                WalletSummaryFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.wallet_sumary_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        getTitleBar().setRightText("账单");
        TextView mRightTxtView = getTitleBar().mRightTxtView;
        getTitleBar().setCompoundDrawable(mRightTxtView, R.drawable.flow_icon, 4);
        mRightTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WalletFlowFrag.start(mContext);
            }
        });
        hideTitleBarShdow();

        swipeRefreshLayout = findViewById(R.id.wallet_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //执行网络请求
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    ToastUtil.show(R.string.net_noconnection);
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                doGetWallet();
            }
        });
    }

    private void setData(WalletInfo info) {
        if (null != info) {
            tv_currenBalance.setText(InsuranceUtil.buildPrice(info.curren_balance));
            tv_totalEarn.setText(InsuranceUtil.buildPricePoint(info.total_earn));
            tv_canDrawMoney.setText(InsuranceUtil.buildPricePoint(info.available_money));
            tv_processingMoney.setText(InsuranceUtil.buildPricePoint(info.processing_money));

            tv_bankCard.setText(info.bind_bank_count + "");//绑定的银行卡张数
        }
    }

    private boolean isGetWallet;

    private void doGetWallet() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (isGetWallet) return;
        getWalletTask = UserClient.doGetWallet(new ICallback() {
            @Override
            public void success(Object data) {
                WalletResponse response = (WalletResponse) data;
                if (response.isSuccessful()) {
                    walletInfo = response.walletSummary;
                    setData(walletInfo);
                } else {
                    ToastUtil.show(response.msg);
                }
            }

            @Override
            public void start() {
                isGetWallet = true;
            }

            @Override
            public void failure(NetException e) {
                showToastLong(R.string.req_fail);
            }

            @Override
            public void end() {
                isGetWallet = false;
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        doGetWallet();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != getWalletTask) {
            getWalletTask.stop();
        }
    }

    @OnClick({R.id.wallet_query, R.id.user_withdraw, R.id.bank_card_banner})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_withdraw:
                if (null == walletInfo) {
                    ToastUtil.show("请求无数据");
                    return;
                }
                UserResponse userInfo = UserClient.getLoginedUser();
                IdAuthInfo id_auth_info = userInfo.id_auth_info;
                if (null != id_auth_info && id_auth_info.getAuthStatus() != AuthStatus.AUTH_SUCCESS) {
                    ToastUtil.show(R.string.user_auth_tip);
                    return;
                }
                WalletWithdrawFrag.start(mContext, walletInfo);
                break;
            case R.id.wallet_query://钱包解释说明
                WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                webViewParam.title = getString(R.string.wallet_help);
                webViewParam.url = H5.WALLET_RULE;
                WebViewFrag.start(mContext, webViewParam);
                break;
            case R.id.bank_card_banner://添加银行卡
                BankCardListFrag.start(mContext);
                return;


        }
    }
}
