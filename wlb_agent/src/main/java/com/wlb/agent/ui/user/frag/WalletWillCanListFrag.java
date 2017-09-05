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
import com.wlb.agent.core.data.user.response.WalletWillCanResponse;
import com.wlb.agent.ui.common.CommonListFrag;
import com.wlb.agent.ui.user.adapter.WalletWillCanListAdapter;
import com.wlb.agent.util.PopupWindowUtil;

import java.util.List;

import common.widget.adapter.ListAdapter;

/**
 * 待审核金额列表
 *
 * @author Berfy
 */
public class WalletWillCanListFrag extends CommonListFrag implements View.OnClickListener {

    private int mPosition;
    private PopupWindowUtil mPopupWindowUtil;

    public WalletWillCanListFrag() {
    }

    @Override
    protected void initParams(Bundle savedInstanceState) {
        mPopupWindowUtil = new PopupWindowUtil(mContext);
        if (null != getArguments()) {
            mPosition = getArguments().getInt("position");
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
        return UserClient.getWalletWillCan(lastId, mPosition, callback);
    }

    @Override
    protected ListAdapter initAdapterWithData(List mDataList) {
        WalletWillCanListAdapter adapter = new WalletWillCanListAdapter(getContext());
        adapter.refresh(mDataList);
        return adapter;
    }

    @Override
    protected ListData getDataListFromResponse(BaseResponse baseResponse) {
        WalletWillCanResponse response = (WalletWillCanResponse) baseResponse;
        List<WalletFlowInfo> walletWillCans = response.list;
        long lastId = 0;
        if (!walletWillCans.isEmpty()) {
            lastId = walletWillCans.get(walletWillCans.size() - 1).billId;
        }
        return new ListData(walletWillCans, lastId);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}