package com.wlb.agent.ui.user.frag;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.android.util.http.callback.ICallback;
import com.android.util.http.task.Task;
import com.wlb.agent.R;
import com.wlb.agent.ui.common.CommonListFrag;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.WalletFlowInfo;
import com.wlb.agent.core.data.user.response.WalletFlowResponse;
import com.wlb.agent.ui.user.adapter.WalletFlowListAdapter;
import com.wlb.common.SimpleFragAct;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import common.widget.adapter.ListAdapter;

/**
 * 钱包流水
 *
 * @author 张全
 */
public class WalletFlowFrag extends CommonListFrag {

    public static SimpleFragAct.SimpleFragParam getStartParam() {
        return new SimpleFragAct.SimpleFragParam("我的账单",
                WalletFlowFrag.class);
    }

    public static void start(Context context) {
        SimpleFragAct.start(context, getStartParam());
    }


    @Override
    protected void initParams(Bundle savedInstanceState) {
        setBackgroundColor(getColor(R.color.c_e9e9e9));
        setDividerHeight(0);
    }

    @Override
    protected Task executeTask(long lastId, ICallback callback) {
        return UserClient.doGetWalletFlowList(lastId, 0, getPageCount(), callback);
    }

    @Override
    protected ListAdapter initAdapterWithData(List mDataList) {
        return new WalletFlowListAdapter(mContext, mDataList, R.layout.wallet_flow_list_item);
    }

    @Override
    protected ListData getDataListFromResponse(BaseResponse baseResponse) {
        WalletFlowResponse response = (WalletFlowResponse) baseResponse;
        List<WalletFlowInfo> list = response.list;
        long lastId = 0;
        if (!list.isEmpty()) {
            lastId = list.get(list.size() - 1).billId;
        }
        List<WalletFlowWrapper> walletFlowWrappers = constructData(list);
        return new ListData(walletFlowWrappers, lastId);
    }

    @Override
    protected void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        WalletFlowWrapper fllowInfoWrapper = (WalletFlowWrapper) getAdapter().getList().get(position);
        WalletFlowDetailFrag.start(mContext, fllowInfoWrapper.flowInfo.billId);
    }

    private List<WalletFlowWrapper> constructData(List<WalletFlowInfo> dataList) {
        if (dataList.isEmpty()) {
            return new ArrayList<WalletFlowWrapper>();
        }

        ListAdapter adapter = getAdapter();
        WalletFlowWrapper lastFlowWrapper = null;
        if (null != adapter) {
            List<WalletFlowWrapper> list = adapter.getList();
            if (!list.isEmpty()) {
                lastFlowWrapper = list.get(list.size() - 1);
            }
        }
        List<WalletFlowWrapper> walletFlowWrappers = new ArrayList<>();
        String lastYearMonth = null;
        if (null != lastFlowWrapper) {
            lastYearMonth = lastFlowWrapper.yearMonth;
        }
        for (int i = 0; i < dataList.size(); i++) {
            WalletFlowInfo walletFlowInfo = dataList.get(i);
            String yearMonth = yearFormat.format(new Date(walletFlowInfo.timestamp));
            WalletFlowWrapper walletFlowWrapper = new WalletFlowWrapper(yearMonth, walletFlowInfo);
            if (!yearMonth.equals(lastYearMonth)) {
                walletFlowWrapper.showYearMonth = true;
            }
            lastYearMonth = yearMonth;
            walletFlowWrappers.add(walletFlowWrapper);
        }
        return walletFlowWrappers;
    }

    public static class WalletFlowWrapper {
        public String yearMonth;
        public WalletFlowInfo flowInfo;
        public boolean showYearMonth;

        public WalletFlowWrapper(String yearMonth, WalletFlowInfo flowInfo) {
            this.yearMonth = yearMonth;
            this.flowInfo = flowInfo;
        }
    }

    private SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());

}
