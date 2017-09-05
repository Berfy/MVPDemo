package com.wlb.agent.ui.user.frag;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.android.util.http.callback.ICallback;
import com.android.util.http.task.Task;
import com.wlb.agent.R;
import com.wlb.agent.core.data.base.BaseResponse;
import com.wlb.agent.core.data.user.UserClient;
import com.wlb.agent.core.data.user.entity.WalletFlowInfo;
import com.wlb.agent.core.data.user.response.WalletFlowResponse;
import com.wlb.agent.ui.common.CommonListFrag;
import com.wlb.agent.ui.user.adapter.WalletDetailListAdapter;
import com.wlb.agent.util.PopupWindowUtil;

import java.util.List;

import common.widget.adapter.ListAdapter;

/**
 * 收支明细列表
 *
 * @author Berfy
 */
public class WalletDetailListFrag extends CommonListFrag implements View.OnClickListener {

    private PopupWindowUtil mPopupWindowUtil;
    public static final String PARAM_STATUS = "status";
    private int mStatus;//10001推广费 20002消费 0全部

    @Override
    protected void initParams(Bundle savedInstanceState) {
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        if (null != getArguments()) {
            mStatus = getArguments().getInt(PARAM_STATUS);
        }
        setNoContentTip(R.drawable.ic_order_null, getString(R.string.no_content), null, 0, "");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    protected Task executeTask(long lastId, ICallback callback) {
//        if (lastId == 0) {
//            ToastUtil.show("位置" + mPosition);
//        }
//        WalletWillCanResponse walletWillCanResponse = new WalletWillCanResponse();
//        List<WalletWillCan> datas = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            String status = "";
//            switch (mPosition) {
//                case 0:
//                    status = "提现";
//                    break;
//                case 1:
//                    status = "推广";
//                    break;
//                case 2:
//                    status = "充值";
//                    break;
//                case 3:
//                    status = "提现";
//                    break;
//            }
//            WalletWillCan walletWillCan = new WalletWillCan("13131" + i, TimeUtil.getCurrentTime(), status, i % 2 == 0 ? 0 : 1, 100 + i);
//            datas.add(walletWillCan);
//        }
//        walletWillCanResponse.setData(datas);
//        callback.success(walletWillCanResponse);
//        callback.end();
//        return new Task(null, null);
        return UserClient.doGetWalletFlowList(lastId, mStatus, getPageCount(), callback);
    }

    @Override
    protected ListAdapter initAdapterWithData(List mDataList) {
        WalletDetailListAdapter adapter = new WalletDetailListAdapter(getContext());
        adapter.refresh(mDataList);
        return adapter;
    }

    @Override
    protected ListData getDataListFromResponse(BaseResponse baseResponse) {
        WalletFlowResponse response = (WalletFlowResponse) baseResponse;
        List<WalletFlowInfo> walletWillCans = response.list;
        if (!walletWillCans.isEmpty()) {

        }
        long lastId = walletWillCans.size();
        return new ListData(walletWillCans, lastId);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}