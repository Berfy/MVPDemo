package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.util.ext.ToastUtil;
import com.android.util.http.callback.ICallback;
import com.android.util.http.exception.NetException;
import com.android.util.http.task.Task;
import com.android.util.os.NetworkUtil;
import com.wlb.agent.R;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.CommissionDetailInfo;
import com.wlb.agent.core.data.user.response.WalletFlowDetailResponse;
import com.wlb.agent.ui.user.util.UserUtil;
import com.wlb.common.SimpleFrag;
import com.wlb.common.SimpleFragAct;

import java.text.SimpleDateFormat;
import java.util.Locale;

import common.widget.LoadingBar;

/**
 * 钱包流水详情
 *
 * @author caozechen
 */
public class WalletFlowDetailFrag extends SimpleFrag {

    private static final String BILLID = "billid";
    private Task flowDetailTask;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private LoadingBar loadingBar;
    private long billId;
    private WalletFlowDetailResponse response;

    public static SimpleFragAct.SimpleFragParam getStartParam(long billId) {
        Bundle bundle = new Bundle();
        bundle.putLong(BILLID, billId);
        SimpleFragAct.SimpleFragParam param = new SimpleFragAct.SimpleFragParam("账单详情",
                WalletFlowDetailFrag.class, bundle);
        return param;
    }

    public static void start(Context context, long billId) {
        SimpleFragAct.start(context, getStartParam(billId));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.wallet_detail_frag;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        getTitleBar().setBackgroundColor(Color.WHITE);
        getActivity().getWindow().setBackgroundDrawable(null);

        billId=getArguments().getLong(BILLID);

        loadingBar = findViewById(R.id.loadingBar);
        loadingBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetworkAvailable(mContext)) {
                    ToastUtil.show(R.string.net_noconnection);
                    loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.NOCONNECTION);
                    return;
                }
                GetFlowDetail();
            }
        });
        GetFlowDetail();
    }

    private void GetFlowDetail() {
        if (!NetworkUtil.isNetworkAvailable(mContext)) {
            ToastUtil.show(R.string.net_noconnection);
            return;
        }
        loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.START);
        flowDetailTask = UserClient.doGetWalletFlowDetail(billId, new ICallback() {
            @Override
            public void start() {
            }

            @Override
            public void success(Object data) {
                response = (WalletFlowDetailResponse) data;
                if (response.isSuccessful()) {
                    flowSuccess(response);
                    loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.SUCCESS);
                } else {
                    loadFail();
                }
            }

            @Override
            public void failure(NetException e) {
                loadFail();
            }

            @Override
            public void end() {
            }
        });
    }

    private void loadFail() {
        loadingBar.setLoadingStatus(LoadingBar.LoadingStatus.RELOAD);
    }

    private void flowSuccess(WalletFlowDetailResponse info) {

        TextView tv_money = findViewById(R.id.flow_detail_money);
        //交易金额
        if (1 == info.billType) {
            tv_money.setText("+ " + info.amount);
           /* if (info.category == 1) {
                //佣金收入(税后)
                findViewById(R.id.tax).setVisibility(View.VISIBLE);
            }*/
        } else if (2 == info.billType) {
            tv_money.setText("- " + info.amount);
        }

        if (info.category == 1) { //佣金
            LinearLayout addLayout = findViewById(R.id.add_wallet);
            addLayout.setVisibility(View.VISIBLE);

            CommissionDetailInfo commissionInfo = info.detail;
            TextView tv_policyNo = findViewById(R.id.flow_detail_policyNo);//保单号
            tv_policyNo.setText(commissionInfo.policyNo);
            TextView tv_licenseNo = findViewById(R.id.flow_detail_licenseNo);//车牌号
            tv_licenseNo.setText(commissionInfo.licenseNo);
            TextView tv_insurant = findViewById(R.id.flow_detail_insurant);//被保人
            tv_insurant.setText(commissionInfo.insurant);
            TextView tv_commission = findViewById(R.id.flow_detail_commission);//佣金
            tv_commission.setText(String.valueOf(commissionInfo.commission));
            TextView tv_comState = findViewById(R.id.flow_detail_commission_state);//佣金状态
            tv_comState.setText(info.statusText);

            TextView tv_successTime = findViewById(R.id.flow_detail_addTime);
            tv_successTime.setText(format.format(info.timestamp));

        } else if (info.category == 2) { //提现
            LinearLayout addLayout = findViewById(R.id.reduce_wallet);
            addLayout.setVisibility(View.VISIBLE);

            TextView tv_cardNo = findViewById(R.id.flow_detail_card);//银行卡号
            tv_cardNo.setText(UserUtil.getEncodedCard(info.bankCrad));

            TextView tv_type = findViewById(R.id.flow_detail_type);
            if (1 == info.category) {
                tv_type.setText("佣金");
            } else if (2 == info.category) {
                tv_type.setText("提现");
            }
            TextView tv_state = findViewById(R.id.flow_detail_state);//状态
            tv_state.setText(info.statusText);

            TextView tv_failTime = findViewById(R.id.flow_detail_reduceTime);
            tv_failTime.setText(format.format(info.timestamp));
        } else {

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != flowDetailTask) {
            flowDetailTask.stop();
        }
    }
}
